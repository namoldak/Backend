package com.example.namoldak.domain;

import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;
import java.util.List;

// 기능 : Redis에 게임 진행상황 저장
@Getter
@Setter
public class GameStartSet2 implements Serializable {

    private static final long serialVersionUID = 6494678977089006639L;
    private String category;
    private List<String> keyword;
    private Long roomId;
    private Integer round;
    private Integer spotNum = 0;
    private String winner;
}

