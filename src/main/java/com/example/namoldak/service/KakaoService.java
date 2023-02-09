package com.example.namoldak.service;

import com.example.namoldak.domain.Member;
import com.example.namoldak.domain.RefreshToken;
import com.example.namoldak.domainModel.MemberCommand;
import com.example.namoldak.domainModel.MemberQuery;
import com.example.namoldak.dto.RequestDto.KakaoUserInfoDto;
import com.example.namoldak.util.GlobalResponse.CustomException;
import com.example.namoldak.util.jwt.JwtUtil;
import com.example.namoldak.util.jwt.KakaoTokenDto;
import com.example.namoldak.util.security.UserDetailsImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

import static com.example.namoldak.util.GlobalResponse.code.StatusCode.JSON_PROCESS_FAILED;

// 기능 : OAuth.2.0 카카오 로그인
@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoService {
    private final PasswordEncoder passwordEncoder;
    private final MemberQuery memberQuery;
    private final MemberCommand memberCommand;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    @Value("${kakao.oauth2.client.id}")
    String client_id;

    public String kakaoLogin(String code, HttpServletResponse response) {
        // 1. "인가 코드"로 "액세스 토큰" 요청
        String kakaoAccessToken = getToken(code);

        // 2. 토큰으로 카카오 API 호출 : "액세스 토큰"으로 "카카오 사용자 정보" 가져오기
        KakaoUserInfoDto kakaoUserInfo = getKakaoUserInfo(kakaoAccessToken);

        // 3. 필요시에 회원가입
        Member kakaoUser = registerKakaoUserIfNeeded(kakaoUserInfo);

        // 4. 강제 로그인 처리
        Authentication authentication = forceLogin(kakaoUser);

        // 5. response Header에 JWT 토큰 추가
        KakaoTokenDto tokenDto = jwtUtil.createAllToken(kakaoUserInfo.getEmail(), kakaoAccessToken);

        if(refreshTokenService.existByEmail(kakaoUser.getEmail())){
            RefreshToken refreshToken = refreshTokenService.findByEmail(kakaoUser.getEmail());
            refreshTokenService.saveRefreshToken(refreshToken.updateToken(refreshToken.getRefreshToken()));
        } else {
            RefreshToken newToken = new RefreshToken(kakaoUserInfo.getEmail(),jwtUtil.createToken(kakaoUser.getEmail(), "Refresh"));
            refreshTokenService.saveRefreshToken(newToken);
        }

        setHeader(response, tokenDto);

        return kakaoUser.getNickname();
    }

    // 1. "인가 코드"로 "액세스 토큰" 요청
    private String getToken(String code) {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", client_id); // REST API키
        body.add("redirect_uri", "https://namoldak.com/login");
        body.add("code", code);

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest =
                new HttpEntity<>(body, headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        try {
            // HTTP 응답 (JSON) -> 액세스 토큰 파싱
            String responseBody = response.getBody();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            return jsonNode.get("access_token").asText();
        } catch (JsonProcessingException e) {
            throw new CustomException(JSON_PROCESS_FAILED);
        }

    }

    // 2. 토큰으로 카카오 API 호출 : "액세스 토큰"으로 "카카오 사용자 정보" 가져오기
    private KakaoUserInfoDto getKakaoUserInfo(String accessToken) {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoUserInfoRequest = new HttpEntity<>(headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoUserInfoRequest,
                String.class
        );

        try {
            String responseBody = response.getBody();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            Long id = jsonNode.get("id").asLong();
            String nickname = jsonNode.get("properties")
                    .get("nickname").asText();
            String email = jsonNode.get("kakao_account")
                    .get("email").asText();

            log.info("카카오 사용자 정보: " + id + ", " + nickname + ", " + email);
            return new KakaoUserInfoDto(id, nickname, email);
        } catch (JsonProcessingException e) {
            throw new CustomException(JSON_PROCESS_FAILED);
        }
    }

    // 3. 필요시에 회원가입
    private Member registerKakaoUserIfNeeded(KakaoUserInfoDto kakaoUserInfo) {
        // DB 에 중복된 Kakao Id 가 있는지 확인
        Long kakaoId = kakaoUserInfo.getId();
        Member kakaoUser;
        if (!memberQuery.existMemberByKakaoId(kakaoId)) {
            // 카카오 사용자 email 동일한 email 가진 회원이 있는지 확인
            String kakaoEmail = kakaoUserInfo.getEmail();
            if(memberQuery.existMemberByEmail(kakaoEmail)) {
                kakaoUser = memberQuery.findMemberByEmail(kakaoEmail);
            } else {
                // 신규 회원가입
                // password: random UUID
                String password = UUID.randomUUID().toString();
                String encodedPassword = passwordEncoder.encode(password);

                // email: kakao email
                String email = kakaoUserInfo.getEmail();

                kakaoUser = new Member(email, encodedPassword, kakaoId, kakaoUserInfo.getNickname());
            }
            memberCommand.saveMember(kakaoUser);
        } else {
            kakaoUser = memberQuery.findMemberByKakaoId(kakaoId);
        }
        return kakaoUser;
    }

    // 4. 강제 로그인 처리
    private Authentication forceLogin(Member kakaoUser) {
        UserDetails userDetails = new UserDetailsImpl(kakaoUser, kakaoUser.getNickname());
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return authentication;
    }

    // 5. response Header에 JWT 토큰 추가
    private boolean setHeader(HttpServletResponse response, KakaoTokenDto tokenDto) {
        response.addHeader(JwtUtil.ACCESS_TOKEN, tokenDto.getAccessToken());
        response.addHeader(JwtUtil.REFRESH_TOKEN, tokenDto.getRefreshToken());
        response.addHeader(JwtUtil.KAKAO_TOKEN, tokenDto.getKakaoAccessToken());
        return true;
    }

    // 회원탈퇴
    public void deleteKakaoMember(String nickname) {
        Member member = memberQuery.findMemberByNickname(nickname);
        memberCommand.removeMemberInfo(member);
    }
}

