package com.example.namoldak.repository;

import com.example.namoldak.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

// 기능 : 유저 정보 레포
public interface MemberRepository extends JpaRepository<Member, Long>{
    Optional<Member> findByEmail(String email); // email로 회원 조회
    Optional<Member> findByNickname(String nickname); // nickname으로 회원 조회
    Optional<Member> findByKakaoId(Long kakaoId); // 카카오Id로 회원 조회
    boolean existsByEmail(String email); // 같은 email 존재여부 확인
    boolean existsByNickname(String nickname); // 같은 nickname 존재여부 확인
    boolean existsByKakaoId(Long kakaoId); // 같은 kakaoId 존재여부 확인
}
