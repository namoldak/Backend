package com.example.namoldak.service;

import com.example.namoldak.domain.RefreshToken;
import com.example.namoldak.repository.RefreshTokenRepository;
import com.example.namoldak.util.GlobalResponse.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.example.namoldak.util.GlobalResponse.code.StatusCode.NOT_FOUND_REFRESHTOKEN;

// 기능 : Redis에 Refresh Token 저장
@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    // 특정 RefreshToken 조회
    public RefreshToken findByEmail(String email) {
        return refreshTokenRepository.findById(email).orElseThrow(
                () -> new CustomException(NOT_FOUND_REFRESHTOKEN)
        );
    }

    // 특정 RefreshToken 조회
    public String findRefreshTokenByEmail(String email) {
        return refreshTokenRepository.findById(email).orElseThrow(
                () -> new CustomException(NOT_FOUND_REFRESHTOKEN)
        ).getRefreshToken();
    }
    
    public boolean existByEmail(String email) {
        return refreshTokenRepository.existsById(email);
    }

    // RefreshToken 저장
    public void saveRefreshToken(RefreshToken refreshToken){
        RefreshToken refreshToken1 = new RefreshToken(refreshToken.getEmail(), refreshToken.getRefreshToken());
        refreshTokenRepository.save(refreshToken1);
    }

    // RefreshToken 삭제
    public void deleteRefreshToken(String email){
        refreshTokenRepository.deleteById(email);
    }
}