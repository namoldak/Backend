package com.example.namoldak.repository;

import com.example.namoldak.domain.GameRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// 기능 : 게임룸 레포
public interface GameRoomRepository extends JpaRepository<GameRoom, Long> {
    Page<GameRoom> findAll(Pageable pageable); // 게임룸 전체 조회 페이징처리
    Page<GameRoom> findByGameRoomNameContaining(Pageable pageable, String keyword);
    Optional<GameRoom> findByGameRoomId(Long gameRoomId);

}
