package com.example.namoldak.service;

import com.example.namoldak.domain.Member;
import com.example.namoldak.dto.RequestDto.SignupRequestDto;
import com.example.namoldak.dto.ResponseDto.MemberResponseDto;
import com.example.namoldak.dto.ResponseDto.ResponseDto;
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

    @Transactional
    public void signup(SignupRequestDto signupRequestDto){
        String email = signupRequestDto.getEmail();
        String password = passwordEncoder.encode(signupRequestDto.getPassword());
        String nickname = signupRequestDto.getNickname();

        if (memberRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("이미 있는 이메일임");
        }

        if (memberRepository.findByNickname(nickname).isPresent()) {
            throw new IllegalArgumentException("이미 있는 닉네임임");
        }

        Member member = new Member(email, nickname, password);
        memberRepository.save(member);
    }

    @Transactional
    public MemberResponseDto login(SignupRequestDto signupRequestDto, HttpServletResponse response) {
        String email = signupRequestDto.getEmail();
        String password = signupRequestDto.getPassword();

        Member member = memberRepository.findByEmail(email).orElseThrow(
                () -> new IllegalArgumentException("유저를 찾을 수 없음")
        );

        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 다르잖슴");
        }

        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, jwtUtil.createToken(member.getEmail()));

        return new MemberResponseDto(member);
    }

    @Transactional(readOnly = true)
    public void emailCheck(SignupRequestDto signipRequesDto){
        String email = signipRequesDto.getEmail();

        if (memberRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("이미 있는 이메일임");
        }
    }

    @Transactional(readOnly = true)
    public void nicknameCheck(SignupRequestDto signipRequesDto){
        String nickname = signipRequesDto.getNickname();

        if (memberRepository.findByNickname(nickname).isPresent()) {
            throw new IllegalArgumentException("이미 있는 닉네임임");
        }
    }
}
