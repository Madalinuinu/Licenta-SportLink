package com.licenta.sport_link.dto;

import com.licenta.sport_link.domain.Lobby;
import java.time.LocalDateTime;
import java.util.List;

public record LobbyResponse(
        Long id,
        String title,
        String sport,
        String location,
        LocalDateTime dateTime,
        Integer maxPlayers,
        Integer participantCount,
        Integer availableSpots,
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
                lobby.getTitle(),
                lobby.getSport(),
                lobby.getLocation(),
                lobby.getDateTime(),
                lobby.getMaxPlayers(),
                participantCount,
                Math.max(lobby.getMaxPlayers() - participantCount, 0),
                lobby.getCreator().getId(),
                participantIds
        );
    }
}
