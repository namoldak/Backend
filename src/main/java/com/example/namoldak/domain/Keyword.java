package com.example.namoldak.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;

// 기능 : 게임 카테고리와 키워드 Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Keyword {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long keywordId;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String word;
}