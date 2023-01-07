package com.example.namoldak.dto.RequestDto;

import lombok.Getter;
import lombok.NoArgsConstructor;

// 기능 :  kakao 유저 정보를 받는 Dto
@Getter
@NoArgsConstructor
public class KakaoUserInfoDto {
    private Long id;
    private String email;
    private String nickname;

    public KakaoUserInfoDto(Long id, String nickname, String email) {
        this.id       = id;
        this.nickname = nickname;
        this.email    = email;
    }
}