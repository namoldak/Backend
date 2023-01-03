package com.example.namoldak.controller;

import com.example.namoldak.dto.ResponseDto.MemberResponseDto;
import com.example.namoldak.dto.ResponseDto.ResponseDto;
import com.example.namoldak.dto.RequestDto.SignupRequestDto;
import com.example.namoldak.service.KakaoService;
import com.example.namoldak.service.MemberService;
import com.example.namoldak.util.jwt.JwtUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;


@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final KakaoService kakaoService;

    // 회원가입
    @PostMapping(value = "/auth/signup")
    public ResponseEntity<ResponseDto> signup(@RequestBody SignupRequestDto signupRequestDto) {
        memberService.signup(signupRequestDto);
        return ResponseEntity.ok(new ResponseDto(HttpStatus.OK.value(), "회원가입 성공"));
    }

    // 로그인
    @PostMapping(value = "/auth/login")
    public MemberResponseDto login(@RequestBody SignupRequestDto signupRequestDto, HttpServletResponse response) {
        return memberService.login(signupRequestDto, response);
    }

    // 이메일 중복 확인
    @PostMapping("/auth/emailCheck")
    public ResponseEntity<ResponseDto> idCheck(@RequestBody SignupRequestDto signupRequestDto) {
        memberService.emailCheck(signupRequestDto);
        return ResponseEntity.ok(new ResponseDto(HttpStatus.OK.value(), "사용 가능한 이메일"));
    }

    // 닉네임 중복 확인
    @PostMapping("/auth/nicknameCheck")
    public ResponseEntity<ResponseDto> nicknameCheck(@RequestBody SignupRequestDto signupRequestDto) {
        memberService.nicknameCheck(signupRequestDto);
        return ResponseEntity.ok(new ResponseDto(HttpStatus.OK.value(), "사용 가능한 닉네임"));
    }

    // 카카오 로그인
    @GetMapping("/auth/kakao/callback")
    public ResponseEntity<ResponseDto> kakaoLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {
        // code: 카카오 서버로부터 받은 인가 코드
        String createToken = kakaoService.kakaoLogin(code, response);

        // Cookie 생성 및 직접 브라우저에 Set
        Cookie cookie = new Cookie(JwtUtil.AUTHORIZATION_HEADER, createToken.substring(7));  //앞부분이 키값, 뒷부분이 value값
        cookie.setPath("/");
        response.addCookie(cookie);

        return ResponseEntity.ok(new ResponseDto(HttpStatus.OK.value(), "로그인 성공"));
    }
}