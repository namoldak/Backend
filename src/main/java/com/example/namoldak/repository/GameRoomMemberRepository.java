package com.example.namoldak.repository;

import com.example.namoldak.domain.GameRoom;
import com.example.namoldak.domain.GameRoomMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GameRoomMemberRepository extends JpaRepository<GameRoomMember, Long> {
    List<GameRoomMember> findByGameRoom(GameRoom gameRoom);
}
