package com.example.namoldak.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;

// 기능 : 게임에 필요한 세트 설정
@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameStartSet{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gameSetId;

    @Column(nullable = false)
    private Long roomId;

    @Column(nullable = false)
    private String category;

    @Column
    private String keywordToMember;     // JSON화

    @Column(nullable = false)
    private Integer round;

    @Column(nullable = false)
    private Integer spotNum;

    @Column
    private String winner;

    @Column
    private Long gameStartTime;

    public void setSpotNum(Integer num) {
        this.spotNum = num;
    }

    public void setRound(Integer round) {
        this.round = round;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }
}