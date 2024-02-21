package com.example.userapi;

import com.example.userapi.dto.UserRequestDTO;
import com.example.userapi.entity.User;
import com.example.userapi.service.UserService;
import com.example.userapi.util.UserUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureWebTestClient
class UserControllerTests {

    @MockBean
    private UserService userService;

    @Autowired
    private WebTestClient webClient;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void getOk() throws Exception {
        User user = User.builder()
                .id(1L)
                .name("Ivan")
                .email("ivan@ya.ru")
                .build();

        Mono<User> monoUser = Mono.just(user);

        when(userService.get(1L)).thenReturn(monoUser);

        String jsonObj = objectMapper.writeValueAsString(user);

        webClient.get().uri("/users/{id}", 1L)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .json(jsonObj);
    }

    @Test
    public void getNotFound() throws Exception {
        when(userService.get(4L)).thenReturn(Mono.empty());

        webClient.get().uri("/users/{id}", 4)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.error").isEqualTo("Not Found");
    }

    @Test
    public void createOk() throws Exception {
        UserRequestDTO userRequestDTO = UserRequestDTO.builder()
                .name("Ivan")
                .email("ivan@ya.ru")
                .build();

        User user = UserUtils.toUser(userRequestDTO);
        user.setId(1L);

        Mono<User> monoUser = Mono.just(user);

        when(userService.create(any(UserRequestDTO.class))).thenReturn(monoUser);

        String jsonObj = objectMapper.writeValueAsString(user);

        webClient.post().uri("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(userRequestDTO)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .json(jsonObj);
    }

    @Test
    public void createNotUniqueEmail() throws Exception {
        when(userService.create(any(UserRequestDTO.class))).thenThrow(DuplicateKeyException.class);

        UserRequestDTO userRequestDTO_1 = UserRequestDTO.builder()
                .name("Ivanov")
                .email("ivan@ya.ru")
                .build();

        webClient.post().uri("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(userRequestDTO_1)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Not unique email");
    }

    @Test
    public void updateOk() throws Exception {
        User user = User.builder()
                .id(1L)
                .name("Ivanov")
                .email("ivan@gmail.com")
                .build();

        Mono<User> monoUser = Mono.just(user);

        when(userService.update(any(User.class))).thenReturn(monoUser);

        String jsonObj = objectMapper.writeValueAsString(user);

        webClient.put().uri("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(user)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .json(jsonObj);
    }

    @Test
    public void updateNotFound() throws Exception {
        User user = User.builder()
                .id(4L)
                .name("Ivanov")
                .email("ivan@gmail.com")
                .build();

        when(userService.update(any(User.class))).thenReturn(Mono.empty());

        webClient.put().uri("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(user)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.error").isEqualTo("Not Found");
    }

    @Test
    public void deleteOk() throws Exception {
        when(userService.delete(1L)).thenReturn(Mono.just(true));

        webClient.delete().uri("/users/{id}", 1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Deleted");
    }

    @Test
    public void deleteNotFound() throws Exception {
        when(userService.delete(4L)).thenReturn(Mono.just(false));
        webClient.delete().uri("/users/{id}", 4)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.message").isEqualTo("User not found");
    }

    @Test
    public void getAllOk() throws Exception {
        User user = User.builder()
                .id(1L)
                .name("Ivan")
                .email("ivan@ya.ru")
                .build();

        User user_1 = User.builder()
                .id(2L)
                .name("Petr")
                .email("petr@ya.ru")
                .build();

        List<User> users = List.of(user, user_1);

        when(userService.getAll()).thenReturn(Flux.fromIterable(users));

        String jsonObj = objectMapper.writeValueAsString(users);

        webClient.get().uri("/users")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .json(jsonObj);
    }
}
