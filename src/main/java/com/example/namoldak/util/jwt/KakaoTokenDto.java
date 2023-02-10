package com.example.namoldak.util.jwt;

import lombok.Getter;

// 기능 : 카카오 회원용 토큰 Dto
@Getter
public class KakaoTokenDto {
    private String accessToken;
    private String kakaoAccessToken;    // 연결끊기 용으로 사용되는 카카오 accessToken
    private String refreshToken;

    public KakaoTokenDto(String accessToken, String refreshToken, String kakaoAccessToken) {
        this.accessToken = accessToken;
        this.kakaoAccessToken = kakaoAccessToken;
        this.refreshToken = refreshToken;
    }
}
