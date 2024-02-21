package com.example.userapi.repository;

import com.example.userapi.entity.User;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import lombok.AllArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.BiFunction;

@Repository
@AllArgsConstructor
@Transactional(readOnly = true)
public class UserRepositoryImpl implements UserRepository{

    public static final BiFunction<Row, RowMetadata, User> MAPPING_FUNCTION = (row, rowMetaData) -> User.builder()
            .id(row.get("id", Long.class))
            .name(row.get("name", String.class))
            .email(row.get("email", String.class))
            .build();

    private final DatabaseClient databaseClient;

    @Override
    @Transactional
    public Mono<User> update(User user) {
        return databaseClient.sql("UPDATE users set name=:name, email=:email WHERE id=:id")
                .bind("name", user.getName())
                .bind("email", user.getEmail())
                .bind("id", user.getId())
                .fetch()
                .first()
                .then(Mono.just(user));
    }

    @Override
    @Transactional
    public Mono<Boolean> delete(Long id) {
        return databaseClient.sql("DELETE FROM users WHERE id=:id")
                .bind("id", id)
                .fetch()
                .first()
                .then(Mono.just(true));
    }


    @Override
    @Transactional
    public Mono<User> create(User user) {
        return databaseClient.sql("INSERT INTO  users (name, email) VALUES (:name, :email)")
                .filter((statement, executeFunction) -> statement.returnGeneratedValues("id").execute())
                .bind("name", user.getName())
                .bind("email", user.getEmail())
                .fetch()
                .first()
                .map(r -> {
                    user.setId((Long) r.get("id"));
                    return user;
                });
    }

    @Override
    public Mono<User> get(Long id) {
        return databaseClient
                .sql("SELECT * FROM users WHERE id=:id")
                .bind("id", id)
                .map(MAPPING_FUNCTION)
                .one();
    }

    @Override
    public Flux<User> getAll() {
        return databaseClient
                .sql("SELECT * FROM users")
                .map(MAPPING_FUNCTION)
                .all();
    }
}
