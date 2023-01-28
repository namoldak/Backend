package com.example.namoldak.repository;

import com.example.namoldak.domain.Comment;
import com.example.namoldak.domain.GameRoom;
import com.example.namoldak.domain.GameRoomAttendee;
import com.example.namoldak.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

// 기능 : 게임룸에 들어간 유저 정보 레포
public interface GameRoomAttendeeRepository extends JpaRepository<GameRoomAttendee, Long> {
    List<GameRoomAttendee> findByGameRoom(GameRoom gameRoom); // 게임룸 ID로 안에 있는 멤버들 전부 조회
    List<GameRoomAttendee> findByGameRoom(Optional<GameRoom> gameRoom); // 게임룸 ID로 안에 있는 멤버들 전부 조회
    GameRoomAttendee findByMember(Member member); // 멤버 객체로 참가자 정보 조회
    List<GameRoomAttendee> findByGameRoom_GameRoomId(Long gameRoomId);
    @Transactional
    void deleteAllByMember(Member member);
    boolean existsByMember(Member member);

}
