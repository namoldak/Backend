package com.example.namoldak.dto.ResponseDto;

import lombok.Getter;
import java.util.List;

// 기능 : 게임룸 List 응답 Dto
@Getter
public class GameRoomResponseListDto {
    private int totalPage;
    List<GameRoomResponseDto> gameRoomResponseDtoList;

    public GameRoomResponseListDto(int totalPage, List<GameRoomResponseDto> gameRoomResponseDtoList) {
        this.totalPage               = totalPage;
        this.gameRoomResponseDtoList = gameRoomResponseDtoList;
    }
}
