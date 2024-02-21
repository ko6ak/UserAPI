package com.example.userapi;

import com.example.userapi.dto.UserRequestDTO;
import com.example.userapi.entity.User;
import com.example.userapi.repository.UserRepository;
import com.example.userapi.service.UserService;
import com.example.userapi.util.UserUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DuplicateKeyException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@SpringBootTest
class UserServiceTests {

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @Test
    public void getOk() throws Exception {
        User user = User.builder()
                .id(1L)
                .name("Ivan")
                .email("ivan@ya.ru")
                .build();

        Mono<User> monoUser = Mono.just(user);

        when(userRepository.get(1L)).thenReturn(monoUser);

        assertEquals(user, userService.get(1L).block());
    }

    @Test
    public void getNotFound() throws Exception {
        when(userRepository.get(4L)).thenReturn(Mono.empty());

        assertEquals(Mono.empty(), userService.get(4L));
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

        when(userRepository.create(any(User.class))).thenReturn(monoUser);

        assertEquals(user, userService.create(userRequestDTO).block());
    }

    @Test
    public void createNotUniqueEmail() throws Exception {
        UserRequestDTO userRequestDTO = UserRequestDTO.builder()
                .name("Ivan")
                .email("ivan@ya.ru")
                .build();

        when(userRepository.create(any(User.class))).thenThrow(DuplicateKeyException.class);

        assertThrows(DuplicateKeyException.class, () -> userService.create(userRequestDTO));
    }

    @Test
    public void updateOk() throws Exception {
        User user = User.builder()
                .id(1L)
                .name("Ivan")
                .email("ivan@ya.ru")
                .build();

        Mono<User> monoUser = Mono.just(user);

        when(userRepository.get(1L)).thenReturn(monoUser);
        when(userRepository.update(any(User.class))).thenReturn(monoUser);

        assertEquals(user, userService.update(user).block());
        verify(userRepository,  times(0)).update(user);

        User user_1 = User.builder()
                .id(1L)
                .name("Ivanov")
                .email("ivan@gmail.com")
                .build();

        assertEquals(user_1, userService.update(user_1).block());
        verify(userRepository,  times(1)).update(user_1);
    }

    @Test
    public void updateNotFound() throws Exception {
        User user = User.builder()
                .id(4L)
                .name("Ivanov")
                .email("ivan@gmail.com")
                .build();

        when(userRepository.get(4L)).thenReturn(Mono.empty());

        assertNull(userService.update(user).block());
    }

    @Test
    public void deleteOk() throws Exception {
        when(userRepository.get(1L)).thenReturn(Mono.just(new User()));
        when(userRepository.delete(1L)).thenReturn(Mono.just(true));

        assertEquals(Boolean.TRUE, userService.delete(1L).block());
    }

    @Test
    public void deleteNotFound() throws Exception {
        when(userRepository.get(4L)).thenReturn(Mono.empty());

        assertEquals(Boolean.FALSE, userService.delete(4L).block());
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

        when(userRepository.getAll()).thenReturn(Flux.fromIterable(users));

        assertEquals(users, userService.getAll().toStream().toList());
    }
}
