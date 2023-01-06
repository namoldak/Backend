package com.example.namoldak.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class GameStartSet2 implements Serializable {

    private static final long serialVersionUID = 6494678977089006639L;
    private Long gameSetId;
    private String category;
    private List<String> keyword;
    private Long roomId;
    private Integer round;
    private Integer spotNum = 0;
    private String winner;
}

