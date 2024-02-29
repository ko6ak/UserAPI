package com.example.userapi.exception;

import com.example.userapi.dto.MessageResponseDTO;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import reactor.core.publisher.Mono;

@ControllerAdvice
public class ExceptionInfoHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public Mono<ResponseEntity<?>> userNotFound(ConstraintViolationException e) {
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponseDTO("Not unique email")));
    }
}
