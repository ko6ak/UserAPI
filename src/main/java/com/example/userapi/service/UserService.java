package com.example.userapi.service;

import com.example.userapi.dto.UserRequestDTO;
import com.example.userapi.entity.User;
import com.example.userapi.repository.UserRepository;
import com.example.userapi.util.UserUtils;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Cacheable(value = "users", key = "#id")
    public Mono<User> get(Long id) {
        return userRepository.findById(id);
    }

    public Mono<User> create(UserRequestDTO userRequestDTO) {
        return userRepository.save(UserUtils.toUser(userRequestDTO));
    }

    @CachePut(value = "users", key = "#user.id")
    public Mono<User> update(User user) {
        return userRepository.findById(user.getId())
                .flatMap(dbUser -> {
                    if (dbUser.getName().equals(user.getName()) && dbUser.getEmail().equals(user.getEmail())) return Mono.just(dbUser);
                    dbUser.setName(user.getName());
                    dbUser.setEmail(user.getEmail());
                    return userRepository.save(dbUser);
                }).switchIfEmpty(Mono.defer(Mono::empty));
    }

    @CacheEvict(value = "users", key = "#id")
    public Mono<Boolean> delete(Long id) {
        return userRepository.findById(id).flatMap(u -> userRepository.deleteById(id).then(Mono.just(Boolean.TRUE))).switchIfEmpty(Mono.just(Boolean.FALSE));

    }

    public Flux<User> getAll() {
        return userRepository.findAll();
    }
}
