package com.adam.docvault.document.dto;

import java.time.Instant;
import java.util.UUID;

public record DocumentResponseDTO(
    UUID id,
    String originalFilename,
    String contentType,
    long size,
    Instant createdAt,
    Instant updatedAt
) {}
