package com.licenta.sport_link.controller;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public Map<String, String> home() {
        return Map.of(
                "message", "SportLink backend is running",
                "health", "/health",
                "register", "POST /api/auth/register",
                "login", "POST /api/auth/login",
                "createLobby", "POST /api/lobbies",
                "createLobbyAlias", "POST /api/lobbies/create",
                "activeLobbies", "GET /api/lobbies/active"
        );
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "UP");
    }
}
