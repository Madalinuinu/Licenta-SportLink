package com.licenta.sport_link.controller;

import com.licenta.sport_link.dto.CreateLobbyRequest;
import com.licenta.sport_link.dto.JoinLeaveLobbyRequest;
import com.licenta.sport_link.dto.LobbyResponse;
import com.licenta.sport_link.service.LobbyService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/lobbies")
public class LobbyController {

    private final LobbyService lobbyService;

    public LobbyController(LobbyService lobbyService) {
        this.lobbyService = lobbyService;
    }

    @PostMapping
    public LobbyResponse createLobby(@RequestBody CreateLobbyRequest request) {
        return lobbyService.createLobby(request);
    }

    @GetMapping("/active")
    public List<LobbyResponse> getActiveLobbies() {
        return lobbyService.getActiveLobbies();
    }

    @PostMapping("/{lobbyId}/join")
    public LobbyResponse joinLobby(@PathVariable Long lobbyId, @RequestBody JoinLeaveLobbyRequest request) {
        return lobbyService.joinLobby(lobbyId, request.userId());
    }

    @PostMapping("/{lobbyId}/leave")
    public LobbyResponse leaveLobby(@PathVariable Long lobbyId, @RequestBody JoinLeaveLobbyRequest request) {
        return lobbyService.leaveLobby(lobbyId, request.userId());
    }
}
