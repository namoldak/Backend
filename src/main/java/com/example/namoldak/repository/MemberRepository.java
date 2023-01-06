package com.example.namoldak.repository;

import com.example.namoldak.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

// 기능 : 유저 정보 레포
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
    Optional<Member> findByNickname(String nickname);
    Optional<Member> findByKakaoId(Long kakaoId);
}
