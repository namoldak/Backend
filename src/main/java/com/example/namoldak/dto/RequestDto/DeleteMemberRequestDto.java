package com.example.namoldak.dto.RequestDto;

import lombok.Getter;

// 기능 : 회원탈퇴 Dto
@Getter
public class DeleteMemberRequestDto {
    private String nickname;
    private String password;
}
