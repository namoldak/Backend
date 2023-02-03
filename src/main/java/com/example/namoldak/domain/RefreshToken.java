package com.example.namoldak.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

// 기능 : Redis에 Refresh Token 저장
@Getter
@Setter
@NoArgsConstructor
@RedisHash(value = "refreshToken", timeToLive = 7 * 24 * 60 * 60L ) // 초단위 = 7일
public class RefreshToken {
    @Id
    private String email;
    private String refreshToken;

    public RefreshToken(String email, String token) {
        this.refreshToken = token;
        this.email = email;
    }

    public RefreshToken updateToken(String token) {
        this.refreshToken = token;
        return this;
    }
}