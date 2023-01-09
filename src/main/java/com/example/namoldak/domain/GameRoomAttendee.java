package com.example.namoldak.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Optional;

// 기능 : 게임룸과 유저를 연결하는 중간 Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class GameRoomAttendee extends Timestamped{
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long gameRoomMemberId;

    @JoinColumn(name="gameroomid")
    @OneToOne(fetch = FetchType.LAZY)
    private GameRoom gameRoom;

    @JoinColumn(name="memberid")
    @OneToOne(fetch = FetchType.LAZY)
    private Member member;

    @Column(nullable = false)
    private String memberNickname;

    public GameRoomAttendee(GameRoom gameRoom, Member member){
        this.gameRoom = gameRoom;
        this.member   = member;
        this.memberNickname = member.getNickname();
    }

    public GameRoomAttendee(Optional <GameRoom> gameRoom, Member member){
        this.gameRoom = gameRoom.get();
        this.member   = member;
        this.memberNickname = member.getNickname();
    }
}

