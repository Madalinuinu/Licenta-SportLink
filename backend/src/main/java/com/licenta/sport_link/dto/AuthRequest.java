package com.licenta.sport_link.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthRequest(
        @NotBlank(message = "username is required")
        @Size(min = 3, max = 50, message = "username must be between 3 and 50 characters")
        String username,
        @NotBlank(message = "password is required")
        @Size(min = 6, max = 255, message = "password must be between 6 and 255 characters")
        String password
) {
}
