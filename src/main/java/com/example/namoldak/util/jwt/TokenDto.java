package com.example.namoldak.util.jwt;

import lombok.Getter;
import lombok.NoArgsConstructor;

// 기능 : 일반 form 가입 회원용 토큰 Dto
@Getter
@NoArgsConstructor
public class TokenDto {
    private String accessToken;
    private String refreshToken;

    public TokenDto(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}