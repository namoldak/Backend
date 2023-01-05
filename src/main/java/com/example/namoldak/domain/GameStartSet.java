package com.example.namoldak.domain;

import lombok.Getter;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

@Getter
public class GameStartSet {

    private Long gameSetId;
    private String category;
    private String keyword;
    private Long roomId;
    private Integer round;
    private Integer spotnum = 0;
    private String winner;

    public Integer oneMoerRound(){
        this.round++;
        return this.round;
    }
}
