package com.example.namoldak.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

@Getter
@Entity
@Builder
@NoArgsConstructor
public class GameStartSet{
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
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

    public GameStartSet(Long gameSetId,
                        Long roomId,
                         String category,
                         String keywordToMember,
                         Integer round,
                         Integer spotNum,
                         String winner,
                         Long gameStartTime) {
        this.gameSetId       = gameSetId;
        this.roomId          = roomId;
        this.category        = category;
        this.keywordToMember = keywordToMember;
        this.round           = round;
        this.spotNum         = spotNum;
        this.winner          = winner;
        this.gameStartTime   = gameStartTime;
    }

    public void setSpotNum(Integer num) {
        this.spotNum = num;
    }

    public void setRound(Integer round) {
        this.round = round;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public GameStartSet update(GameStartSet gameStartSet){
        this.round      = gameStartSet.getRound();
        this.winner     = gameStartSet.getWinner();
        return gameStartSet;
    }
}