package com.example.namoldak.dto.ResponseDto;

import lombok.Getter;

import java.util.List;

@Getter
public class GameRoomResponseListDto {
    private int totalPage;
    List<GameRoomResponseDto> gameRoomResponseDtoList;

    public GameRoomResponseListDto(int totalPage, List<GameRoomResponseDto> gameRoomResponseDtoList) {
        this.totalPage = totalPage;
        this.gameRoomResponseDtoList = gameRoomResponseDtoList;
    }
}
