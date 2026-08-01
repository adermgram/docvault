package com.adam.docvault.user.dto;

import java.util.UUID;

import com.adam.docvault.user.entity.Role;

public record UserResponseDTO(
        UUID id,
        String firstName,
        String lastName,
        String email,
        Role role
) {}