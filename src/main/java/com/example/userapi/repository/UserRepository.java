package com.example.userapi.repository;

import com.example.userapi.entity.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface UserRepository {
    Mono<User> create(User user);

    Mono<User> update(User user);

    Mono<Boolean> delete(Long id);

    Mono<User> get(Long id);

    Flux<User> getAll();
}