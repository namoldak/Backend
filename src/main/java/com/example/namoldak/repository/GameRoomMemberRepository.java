package com.example.namoldak.repository;

import com.example.namoldak.domain.GameRoom;
import com.example.namoldak.domain.GameRoomMember;
import com.example.namoldak.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

// 기능 : 게임룸에 들어간 유저 정보 레포
public interface GameRoomMemberRepository extends JpaRepository<GameRoomMember, Long> {
    List<GameRoomMember> findByGameRoom(GameRoom gameRoom);
    List<GameRoomMember> findByGameRoom(Optional<GameRoom> gameRoom);
    GameRoomMember findByMember(Member member);
    List<GameRoomMember> findByGameRoomOrderByCreatedAt(GameRoom gameRoom);
}
