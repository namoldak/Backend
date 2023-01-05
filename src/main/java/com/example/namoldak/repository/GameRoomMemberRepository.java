package com.example.namoldak.repository;

import com.example.namoldak.domain.GameRoom;
import com.example.namoldak.domain.GameRoomMember;
import com.example.namoldak.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GameRoomMemberRepository extends JpaRepository<GameRoomMember, Long> {
    List<GameRoomMember> findByGameRoom(GameRoom gameRoom);
    List<GameRoomMember> findByGameRoom(Optional<GameRoom> gameRoom);
    GameRoomMember findByMember(Member member);
}
