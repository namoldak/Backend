package com.example.namoldak.dto.ResponseDto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MemberResponseDto {
    private Long memberId;
    private String email;
    private String nickname;
}
