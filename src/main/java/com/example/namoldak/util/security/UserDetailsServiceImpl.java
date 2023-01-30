package com.example.namoldak.util.security;

import com.example.namoldak.domain.Member;
import com.example.namoldak.repository.MemberRepository;
import com.example.namoldak.util.GlobalResponse.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import static com.example.namoldak.util.GlobalResponse.code.StatusCode.LOGIN_MATCH_FAIL;

// 기능 : 로그인시 DB에서 해당 유저 체크
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(LOGIN_MATCH_FAIL));
        return new UserDetailsImpl(member, member.getEmail());
    }
}
