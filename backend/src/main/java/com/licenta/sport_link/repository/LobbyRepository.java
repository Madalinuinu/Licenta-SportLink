package com.licenta.sport_link.repository;

import com.licenta.sport_link.domain.Lobby;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LobbyRepository extends JpaRepository<Lobby, Long> {
    List<Lobby> findByActiveTrueAndEventDateTimeAfterOrderByEventDateTimeAsc(LocalDateTime now);
}
