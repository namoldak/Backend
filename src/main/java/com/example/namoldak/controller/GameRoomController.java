package com.example.namoldak.controller;


import com.example.namoldak.dto.RequestDto.GameRoomRequestDto;
import com.example.namoldak.dto.ResponseDto.GameRoomResponseDto;
import com.example.namoldak.service.GameRoomService;
import com.example.namoldak.util.GlobalResponse.ResponseUtil;
import com.example.namoldak.util.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RequiredArgsConstructor
@RestController
public class GameRoomController {
    private final GameRoomService gameRoomService;

    // 게임룸 생성
//    @Cacheable
    @PostMapping("/rooms")
    public ResponseEntity<?> makeGameRoom(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody GameRoomRequestDto gameRoomRequestDto){
        return ResponseUtil.response(gameRoomService.makeGameRoom(userDetails.getMember(), gameRoomRequestDto));
    }


    // 게임룸 전체조회 (페이징 처리)
    @GetMapping("/rooms") // '/rooms?page=1'
    public List<GameRoomResponseDto> mainPage(@PageableDefault(size = 4, sort = "gameRoomId", direction = Sort.Direction.DESC) Pageable pageable){
        return gameRoomService.mainPage(pageable);
    }

    // 게임룸 키워드 조회
    @GetMapping("/rooms/search") // '/rooms/search?keyword=검색어'
    public List<GameRoomResponseDto> searchGame(@PageableDefault(size = 4, sort = "gameRoomId", direction = Sort.Direction.DESC) Pageable pageable, String keyword){
        return gameRoomService.searchGame(pageable, keyword);
    }

    // 게임룸 입장
    @PostMapping("/rooms/{roomId}")
    public ResponseEntity<?> enterGame(@PathVariable Long roomId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return ResponseUtil.response(gameRoomService.enterGame(roomId, userDetails.getMember()));
    }

    // 게임룸 나가기
    @DeleteMapping("room/{roomId}/exit")
    public ResponseEntity<?> roomExit(@PathVariable Long roomId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return ResponseUtil.response(gameRoomService.roomExit(roomId, userDetails.getMember()));
    }
}

