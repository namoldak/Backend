package com.example.namoldak.dto.ResponseDto;

import com.example.namoldak.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class MemberResponseDto {
    private Long memberId;
    private String email;
    private String nickname;

    public MemberResponseDto(Member member){
        this.memberId = member.getId();
        this.email = member.getEmail();
        this.nickname = member.getNickname();
    }
}
