package com.example.namoldak.dto.ResponseDto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class GameRoomResponseDto {
    private Long id;
    private String roomName;
    private String roomPassword;
    private String owner;
    private String status;
    private int memberCnt;
    private List<MemberResponseDto> member;
}
