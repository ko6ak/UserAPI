package com.example.userapi.exception;

import com.example.userapi.dto.MessageResponseDTO;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import reactor.core.publisher.Mono;

@ControllerAdvice
public class ExceptionInfoHandler {

    @ExceptionHandler(DuplicateKeyException.class)
    public Mono<ResponseEntity<?>> userNotFound(DuplicateKeyException e) {
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponseDTO("Not unique email")));
    }
}
