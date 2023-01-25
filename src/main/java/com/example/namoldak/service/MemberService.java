package com.example.namoldak.service;

import com.example.namoldak.domain.Member;
import com.example.namoldak.dto.RequestDto.DeleteMemberRequestDto;
import com.example.namoldak.dto.RequestDto.SignupRequestDto;
import com.example.namoldak.dto.ResponseDto.MemberResponseDto;
import com.example.namoldak.dto.ResponseDto.PrivateResponseBody;
import com.example.namoldak.util.GlobalResponse.CustomException;
import com.example.namoldak.util.GlobalResponse.code.StatusCode;
import com.example.namoldak.util.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class MemberService {
    private final PasswordEncoder passwordEncoder;
    private final RepositoryService repositoryService;
    private final JwtUtil jwtUtil;

    // 회원가입
    @Transactional
    public void signup(SignupRequestDto signupRequestDto){
        String email = signupRequestDto.getEmail();
        String password = passwordEncoder.encode(signupRequestDto.getPassword());
        String nickname = signupRequestDto.getNickname();

        if (!repositoryService.MemberDuplicateByEmail(email)) {
            throw new CustomException(StatusCode.EXIST_EMAIL);
        }

        if (!repositoryService.MemberDuplicateByNickname(nickname)) {
            throw new CustomException(StatusCode.EXIST_NICKNAME);
        }

        Member member = new Member(email, nickname, password);
        repositoryService.saveMember(member);
    }

    // 로그인
    @Transactional
    public MemberResponseDto login(SignupRequestDto signupRequestDto, HttpServletResponse response) {
        String email = signupRequestDto.getEmail();
        String password = signupRequestDto.getPassword();

        Member member = repositoryService.findMemberByEmail(email).orElseThrow(
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
        return repositoryService.MemberDuplicateByEmail(email);
    }

    // 닉네임 중복 확인
    @Transactional(readOnly = true)
    public boolean nicknameCheck(String nickname){
        return repositoryService.MemberDuplicateByNickname(nickname);
    }

    public void deleteMember(Member member, DeleteMemberRequestDto deleteMemberRequestDto) {
        if (passwordEncoder.matches(deleteMemberRequestDto.getPassword(), member.getPassword())){
            repositoryService.deleteMember(member);
        } else {
            throw new CustomException(StatusCode.BAD_PASSWORD);
        }
    }

    // 닉네임 변경
    @Transactional
    public PrivateResponseBody changeNickname(SignupRequestDto signupRequestDto, Member member) {
        Member member1 = repositoryService.findMemberById(member.getId()).orElseThrow(
                ()-> new CustomException(StatusCode.LOGIN_MATCH_FAIL)
        );
        if(member.getId().equals(member1.getId())){
            member1.update(signupRequestDto);
            return new PrivateResponseBody<>(StatusCode.OK,"닉네임 변경 완료");
        }else {
            throw new CustomException(StatusCode.LOGIN_MATCH_FAIL);
        }
    }
}
