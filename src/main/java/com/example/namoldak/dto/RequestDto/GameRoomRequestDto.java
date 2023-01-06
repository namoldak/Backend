package com.example.namoldak.dto.RequestDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 기능 : 게임룸 생성시 사용하는 Dto
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GameRoomRequestDto {
    private String gameRoomName;
    private String gameRoomPassword;
}
