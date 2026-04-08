package com.licenta.sport_link.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public record CreateLobbyRequest(
        @NotNull(message = "creatorId is required")
        @Positive(message = "creatorId must be a positive number")
        Long creatorId,
        @NotBlank(message = "title is required")
        @Size(max = 100, message = "title must have at most 100 characters")
        String title,
        @NotBlank(message = "sport is required")
        @Size(max = 50, message = "sport must have at most 50 characters")
        String sport,
        @NotBlank(message = "location is required")
        @Size(max = 100, message = "location must have at most 100 characters")
        String location,
        @NotNull(message = "dateTime is required")
        @Future(message = "dateTime must be in the future")
        LocalDateTime dateTime,
        @NotNull(message = "maxPlayers is required")
        @Positive(message = "maxPlayers must be greater than 0")
        Integer maxPlayers
) {
}
