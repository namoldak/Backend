package com.example.namoldak.util.jwt;

import com.example.namoldak.domain.RefreshToken;
import com.example.namoldak.service.RefreshTokenService;
import com.example.namoldak.util.GlobalResponse.CustomException;
import com.example.namoldak.util.security.UserDetailsServiceImpl;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

import static com.example.namoldak.util.GlobalResponse.code.StatusCode.*;

// 기능 : JWT 유틸
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {
    private final RefreshTokenService refreshTokenService;
    private final UserDetailsServiceImpl userDetailsService;
    private static final long ACCESS_TIME =  30 * 60 * 1000L; // ACCESS_TIME = 30분
    private static final long REFRESH_TIME =  7 * 24 * 60 * 60 * 1000L;  // REFRESH_TIME = 7일
    public static final String ACCESS_TOKEN = "AccessToken";
    public static final String REFRESH_TOKEN = "RefreshToken";
    @Value("${jwt.secret.key}")
    private String secretKey;
    private Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    // header 토큰을 가져오는 기능
    public String getHeaderToken(HttpServletRequest request, String type) {
        return type.equals("Access") ? request.getHeader(ACCESS_TOKEN) : request.getHeader(REFRESH_TOKEN);
    }

    // 토큰 생성
    public TokenDto createAllToken(String email) {
        return new TokenDto(createToken(email, "Access"), createToken(email, "Refresh"));
    }

    public String createToken(String email, String type) {
        //현재 시각
        Date date = new Date();
        //지속 시간
        long time = type.equals("Access") ? ACCESS_TIME : REFRESH_TIME;

        return Jwts.builder()
                .setSubject(email)
                .setExpiration(new Date(date.getTime() + time))
                .setIssuedAt(date)
                .signWith(key, signatureAlgorithm)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            throw new CustomException(LOGIN_WRONG_SIGNATURE_JWT_TOKEN);
        } catch (ExpiredJwtException e) {
            throw new CustomException(LOGIN_EXPIRED_JWT_TOKEN);
        } catch (UnsupportedJwtException e) {
            throw new CustomException(LOGIN_NOT_SUPPORTED_JWT_TOKEN);
        } catch (IllegalArgumentException e) {
            throw new CustomException(LOGIN_WRONG_FORM_JWT_TOKEN);
        } catch (SignatureException e) {
            throw new CustomException(SIGNATURE_EXCEPTION);
        }
    }

    // refreshToken 토큰 검증
    public Boolean refreshTokenValidation(String token) {

        // 1차 토큰 검증
        if (!validateToken(token)) return false;

        // DB에 저장한 토큰 비교
        Optional<RefreshToken> refreshToken = Optional.ofNullable(refreshTokenService.findByEmail(getUserInfoFromToken(token)));

        return refreshToken.isPresent() && token.equals(refreshToken.get().getRefreshToken());
    }

    // 토큰에서 loginId 가져오는 기능
    public String getUserInfoFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
    }

    // 인증 객체 생성
    public Authentication createAuthentication(String email) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}