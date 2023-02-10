package com.example.namoldak.repository;

import com.example.namoldak.domain.GameStartSet;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

// 기능 : 게임 시작시 저장되는 스타트셋 레포
public interface GameStartSetRepository extends JpaRepository<GameStartSet, Long> {
    Optional<GameStartSet> findByRoomId(Long roomId);   // 게임룸 ID로 스타트 셋 찾기
    void deleteByRoomId(Long roomId);   // 게임룸 ID로 스타트 셋 지우기
}
