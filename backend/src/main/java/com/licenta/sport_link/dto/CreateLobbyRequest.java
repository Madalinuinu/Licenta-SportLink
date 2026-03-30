package com.licenta.sport_link.dto;

import java.time.LocalDateTime;

public record CreateLobbyRequest(
        Long creatorId,
        String sport,
        String location,
        LocalDateTime dateTime,
        Integer maxParticipants
) {
}
