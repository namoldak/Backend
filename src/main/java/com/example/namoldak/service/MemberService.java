package com.example.namoldak.service;

import com.example.namoldak.domain.Member;
import com.example.namoldak.dto.RequestDto.DeleteMemberRequestDto;
import com.example.namoldak.dto.RequestDto.SignupRequestDto;
import com.example.namoldak.dto.ResponseDto.MemberResponseDto;
import com.example.namoldak.util.GlobalResponse.CustomException;
import com.example.namoldak.util.GlobalResponse.code.StatusCode;
import com.example.namoldak.util.jwt.JwtUtil;
import com.example.namoldak.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.servlet.http.HttpServletResponse;

@RequiredArgsConstructor
@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // 회원가입
    @Transactional
    public void signup(SignupRequestDto signupRequestDto){
        String email = signupRequestDto.getEmail();
        String password = passwordEncoder.encode(signupRequestDto.getPassword());
        String nickname = signupRequestDto.getNickname();

        if (memberRepository.findByEmail(email).isPresent()) {
            throw new CustomException(StatusCode.EXIST_EMAIL);
        }

        if (memberRepository.findByNickname(nickname).isPresent()) {
            throw new CustomException(StatusCode.EXIST_NICKNAME);
        }

        Member member = new Member(email, nickname, password);
        memberRepository.save(member);
    }

    // 로그인
    @Transactional
    public MemberResponseDto login(SignupRequestDto signupRequestDto, HttpServletResponse response) {
        String email = signupRequestDto.getEmail();
        String password = signupRequestDto.getPassword();

        Member member = memberRepository.findByEmail(email).orElseThrow(
                () -> new CustomException(StatusCode.LOGIN_MATCH_FAIL)
        );

        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new CustomException(StatusCode.BAD_PASSWORD);
        }

        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, jwtUtil.createToken(member.getEmail()));

        return new MemberResponseDto(member);
    }

    // 이메일 중복 확인
    @Transactional(readOnly = true)
    public boolean emailCheck(String email){
        return memberRepository.findByEmail(email).isPresent();
    }

    // 닉네임 중복 확인
    @Transactional(readOnly = true)
    public boolean nicknameCheck(String nickname){
        return memberRepository.findByNickname(nickname).isPresent();
    }

    public void deleteMember(Member member, DeleteMemberRequestDto deleteMemberRequestDto) {
        if (passwordEncoder.matches(deleteMemberRequestDto.getPassword(), member.getPassword())){
            memberRepository.delete(member);
        } else {
            throw new CustomException(StatusCode.BAD_PASSWORD);
        }
    }
}
