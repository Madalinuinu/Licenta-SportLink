package com.licenta.sport_link.dto;

import com.licenta.sport_link.domain.Lobby;
import java.time.LocalDateTime;
import java.util.List;

public record LobbyResponse(
        Long id,
        String sport,
        String location,
        LocalDateTime dateTime,
        Integer maxParticipants,
        Integer participantCount,
        Integer availableSpots,
        Boolean active,
        Long creatorId,
        List<Long> participantIds
) {
    public static LobbyResponse from(Lobby lobby) {
        List<Long> participantIds = lobby.getParticipants()
                .stream()
                .map(user -> user.getId())
                .toList();

        int participantCount = participantIds.size();
        return new LobbyResponse(
                lobby.getId(),
                lobby.getSport(),
                lobby.getLocation(),
                lobby.getEventDateTime(),
                lobby.getMaxParticipants(),
                participantCount,
                Math.max(lobby.getMaxParticipants() - participantCount, 0),
                lobby.getActive(),
                lobby.getCreator().getId(),
                participantIds
        );
    }
}
