package com.example.namoldak.domain;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import java.util.Map;

// 기능 : Redis에 게임 진행상황 저장
@Getter
@RedisHash("GAME_SET")
public class GameStartSet {

    @Id
    private Long roomId;
    private String category;
    private Map<String, String> keywordToMember;
    private Integer round;
    private Integer spotNum = 0;
    private String winner;

    @Builder
    public GameStartSet(Long roomId,
                        String category,
                        Map<String, String> keywordToMember,
                        Integer round,
                        Integer spotNum,
                        String winner) {
        this.roomId = roomId;
        this.category = category;
        this.keywordToMember = keywordToMember;
        this.round = round;
        this.spotNum = spotNum;
        this.winner = winner;
    }

    // Setter
    public void setKeywordToMember(Map<String, String> keywordToMember) {
        this.keywordToMember = keywordToMember;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setRound(Integer round) {
        this.round = round;
    }

    public void setSpotNum(Integer spotNum) {
        this.spotNum = spotNum;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }
}

