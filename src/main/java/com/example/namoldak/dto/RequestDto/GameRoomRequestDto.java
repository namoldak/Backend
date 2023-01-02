package com.example.namoldak.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class GameRoomRequestDto {
    private String gameRoomName;
    private String gameRoomPassword;
}
