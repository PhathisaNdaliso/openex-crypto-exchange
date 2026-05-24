package com.openex.backend.dto;

public record UserRequest(
        String username,
        String email,
        String password,
        Boolean enabled
) {
}
