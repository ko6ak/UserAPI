package com.example.userapi.controller;

import com.example.userapi.dto.MessageResponseDTO;
import com.example.userapi.dto.UserRequestDTO;
import com.example.userapi.entity.User;
import com.example.userapi.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@AllArgsConstructor
@RequestMapping("/users")
public class UserControllerImpl implements UserController {

    private final UserService userService;

    @Override
    @PostMapping()
    public Mono<ResponseEntity<User>> create(@RequestBody UserRequestDTO userDto) {
        return userService.create(userDto)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not unique email")))
                .map(ResponseEntity::ok);
    }

    @Override
    @GetMapping("/{id}")
    public Mono<ResponseEntity<User>> get(@PathVariable Long id) {
        return userService.get(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")))
                .map(ResponseEntity::ok);
    }

    @Override
    @PutMapping()
    public Mono<ResponseEntity<User>> update(@RequestBody User user) {
        return userService.update(user)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")))
                .map(ResponseEntity::ok);
    }

    @Override
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<?>> delete(@PathVariable Long id) {
        return userService.delete(id)
                .flatMap(b -> b ? Mono.just(ResponseEntity.ok(new MessageResponseDTO("Deleted"))) :
                        Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponseDTO("User not found"))));
    }

    @Override
    @GetMapping()
    public Flux<User> getAll() {
        return userService.getAll();
    }
}
