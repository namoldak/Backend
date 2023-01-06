package com.example.namoldak.controller;

import com.example.namoldak.dto.RequestDto.SignupRequestDto;
import com.example.namoldak.service.KakaoService;
import com.example.namoldak.service.MemberService;
import com.example.namoldak.util.GlobalResponse.ResponseUtil;
import com.example.namoldak.util.jwt.JwtUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import java.util.List;

import static com.example.namoldak.util.GlobalResponse.code.StatusCode.*;


@RestController
@RequiredArgsConstructor
@RequestMapping(produces = "application/json; charset=utf8")
public class MemberController {
    private final MemberService memberService;
    private final KakaoService kakaoService;

    // 회원가입
    @PostMapping(value = "/auth/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequestDto signupRequestDto) {
        memberService.signup(signupRequestDto);
        return ResponseUtil.response(SIGNUP_OK);
    }

    // 로그인
    @PostMapping(value = "/auth/login")
    public ResponseEntity<?> login(@RequestBody SignupRequestDto signupRequestDto,
                                   HttpServletResponse response) {
        return ResponseUtil.response(memberService.login(signupRequestDto, response));
    }

    // 이메일 중복 확인
    @PostMapping("/auth/emailCheck")
    public ResponseEntity<?> idCheck(@RequestParam ("email") String email) {
        return ResponseUtil.response(memberService.emailCheck(email));
    }

    // 닉네임 중복 확인
    @PostMapping("/auth/nicknameCheck")
    public ResponseEntity<?> nicknameCheck(@RequestParam ("nickname") String nickname) {
        return ResponseUtil.response(memberService.nicknameCheck(nickname));
    }

    // 카카오 로그인
    @GetMapping("/auth/kakao/callback")
    public ResponseEntity<?> kakaoLogin(@RequestParam String code,
                                        HttpServletResponse response) throws JsonProcessingException {
        // code: 카카오 서버로부터 받은 인가 코드
        List<String> kakaoReturnValue = kakaoService.kakaoLogin(code, response);

        // Cookie 생성 및 직접 브라우저에 Set
        Cookie cookie = new Cookie(JwtUtil.AUTHORIZATION_HEADER, kakaoReturnValue.get(0).substring(7));  //앞부분이 키값, 뒷부분이 value값
        cookie.setPath("/");
        response.addCookie(cookie);
        return ResponseUtil.response(kakaoReturnValue.get(1));
    }
}