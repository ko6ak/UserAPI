package com.example.userapi.util;

import com.example.userapi.dto.UserRequestDTO;
import com.example.userapi.entity.User;

public class UserUtils {
    public static User toUser(UserRequestDTO userRequestDTO) {
        return new User(userRequestDTO.getName(), userRequestDTO.getEmail());
    }
}
