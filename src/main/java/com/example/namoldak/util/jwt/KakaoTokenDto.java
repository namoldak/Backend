package com.example.namoldak.util.jwt;

import lombok.Getter;

@Getter
public class KakaoTokenDto {
    private String accessToken;
    private String kakaoAccessToken;
    private String refreshToken;

    public KakaoTokenDto(String accessToken, String refreshToken, String kakaoAccessToken) {
        this.accessToken = accessToken;
        this.kakaoAccessToken = kakaoAccessToken;
        this.refreshToken = refreshToken;
    }
}
