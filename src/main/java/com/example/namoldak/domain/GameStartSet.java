package com.example.namoldak.domain;

import lombok.*;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Entity
@Setter
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

}
