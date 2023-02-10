package com.example.namoldak.repository;

import com.example.namoldak.domain.GameRoom;
import com.example.namoldak.domain.GameRoomAttendee;
import com.example.namoldak.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

// 기능 : 게임룸에 들어간 유저 정보 레포
public interface GameRoomAttendeeRepository extends JpaRepository<GameRoomAttendee, Long> {
    List<GameRoomAttendee> findByGameRoom(GameRoom gameRoom); // 게임룸 객체로 안에 있는 멤버들 전부 조회
    Optional<GameRoomAttendee> findByMember(Member member); // 멤버 객체로 참가자 정보 조회
    Optional<GameRoomAttendee> findByMember_Id(Long memberId); // 멤버 ID로 참가자 정보 조회
    List<GameRoomAttendee> findByGameRoom_GameRoomId(Long gameRoomId); // 게임룸 ID로 안에 있는 멤버 전부 조회
    @Transactional
    void deleteAllByMember(Member member);  // 멤버 객체로 삭제
    boolean existsByMember(Member member);  // 멤버 존재 여부 확인

}
