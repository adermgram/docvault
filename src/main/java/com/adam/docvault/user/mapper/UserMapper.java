package com.adam.docvault.user.mapper;

import org.springframework.stereotype.Component;

import com.adam.docvault.user.dto.UserResponseDTO;
import com.adam.docvault.user.entity.User;

@Component
public class UserMapper {

    public UserResponseDTO toResponse(User user){

        return new UserResponseDTO(
            user.getId(),
            user.getFirstName(),
            user.getFirstName(),
            user.getEmail(),
            user.getRole()
        );
    }
}