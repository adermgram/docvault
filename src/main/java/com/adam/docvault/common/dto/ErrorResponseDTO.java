package com.adam.docvault.common.dto;

import java.time.Instant;

public record ErrorResponseDTO(
        Instant timestamp,
        int status,
        String message,
        String path
) {}