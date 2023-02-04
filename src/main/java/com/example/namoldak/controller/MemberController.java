package com.example.namoldak.controller;

import com.example.namoldak.dto.RequestDto.DeleteMemberRequestDto;
import com.example.namoldak.dto.RequestDto.SignupRequestDto;
import com.example.namoldak.dto.ResponseDto.MemberResponseDto;
import com.example.namoldak.dto.ResponseDto.MyDataResponseDto;
import com.example.namoldak.dto.ResponseDto.PrivateResponseBody;
import com.example.namoldak.dto.ResponseDto.ResponseDto;
import com.example.namoldak.service.KakaoService;
import com.example.namoldak.service.MemberService;
import com.example.namoldak.util.GlobalResponse.GlobalResponseDto;
import com.example.namoldak.util.GlobalResponse.ResponseUtil;
import com.example.namoldak.util.jwt.JwtUtil;
import com.example.namoldak.util.security.UserDetailsImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import static com.example.namoldak.util.GlobalResponse.code.StatusCode.*;

// 기능 : 멤버 로그인, 회원가입 관련 컨트롤
@RestController
@RequiredArgsConstructor
@RequestMapping(produces = "application/json; charset=utf8")
public class MemberController {
    private final MemberService memberService;
    private final KakaoService kakaoService;

    // 회원가입
    @PostMapping(value = "/auth/signup")
    public ResponseEntity<GlobalResponseDto> signup(@RequestBody SignupRequestDto signupRequestDto) {
        memberService.signup(signupRequestDto);
        return ResponseUtil.response(SIGNUP_OK);
    }

    // 로그인
    @PostMapping(value = "/auth/login")
    public ResponseEntity<MemberResponseDto> login(@RequestBody SignupRequestDto signupRequestDto,
                                                   HttpServletResponse response) {
        return ResponseUtil.response(memberService.login(signupRequestDto, response));
    }

    // 이메일 중복 확인
    @PostMapping("/auth/emailCheck")
    public ResponseEntity<Boolean> idCheck(@RequestParam ("email") String email) {
        return ResponseUtil.response(memberService.emailCheck(email));
    }

    // 닉네임 중복 확인
    @PostMapping("/auth/nicknameCheck")
    public ResponseEntity<Boolean> nicknameCheck(@RequestParam ("nickname") String nickname) {
        return ResponseUtil.response(memberService.nicknameCheck(nickname));
    }

    // 카카오 로그인
    @GetMapping("/auth/kakao/callback")
    public ResponseEntity<String> kakaoLogin(@RequestParam String code,
                                             HttpServletResponse response) {
        return ResponseUtil.response(kakaoService.kakaoLogin(code, response));
    }

    // 회원탈퇴
    @DeleteMapping("/auth/deleteMember")
    public ResponseEntity<GlobalResponseDto> deleteMember(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                          @RequestBody DeleteMemberRequestDto deleteMemberRequestDto) {
        memberService.deleteMember(userDetails.getMember(), deleteMemberRequestDto);
        return ResponseUtil.response(DELETE_MEMBER_OK);
    }

    // 닉네임 변경
    @PutMapping("/auth/changeNickname")
    public ResponseEntity<PrivateResponseBody> changeNickname(@RequestBody SignupRequestDto signupRequestDto,
                                                              @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseUtil.response(memberService.changeNickname(signupRequestDto, userDetails.getMember()));
    }

    // 내 정보 조회하기
    @GetMapping("/auth/myData")
    public ResponseEntity<MyDataResponseDto> myData(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseUtil.response(memberService.myData(userDetails));
    }

    // 로그아웃
    @PostMapping("/auth/logout")
    public ResponseEntity<GlobalResponseDto> logout(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return memberService.logout(userDetails.getMember().getEmail());
    }

    // 토큰 재발행
    @PostMapping("/auth/issue/token")
    public ResponseDto<String> issuedToken(@AuthenticationPrincipal UserDetailsImpl userDetails, HttpServletResponse response){
        return memberService.issuedToken(userDetails.getMember().getEmail(), response);
    }

    @PostMapping("/auth/leave")
    public ResponseEntity<GlobalResponseDto> leave(Long id) {
        kakaoService.deleteMember(id);
        return ResponseUtil.response(DELETE_MEMBER_OK);
    }
}