package com.openex.backend.dto;

import java.time.Instant;

public record UserResponse(
        Long id,
        String username,
        String email,
        Boolean enabled,
        Instant createdAt,
        Instant updatedAt
) {
}
