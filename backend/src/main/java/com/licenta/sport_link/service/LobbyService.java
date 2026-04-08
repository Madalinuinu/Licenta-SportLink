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
        lobby.setTitle(request.title().trim());
        lobby.setSport(request.sport().trim());
        lobby.setLocation(request.location().trim());
        lobby.setDateTime(request.dateTime());
        lobby.setMaxPlayers(request.maxPlayers());
        lobby.setCreator(creator);
        lobby.getParticipants().add(creator);

        Lobby saved = lobbyRepository.save(lobby);
        return LobbyResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public List<LobbyResponse> getActiveLobbies() {
        return lobbyRepository.findByDateTimeAfterOrderByDateTimeAsc(LocalDateTime.now())
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
        if (alreadyJoined) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User is already in this lobby");
        }

        if (lobby.getParticipants().size() >= lobby.getMaxPlayers()) {
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

        boolean removed = lobby.getParticipants().removeIf(participant -> participant.getId().equals(userId));
        if (!removed) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not in this lobby");
        }

        Lobby saved = lobbyRepository.save(lobby);
        return LobbyResponse.from(saved);
    }

    private void validateCreateLobbyRequest(CreateLobbyRequest request) {
        if (request == null
                || request.creatorId() == null
                || isBlank(request.title())
                || isBlank(request.sport())
                || isBlank(request.location())
                || request.dateTime() == null
                || request.maxPlayers() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "creatorId, title, sport, location, dateTime and maxPlayers are required"
            );
        }

        if (request.maxPlayers() < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "maxPlayers must be at least 1");
        }

        if (request.dateTime().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "dateTime must be in the future");
        }
    }

    private void ensureLobbyIsJoinable(Lobby lobby) {
        if (lobby.getDateTime().isBefore(LocalDateTime.now())) {
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
