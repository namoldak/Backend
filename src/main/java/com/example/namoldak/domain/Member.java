package com.example.namoldak.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    private Long kakaoId;

    @JsonIgnore
    @JoinColumn(name="gameroommember_id")
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private GameRoomMember gameRoomMember;

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
}
