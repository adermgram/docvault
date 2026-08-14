package com.adam.docvault.user.dto;

import java.time.Instant;
import java.util.UUID;

import com.adam.docvault.user.entity.Role;

public record AdminUserResponseDTO(
        UUID id,
        String firstName,
        String lastName,
        String email,
        Role role,
        Instant createdAt,
        Instant updatedAt
) {
}