package com.example.namoldak.domain;

import lombok.Getter;

import javax.persistence.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class GameStartSet2 {

    private Long gameSetId;
    private String category;
    private List<String> keyword;
    private Long roomId;
    private Integer round;
    private Integer spotnum = 0;
    private String winner;

    public GameStartSet2(Long roomId, String category, List<String> keyword ){
        this.roomId = roomId;
        this.category = category;
        this.keyword = keyword;
        this.round = 0;
        this.spotnum = 0;
    }

    public Integer setRound(Integer round){
        this.round = round;
        return this.round;
    }

    public void setSpotNum(Integer num){
        this.spotnum = num;
    }
}

