package com.licenta.sport_link.dto;

public record RegisterRequest(
        String name,
        String email,
        String password
) {
}
