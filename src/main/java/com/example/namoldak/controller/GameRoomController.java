package com.example.namoldak.controller;

import com.example.namoldak.dto.GameRoomRequestDto;
import com.example.namoldak.service.GameRoomService;
import com.example.namoldak.util.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RequiredArgsConstructor
@RestController
public class GameRoomController {
    private final GameRoomService gameRoomService;

    @PostMapping("/rooms")
    public ResponseEntity<?> makeGameRoom(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody GameRoomRequestDto gameRoomRequestDto){
        return gameRoomService.makeGameRoom(userDetails.getMember(), gameRoomRequestDto);
    }
}

