package com.example.namoldak.domain;

import com.example.namoldak.dto.RequestDto.SignupRequestDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;

// 기능 : 유저 정보 Entity
@Entity
@Table(name = "MEMBER")
@Getter
@NoArgsConstructor
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false)
    private String password;

    @Column
    private Long kakaoId;

    @Column
    private Long winNum = 0L;

    @Column
    private Long loseNum = 0L;

    @Column
    private Long totalGameNum = 0L;
    @Column
    private Long enterGameNum = 0L;

    @Column
    private Long soloExitNum = 0L;

    @Column
    private Long makeRoomNum = 0L;

    public Member(String email, String nickname, String password) {
        this.email    = email;
        this.nickname = nickname;
        this.password = password;
    }

    public Member(String email, String password, Long kakaoId, String kakaoNickname){
        this.email     = email;
        this.password  = password;
        this.kakaoId   = kakaoId;
        this.nickname  = kakaoNickname;
    }

    public Member kakaoIdUpdate(Long kakaoId){
        this.kakaoId = kakaoId;
        return this;
    }

    public void update(SignupRequestDto signupRequestDto) {
        this.nickname = signupRequestDto.getNickname();
    }

    public void updateWinNum(Long num) {
        this.winNum += num;
    }

    public void updateLoseNum(Long num) {
        this.loseNum += num;
    }

    public void updateTotalGame(Long num) {
        this.totalGameNum += num;
    }

    public void updateSoloExit(Long num) {
        this.soloExitNum += num;
    }

    public void updateMakeRoom(Long num) {
        this.makeRoomNum += num;
    }

    public void updateEnterGame(Long num) {
        this.enterGameNum += num;
    }
}
