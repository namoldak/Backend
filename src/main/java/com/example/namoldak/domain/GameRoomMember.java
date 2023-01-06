package com.example.namoldak.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Optional;

// 기능 : 게임룸과 유저를 다대다 연결하는 중간 Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class GameRoomMember extends Timestamped{
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long gameRoomMemberId;

//    @Column
//    private String keyword;

//    @Column
//    private String nickname;

    // 추가
    @JsonIgnore
    @JoinColumn(name="gameroomid")
    @ManyToOne(fetch = FetchType.LAZY)
    private GameRoom gameRoom;

    //TODO 왜 이렇게 했나요?
    @JsonIgnore
    @JoinColumn(name="memberid")
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    public GameRoomMember(GameRoom gameRoom, Member member){
        this.gameRoom = gameRoom;
        this.member   = member;
    }

    public GameRoomMember(Optional <GameRoom> gameRoom, Member member){
        this.gameRoom = gameRoom.get();
        this.member   = member;
    }
}

