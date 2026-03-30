package com.licenta.sport_link.dto;

public record AuthResponse(
        Long userId,
        String name,
        String email
) {
}
