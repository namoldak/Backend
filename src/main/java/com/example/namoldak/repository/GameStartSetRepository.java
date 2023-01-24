package com.example.namoldak.repository;

import com.example.namoldak.domain.GameStartSet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GameStartSetRepository extends JpaRepository<GameStartSet, Long> {
    Optional<GameStartSet> findByRoomId(Long roomId);
    void deleteByRoomId(Long roomId);
}
