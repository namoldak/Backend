package com.example.namoldak.domain;

import com.example.namoldak.dto.RequestDto.PostRequestDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Entity
public class Reward {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long rewardId;

    @Column(nullable = false)
    private String rewardName;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    public Reward(String rewardName, Member member) {
        this.rewardName = rewardName;
        this.member = member;
    }
}
