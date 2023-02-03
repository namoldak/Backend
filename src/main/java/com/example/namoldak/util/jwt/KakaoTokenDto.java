package com.example.namoldak.util.jwt;

import lombok.Getter;

@Getter
public class KakaoTokenDto {
    private String accessToken;
    private String refreshToken;
    private String kakaoAccessToken;

    public KakaoTokenDto(String accessToken, String refreshToken, String kakaoAccessToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.kakaoAccessToken = kakaoAccessToken;
    }
}
