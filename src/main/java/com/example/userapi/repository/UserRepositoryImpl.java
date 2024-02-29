package com.example.userapi.repository;

import com.example.userapi.entity.User;
import io.smallrye.mutiny.Uni;
import lombok.AllArgsConstructor;
import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@AllArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final Mutiny.SessionFactory sessionFactory;

    @Override
    public Mono<User> update(User user) {
        return Mono.fromFuture(sessionFactory.withTransaction(session -> session.merge(user).call(session::flush))
                .convert().toCompletableFuture());
    }

    @Override
    public Mono<Boolean> delete(Long id) {

        return Mono.fromFuture(sessionFactory.withTransaction(session -> session.createNamedQuery("delete")
                .setParameter("id", id)
                .executeUpdate()
                .flatMap(i -> Uni.createFrom().item(Boolean.TRUE))
        ).convert().toCompletableFuture());
    }

    @Override
    public Mono<User> create(User user) {
        return Mono.fromFuture(sessionFactory.withTransaction(session -> session.merge(user).call(session::flush))
                .convert().toCompletableFuture());
    }

    @Override
    public Mono<User> get(Long id) {
        return Mono.fromFuture(sessionFactory.withTransaction((session -> session.find(User.class, id)))
                .convert().toCompletableFuture());
    }

    @Override
    public Flux<User> getAll() {
        return Mono.fromFuture(
                sessionFactory.withTransaction(
                        session -> session.createQuery("SELECT u FROM User u", User.class)
                        .getResultList())
                                .convert()
                                .toCompletableFuture())
                                        .flux()
                                        .flatMap(Flux::fromIterable);
    }
}
