package com.example.namoldak.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class GameRoomMember extends Timestamped{
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long gameRoomMemberId;

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
        this.member = member;
    }
}

