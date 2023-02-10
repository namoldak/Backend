package com.example.namoldak.domainModel;

import com.example.namoldak.domain.Member;
import com.example.namoldak.repository.MemberRepository;
import com.example.namoldak.util.GlobalResponse.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.example.namoldak.util.GlobalResponse.code.StatusCode.NOT_FOUND_MEMBER;

// 기능 : 회원 도메인 관련 DB Read 관리
@Service
@RequiredArgsConstructor
public class MemberQuery {
    private final MemberRepository memberRepository;

    // Email로 데이터 검증
    public boolean MemberDuplicateByEmail(String email){
        return memberRepository.existsByEmail(email);
    }

    // Nickname으로 데이터 검증
    public boolean MemberDuplicateByNickname(String nickname){
        return memberRepository.existsByNickname(nickname);
    }

    // 닉네임으로 Member 객체 갖고오기
    public Member findMemberByNickname(String nickname) {
        return memberRepository.findByNickname(nickname).orElseThrow(
                ()-> new CustomException(NOT_FOUND_MEMBER)
        );
    }

    // 이메일로 Member 객체 찾아오기
    public Member findMemberByEmail(String email){
        return memberRepository.findByEmail(email).orElseThrow(
                ()-> new CustomException(NOT_FOUND_MEMBER)
        );
    }

    // 이메일로 Member 객체 찾아오기
    public boolean existMemberByEmail(String email){
        return memberRepository.existsByEmail(email);
    }

    // 카카오 아이디로 Member 객체 찾아오기
    public Member findMemberByKakaoId(Long kakaoId){
        return memberRepository.findByKakaoId(kakaoId).orElseThrow(
                ()-> new CustomException(NOT_FOUND_MEMBER)
        );
    }

    // 멤버 ID로 Member 객체 찾아오기
    public Member findMemberById(Long memberId){
        return memberRepository.findById(memberId).orElseThrow(
                ()-> new CustomException(NOT_FOUND_MEMBER)
        );
    }

    // 카카오 아이디로 존재여부
    public boolean existMemberByKakaoId(Long kakaoId){
        return memberRepository.existsByKakaoId(kakaoId);
    }
}
