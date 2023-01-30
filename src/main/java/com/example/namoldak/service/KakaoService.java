package com.example.namoldak.service;

import com.example.namoldak.domain.Member;
import com.example.namoldak.dto.RequestDto.KakaoUserInfoDto;
import com.example.namoldak.util.GlobalResponse.CustomException;
import com.example.namoldak.util.jwt.JwtUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.example.namoldak.util.GlobalResponse.code.StatusCode.JSON_PROCESS_FAILED;

// 기능 : OAuth.2.0 카카오 로그인
@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoService {
    private final PasswordEncoder passwordEncoder;
    private final RepositoryService repositoryService;
    private final JwtUtil jwtUtil;

    public List<String> kakaoLogin(String code, HttpServletResponse response) {
        // 1. "인가 코드"로 "액세스 토큰" 요청
        String accessToken = getToken(code);

        // 2. 토큰으로 카카오 API 호출 : "액세스 토큰"으로 "카카오 사용자 정보" 가져오기
        KakaoUserInfoDto kakaoUserInfo = getKakaoUserInfo(accessToken);

        // 3. 필요시에 회원가입
        Member kakaoUser = registerKakaoUserIfNeeded(kakaoUserInfo);

        // 4. JWT 토큰 반환 (방법2)
        String createToken = jwtUtil.createToken(kakaoUser.getEmail());
        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, createToken);
        //JWT토큰 만들어서 클라이언트에 보낸 다음에 클라이언트에서 직접 쿠키를 저장하는 방식으로 구현가능 (방법1)
        //서버에서 바로 그냥 쿠키 객체를 만들어서 토큰에 직접 넣어서 반환하는 방법도 있음 (방법2)
        //몇번 방식을 쓸 것인지는 react 쪽과 협의 필요!

        List<String> kakaoReturnValue = new ArrayList<>();
        kakaoReturnValue.add(createToken);
        kakaoReturnValue.add(kakaoUser.getNickname());

        // 리턴값으로 프론트에서 요청한 토큰과 유저닉네임을 같이 반환
        return kakaoReturnValue;
    }

    // 1. "인가 코드"로 "액세스 토큰" 요청
    private String getToken(String code) {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", "8e8f2cd2d31d1ee1c2d676f16d9430a0"); // REST API키
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

        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
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
        Member kakaoUser = repositoryService.findMemberByKakaoId(kakaoId)
                .orElse(null);
        if (kakaoUser == null) {
            // 카카오 사용자 email 동일한 email 가진 회원이 있는지 확인
            String kakaoEmail = kakaoUserInfo.getEmail();
            Member sameEmailUser = repositoryService.findMemberByEmail(kakaoEmail).orElse(null);
            if (sameEmailUser != null) {
                kakaoUser = sameEmailUser;
                // 기존 회원정보에 카카오 Id 추가
                kakaoUser = kakaoUser.kakaoIdUpdate(kakaoId);
            } else {
                // 신규 회원가입
                // password: random UUID
                String password = UUID.randomUUID().toString();
                String encodedPassword = passwordEncoder.encode(password);

                // email: kakao email
                String email = kakaoUserInfo.getEmail();

                kakaoUser = new Member(email, encodedPassword, kakaoId, kakaoUserInfo.getNickname());
            }
            repositoryService.saveMember(kakaoUser);
        }
        return kakaoUser;
    }
}

