package com.example.namoldak.service;

import com.example.namoldak.domain.GameRoomAttendee;
import com.example.namoldak.domain.ImageFile;
import com.example.namoldak.domain.Member;
import com.example.namoldak.dto.RequestDto.DeleteMemberRequestDto;
import com.example.namoldak.dto.RequestDto.SignupRequestDto;
import com.example.namoldak.dto.ResponseDto.MemberResponseDto;
import com.example.namoldak.dto.ResponseDto.MyDataResponseDto;
import com.example.namoldak.dto.ResponseDto.PrivateResponseBody;
import com.example.namoldak.repository.*;
import com.example.namoldak.util.GlobalResponse.CustomException;
import com.example.namoldak.util.GlobalResponse.code.StatusCode;
import com.example.namoldak.util.jwt.JwtUtil;
import com.example.namoldak.util.s3.AwsS3Service;
import com.example.namoldak.util.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final CommentRepository commentRepository;
    private final ImageFileRepository imageFileRepository;
    private final PostRepository postRepository;
    private final GameRoomAttendeeRepository gameRoomAttendeeRepository;
    private final RewardReposiroty rewardReposiroty;
    private final AwsS3Service awsS3Service;

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
            removeMemberInfo(member);
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

    public void leave(Long id) {
        Member member = repositoryService.findMemberByKakaoId(id).orElseThrow(
                ()-> new CustomException(StatusCode.LOGIN_MATCH_FAIL)
        );
        removeMemberInfo(member);
    }

    public void removeMemberInfo(Member member) {
        if(commentRepository.existsByMember(member)){
            commentRepository.deleteAllByMember(member);
        }
        // 게임룸 참여 여부 확인
        if(gameRoomAttendeeRepository.existsByMember(member)){
            gameRoomAttendeeRepository.deleteAllByMember(member);
        }
        // 이미지파일 여부 확인
        if(imageFileRepository.existsByMember(member)){

            List<ImageFile> imageFileList = imageFileRepository.findAllByMember(member);
            for (ImageFile imageFile : imageFileList) {
                String path = imageFile.getPath();
                String filename = path.substring(49);
                awsS3Service.deleteFile(filename);
            }

            imageFileRepository.deleteAllByMember(member);
        }
        // 글 여부 확인
        if(postRepository.existsByMember(member)){
            postRepository.deleteAllByMember(member);
        }
        // 리워드 여부 확인
        if(rewardReposiroty.existsByMember(member)){
            rewardReposiroty.deleteAllByMember(member);
        }
        // 회원 삭제
        repositoryService.deleteMember(member);
    }
}
