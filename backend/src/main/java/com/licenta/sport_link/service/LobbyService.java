package com.licenta.sport_link.service;

import com.licenta.sport_link.domain.Lobby;
import com.licenta.sport_link.domain.UserAccount;
import com.licenta.sport_link.dto.CreateLobbyRequest;
import com.licenta.sport_link.dto.LobbyResponse;
import com.licenta.sport_link.repository.LobbyRepository;
import com.licenta.sport_link.repository.UserAccountRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class LobbyService {

    private final LobbyRepository lobbyRepository;
    private final UserAccountRepository userAccountRepository;

    public LobbyService(LobbyRepository lobbyRepository, UserAccountRepository userAccountRepository) {
        this.lobbyRepository = lobbyRepository;
        this.userAccountRepository = userAccountRepository;
    }

    @Transactional
    public LobbyResponse createLobby(CreateLobbyRequest request) {
        validateCreateLobbyRequest(request);

        UserAccount creator = getUserOrThrow(request.creatorId());

        Lobby lobby = new Lobby();
        lobby.setSport(request.sport().trim());
        lobby.setLocation(request.location().trim());
        lobby.setEventDateTime(request.dateTime());
        lobby.setMaxParticipants(request.maxParticipants());
        lobby.setActive(true);
        lobby.setCreator(creator);
        lobby.getParticipants().add(creator);

        Lobby saved = lobbyRepository.save(lobby);
        return LobbyResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public List<LobbyResponse> getActiveLobbies() {
        return lobbyRepository.findByActiveTrueAndEventDateTimeAfterOrderByEventDateTimeAsc(LocalDateTime.now())
                .stream()
                .map(LobbyResponse::from)
                .toList();
    }

    @Transactional
    public LobbyResponse joinLobby(Long lobbyId, Long userId) {
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId is required");
        }

        Lobby lobby = getLobbyOrThrow(lobbyId);
        UserAccount user = getUserOrThrow(userId);
        ensureLobbyIsJoinable(lobby);

        boolean alreadyJoined = lobby.getParticipants().stream()
                .anyMatch(participant -> participant.getId().equals(userId));
        if (!alreadyJoined && lobby.getParticipants().size() >= lobby.getMaxParticipants()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Lobby is full");
        }

        lobby.getParticipants().add(user);
        Lobby saved = lobbyRepository.save(lobby);
        return LobbyResponse.from(saved);
    }

    @Transactional
    public LobbyResponse leaveLobby(Long lobbyId, Long userId) {
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId is required");
        }

        Lobby lobby = getLobbyOrThrow(lobbyId);
        getUserOrThrow(userId);

        lobby.getParticipants().removeIf(participant -> participant.getId().equals(userId));
        if (lobby.getParticipants().isEmpty()) {
            lobby.setActive(false);
        }

        Lobby saved = lobbyRepository.save(lobby);
        return LobbyResponse.from(saved);
    }

    private void validateCreateLobbyRequest(CreateLobbyRequest request) {
        if (request == null
                || request.creatorId() == null
                || isBlank(request.sport())
                || isBlank(request.location())
                || request.dateTime() == null
                || request.maxParticipants() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "creatorId, sport, location, dateTime and maxParticipants are required"
            );
        }

        if (request.maxParticipants() < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "maxParticipants must be at least 1");
        }

        if (request.dateTime().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "dateTime must be in the future");
        }
    }

    private void ensureLobbyIsJoinable(Lobby lobby) {
        if (!Boolean.TRUE.equals(lobby.getActive()) || lobby.getEventDateTime().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Lobby is no longer active");
        }
    }

    private UserAccount getUserOrThrow(Long userId) {
        return userAccountRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    private Lobby getLobbyOrThrow(Long lobbyId) {
        return lobbyRepository.findById(lobbyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lobby not found"));
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
