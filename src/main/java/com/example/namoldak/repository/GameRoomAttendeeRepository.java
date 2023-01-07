package com.example.namoldak.repository;

import com.example.namoldak.domain.GameRoom;
import com.example.namoldak.domain.GameRoomAttendee;
import com.example.namoldak.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

// 기능 : 게임룸에 들어간 유저 정보 레포
public interface GameRoomAttendeeRepository extends JpaRepository<GameRoomAttendee, Long> {
    List<GameRoomAttendee> findByGameRoom(GameRoom gameRoom);
    List<GameRoomAttendee> findByGameRoom(Optional<GameRoom> gameRoom);
    GameRoomAttendee findByMember(Member member);
    List<GameRoomAttendee> findByGameRoomOrderByCreatedAt(GameRoom gameRoom);
}
