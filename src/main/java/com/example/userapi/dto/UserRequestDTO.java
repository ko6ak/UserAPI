package com.example.userapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class UserRequestDTO {

    @Schema(example = "Ivan")
    private String name;

    @Schema(example = "ivan@ya.ru")
    private String email;
}
