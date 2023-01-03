package com.example.namoldak.controller;


import com.example.namoldak.dto.RequestDto.GameRoomRequestDto;
import com.example.namoldak.dto.ResponseDto.GameRoomResponseDto;
import com.example.namoldak.service.GameRoomService;
import com.example.namoldak.util.GlobalResponse.ResponseUtil;
import com.example.namoldak.util.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RequiredArgsConstructor
@RestController
public class GameRoomController {
    private final GameRoomService gameRoomService;

    @PostMapping("/rooms")
    public ResponseEntity<?> makeGameRoom(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody GameRoomRequestDto gameRoomRequestDto){
        return ResponseUtil.successResponse(gameRoomService.makeGameRoom(userDetails.getMember(), gameRoomRequestDto));
    }

    @GetMapping("/rooms/{pageNum}")
    public ResponseEntity<?> mainPage(@PathVariable int pageNum){
        return ResponseUtil.successResponse(gameRoomService.mainPage(pageNum));
    }

    @PostMapping("/rooms/{roomId}")
    public ResponseEntity<?> enterGame(@PathVariable Long roomId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return ResponseUtil.successResponse(gameRoomService.enterGame(roomId, userDetails.getMember()));
    }
}

