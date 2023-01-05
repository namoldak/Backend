package com.example.namoldak.repository;

import com.example.namoldak.domain.GameStartSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameStartSetRepository extends JpaRepository<GameStartSet, Long> {
    GameStartSet findByRoomId(Long roomId);
}
