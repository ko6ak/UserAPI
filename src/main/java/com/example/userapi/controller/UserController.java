package com.example.userapi.controller;

import com.example.userapi.dto.UserRequestDTO;
import com.example.userapi.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Tag(name = "User controller", description = "Выполняет CRUD операции с пользователями")
public interface UserController {

    @Operation(
            summary = "Создание пользователя",
            responses = {
                    @ApiResponse(
                            description = "Пользователь создан",
                            responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {@ExampleObject(value = """
                                                            {
                                                                "id": 1,
                                                                "name": "Ivan",
                                                                "email": "ivan@ya.ru"
                                                            }
                                                            """
                                    )}
                            )),
                    @ApiResponse(
                            description = "Пользователь с таким email уже есть",
                            responseCode = "400",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {@ExampleObject(value = """
                                                            {
                                                                "message": "Not unique email"
                                                            }
                                                            """
                                    )}
                            ))
            }
    )
    Mono<ResponseEntity<User>> create(UserRequestDTO userDto);

    @Operation(
            summary = "Получение пользователя по id",
            responses = {
                    @ApiResponse(
                            description = "Пользователь найден",
                            responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {@ExampleObject(value = """
                                                            {
                                                                "id": 1,
                                                                "name": "Ivan",
                                                                "email": "ivan@ya.ru"
                                                            }
                                                            """
                                            )
                                    }

                            )),
                    @ApiResponse(
                            description = "Пользователь не найден",
                            responseCode = "404",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(
                                                    value = """
                                                            {
                                                                "timestamp": "2024-02-20T09:41:17.941+00:00",
                                                                "path": "/users/3",
                                                                "status": 404,
                                                                "error": "Not Found",
                                                                "requestId": "606b3fc9-5"
                                                            }
                                                            """
                                            )
                                    }
                            ))
            }
    )
    Mono<ResponseEntity<User>> get(Long id);

    @Operation(
            summary = "Обновление информации о пользователе",
            responses = {
                    @ApiResponse(
                            description = "Информация обновлена",
                            responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {@ExampleObject(value = """
                                                            {
                                                                "id": 1,
                                                                "name": "Ivan",
                                                                "email": "ivan@gmail.com"
                                                            }
                                                            """
                                            )
                                    }

                            )),
                    @ApiResponse(
                            description = "Пользователь не найден",
                            responseCode = "404",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(
                                                    value = """
                                                            {
                                                                "timestamp": "2024-02-20T09:41:17.941+00:00",
                                                                "path": "/users/3",
                                                                "status": 404,
                                                                "error": "Not Found",
                                                                "requestId": "606b3fc9-5"
                                                            }
                                                            """
                                            )
                                    }
                            ))
            }
    )
    Mono<ResponseEntity<User>> update(User user);

    @Operation(
            summary = "Удаление пользователя",
            responses = {
                    @ApiResponse(
                            description = "Пользователь удален",
                            responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(
                                                    value = """
                                                            {
                                                                "message": "Deleted"
                                                            }"""
                                            )
                                    }

                            )),
                    @ApiResponse(
                            description = "Пользователь не найден",
                            responseCode = "404",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(
                                                    value = """
                                                            {
                                                                "timestamp": "2024-02-20T09:41:17.941+00:00",
                                                                "path": "/users/3",
                                                                "status": 404,
                                                                "error": "Not Found",
                                                                "requestId": "606b3fc9-5"
                                                            }
                                                            """
                                            )
                                    }
                            ))
            }
    )
    Mono<ResponseEntity<?>> delete(Long id);

    @Operation(
            summary = "Получение списка всех пользователей",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(
                                                    value = """
                                                            [
                                                            {
                                                                "id": 1,
                                                                "name": "Ivan",
                                                                "email": "ivan@ya.ru"
                                                            },
                                                            {
                                                                "id": 2,
                                                                "name": "Petr",
                                                                "email": "petr@ya.ru"
                                                            }
                                                            ]
                                                            """
                                            )
                                    }
                            ))
            }
    )
    Flux<User> getAll();
}
