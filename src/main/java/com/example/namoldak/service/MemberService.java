package com.example.namoldak.service;

import com.example.namoldak.domain.ImageFile;
import com.example.namoldak.domain.Member;
import com.example.namoldak.domain.RefreshToken;
import com.example.namoldak.dto.RequestDto.DeleteMemberRequestDto;
import com.example.namoldak.dto.RequestDto.SignupRequestDto;
import com.example.namoldak.dto.ResponseDto.MemberResponseDto;
import com.example.namoldak.dto.ResponseDto.MyDataResponseDto;
import com.example.namoldak.dto.ResponseDto.PrivateResponseBody;
import com.example.namoldak.dto.ResponseDto.ResponseDto;
import com.example.namoldak.repository.*;
import com.example.namoldak.util.GlobalResponse.CustomException;
import com.example.namoldak.util.GlobalResponse.GlobalResponseDto;
import com.example.namoldak.util.GlobalResponse.ResponseUtil;
import com.example.namoldak.util.GlobalResponse.code.StatusCode;
import com.example.namoldak.util.jwt.JwtUtil;
import com.example.namoldak.util.jwt.KakaoTokenDto;
import com.example.namoldak.util.jwt.TokenDto;
import com.example.namoldak.util.s3.AwsS3Service;
import com.example.namoldak.util.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class MemberService {
    private final PasswordEncoder passwordEncoder;
    private final RepositoryService repositoryService;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;


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

        // user email 값을 포함한 토큰 생성 후 tokenDto 에 저장
        TokenDto tokenDto = jwtUtil.createAllToken(signupRequestDto.getEmail());

//        // user email 값에 해당하는 refreshToken 을 DB에서 가져옴
//        Optional<RefreshToken> refreshToken = Optional.ofNullable(refreshTokenService.findByEmail(member.getEmail()));
//
//        if (refreshToken.isPresent()) {
//            refreshTokenService.saveRefreshToken(refreshToken.get().updateToken(tokenDto.getRefreshToken()));
//        } else {
//            RefreshToken newToken = new RefreshToken(signupRequestDto.getEmail(),tokenDto.getRefreshToken());
//            refreshTokenService.saveRefreshToken(newToken);
//        }

        setHeader(response, tokenDto);

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

    @Transactional
    public MyDataResponseDto myData(UserDetailsImpl userDetails) {
        if (userDetails == null) {
            throw new CustomException(StatusCode.BAD_REQUEST_TOKEN);
        } else {
            return new MyDataResponseDto(userDetails.getMember());
        }
    }

    // 회원탈퇴
    public void deleteMember(Member member, DeleteMemberRequestDto deleteMemberRequestDto) {
        if (passwordEncoder.matches(deleteMemberRequestDto.getPassword(), member.getPassword())){
            // 코멘트 여부 확인
            repositoryService.removeMemberInfo(member);
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

    // 토큰 재발행
    public ResponseDto<String> issuedToken(String email, HttpServletResponse response){
        response.addHeader(JwtUtil.ACCESS_TOKEN, jwtUtil.createToken(email, "Access"));
        return ResponseDto.success("토큰재발행 성공");
    }

    private void setHeader(HttpServletResponse response, TokenDto tokenDto) {
        response.addHeader(JwtUtil.ACCESS_TOKEN, tokenDto.getAccessToken());
//        response.addHeader(JwtUtil.REFRESH_TOKEN, tokenDto.getRefreshToken());
    }

    // 로그아웃
    public ResponseEntity<GlobalResponseDto> logout(String email) {
        // 해당 유저의 refreshtoken 이 없을 경우
        if(refreshTokenService.findByEmail(email) == null){
            throw new CustomException(StatusCode.INVALID_TOKEN);
        }
        // 자신의 refreshtoken 만 삭제 가능
        String memberIdrepo = refreshTokenService.findByEmail(email).getEmail();
        if(email.equals(memberIdrepo)){
            refreshTokenService.deleteRefreshToken(email);
            return ResponseUtil.response(StatusCode.LOGOUT_OK);
        }else{
            return ResponseUtil.response(StatusCode.BAD_REFRESH_TOKEN);
        }
    }
}
