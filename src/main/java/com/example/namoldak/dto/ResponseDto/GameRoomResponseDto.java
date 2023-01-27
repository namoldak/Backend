package com.example.namoldak.dto.ResponseDto;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

// 기능 : 게임룸 정보 Response Dto
@Builder
@Getter
public class GameRoomResponseDto {
    private Long id;
    private String roomName;
    private String roomPassword;
    private String owner;
    private boolean status;
    private int memberCnt;
    private List<MemberResponseDto> member;
}
