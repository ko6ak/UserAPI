package com.example.userapi;

import com.example.userapi.dto.UserRequestDTO;
import com.example.userapi.entity.User;
import com.example.userapi.service.UserService;
import com.example.userapi.util.UserUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@Testcontainers
class UserIntegrationTests {

    @Autowired
    private UserService userService;

    @Autowired
    private WebTestClient webClient;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private Flyway flyway;

    @BeforeEach
    void setUp() {
        flyway.clean();
        flyway.migrate();
    }

    @Test
    public void getOk() throws Exception {
        UserRequestDTO userRequestDTO = UserRequestDTO.builder()
                .name("Ivan")
                .email("ivan@ya.ru")
                .build();

        User user = userService.create(userRequestDTO).block();
        assertThat(user).isNotNull();

        String jsonObj = objectMapper.writeValueAsString(user);

        webClient.get().uri("/users/{id}", user.getId())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .json(jsonObj);
    }

    @Test
    public void getNotFound() throws Exception {
        webClient.get().uri("/users/{id}", 7)
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
        UserRequestDTO userRequestDTO = UserRequestDTO.builder()
                .name("Ivan")
                .email("ivan@ya.ru")
                .build();

        User user = userService.create(userRequestDTO).block();
        assertThat(user).isNotNull();

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
        UserRequestDTO userRequestDTO = UserRequestDTO.builder()
                .name("Ivan")
                .email("ivan@ya.ru")
                .build();

        User user = userService.create(userRequestDTO).block();
        assertThat(user).isNotNull();

        User user_1 = User.builder()
                .id(1L)
                .name("Ivanov")
                .email("ivan@gmail.com")
                .build();

        String jsonObj = objectMapper.writeValueAsString(user_1);

        webClient.put().uri("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(user_1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .json(jsonObj);

        webClient.get().uri("/users/{id}", user.getId())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .json(jsonObj);
    }

    @Test
    public void updateNotFound() throws Exception {
        UserRequestDTO userRequestDTO = UserRequestDTO.builder()
                .name("Ivan")
                .email("ivan@ya.ru")
                .build();

        User user = userService.create(userRequestDTO).block();
        assertThat(user).isNotNull();

        User user_1 = User.builder()
                .id(4L)
                .name("Ivanov")
                .email("ivan@gmail.com")
                .build();

        webClient.put().uri("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(user_1)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.error").isEqualTo("Not Found");
    }

    @Test
    public void deleteOk() throws Exception {
        UserRequestDTO userRequestDTO = UserRequestDTO.builder()
                .name("Ivan")
                .email("ivan@ya.ru")
                .build();

        User user = userService.create(userRequestDTO).block();
        assertThat(user).isNotNull();

        String jsonObj = objectMapper.writeValueAsString(user);

        webClient.get().uri("/users/{id}", user.getId())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .json(jsonObj);

        webClient.delete().uri("/users/{id}", user.getId())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Deleted");

        webClient.get().uri("/users/{id}", user.getId())
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.error").isEqualTo("Not Found");
    }

    @Test
    public void deleteNotFound() throws Exception {
        webClient.delete().uri("/users/{id}", 4)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.message").isEqualTo("User not found");
    }

    @Test
    public void getAllOk() throws Exception {
        UserRequestDTO userRequestDTO = UserRequestDTO.builder()
                .name("Ivan")
                .email("ivan@ya.ru")
                .build();

        UserRequestDTO userRequestDTO_1 = UserRequestDTO.builder()
                .name("Petr")
                .email("petr@ya.ru")
                .build();

        User user = userService.create(userRequestDTO).block();
        User user_1 = userService.create(userRequestDTO_1).block();
        assertThat(user).isNotNull();
        assertThat(user_1).isNotNull();

        List<User> users = List.of(user, user_1);

        String jsonObj = objectMapper.writeValueAsString(users);

        webClient.get().uri("/users")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .json(jsonObj);
    }
}
