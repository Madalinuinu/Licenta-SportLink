package com.licenta.sport_link.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "name is required")
        @Size(max = 100, message = "name must have at most 100 characters")
        String name,
        @NotBlank(message = "username is required")
        @Size(min = 3, max = 50, message = "username must be between 3 and 50 characters")
        @Pattern(
                regexp = "^[a-zA-Z0-9_.-]+$",
                message = "username can only contain letters, numbers, dot, underscore and dash"
        )
        String username,
        @NotBlank(message = "password is required")
        @Size(min = 6, max = 255, message = "password must be between 6 and 255 characters")
        String password
) {
}
