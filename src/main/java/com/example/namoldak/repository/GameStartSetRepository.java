package com.example.namoldak.repository;

import com.example.namoldak.domain.GameStartSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// 기능 : 게임 진행 세트 레포
@Repository
public interface GameStartSetRepository extends JpaRepository<GameStartSet, Long> {
    GameStartSet findByRoomId(Long roomId); // 게임셋 단건 조회

}

