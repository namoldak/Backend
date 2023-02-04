package com.example.namoldak.util.jwt;

import lombok.Getter;

@Getter
public class KakaoTokenDto {
    private String accessToken;
    private String kakaoAccessToken;

    public KakaoTokenDto(String accessToken, String kakaoAccessToken) {
        this.accessToken = accessToken;
        this.kakaoAccessToken = kakaoAccessToken;
    }
}
