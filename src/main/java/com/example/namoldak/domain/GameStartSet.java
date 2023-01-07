package com.example.namoldak.domain;

import lombok.*;
import javax.persistence.*;

// 기능 : 게임진행 관련 Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Entity
public class GameStartSet {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long gameStartSetId;
    @Column
    private String category;
    @Column
    private String keyword;
    @Column
    private Long roomId;
    @Column
    private Integer round;
    @Column
    private Integer spotNum;
    @Column
    private String winner;


    public void setWinner(String winner){
        this.winner = winner;
    }
}
