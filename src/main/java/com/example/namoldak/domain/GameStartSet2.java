package com.example.namoldak.domain;

import lombok.Getter;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

@Getter
public class GameStartSet2 {

    private Long gameSetId;
    private String category;
    private String keyword;
    private Long roomId;
    private Integer round;
    private Integer spotnum = 0;
    private String winner;

    public Integer setRound(Integer round){
        this.round = round;
        return this.round;
    }

    public void setSpotNum(Integer num){
        this.spotnum = num;
    }
}

