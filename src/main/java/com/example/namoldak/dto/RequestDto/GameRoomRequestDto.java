package com.example.namoldak.dto.RequestDto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class GameRoomRequestDto {
    private String gameRoomName;
    private String gameRoomPassword;
}