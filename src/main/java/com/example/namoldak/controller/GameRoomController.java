package com.example.namoldak.controller;

import com.example.namoldak.dto.RequestDto.GameRoomRequestDto;
import com.example.namoldak.dto.ResponseDto.GameRoomResponseDto;
import com.example.namoldak.dto.ResponseDto.GameRoomResponseListDto;
import com.example.namoldak.service.GameRoomService;
import com.example.namoldak.util.GlobalResponse.GlobalResponseDto;
import com.example.namoldak.util.GlobalResponse.ResponseUtil;
import com.example.namoldak.util.GlobalResponse.code.StatusCode;
import com.example.namoldak.util.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 기능 : 게임룸 관련 CRUD 컨트롤
@Slf4j
@RequiredArgsConstructor
@RestController
public class GameRoomController {
    private final GameRoomService gameRoomService;

    // 게임룸 생성
    @PostMapping("/rooms")
    public ResponseEntity<Map<String, String>> makeGameRoom(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                              @RequestBody GameRoomRequestDto gameRoomRequestDto) {
        return ResponseUtil.response(gameRoomService.makeGameRoom(userDetails.getMember(), gameRoomRequestDto));
    }

    // 게임룸 전체조회 (페이징 처리)
    @GetMapping("/rooms") // '/rooms?page=1'
    public ResponseEntity<GameRoomResponseListDto> mainPage(@PageableDefault(size = 4, sort = "gameRoomId", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseUtil.response(gameRoomService.mainPage(pageable));
    }

    // 게임룸 키워드 조회
    @GetMapping("/rooms/search") // '/rooms/search?keyword=검색어'
    public GameRoomResponseListDto searchGame(@PageableDefault(size = 4, sort = "gameRoomId", direction = Sort.Direction.DESC) Pageable pageable, String keyword) {
        return gameRoomService.searchGame(pageable, keyword);
    }

    // 게임룸 입장
    @PostMapping("/rooms/{roomId}")
    public ResponseEntity<Map<String, String>> enterGame(@PathVariable Long roomId,
                                                                            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseUtil.response(gameRoomService.enterGame(roomId, userDetails.getMember()));
    }

    // 게임룸 나가기
    @DeleteMapping("/rooms/{roomId}/exit")
    public ResponseEntity<GlobalResponseDto> roomExit(@PathVariable Long roomId,
                                      @AuthenticationPrincipal UserDetailsImpl userDetails) {
        gameRoomService.roomExit(roomId, userDetails.getMember());
        return ResponseUtil.response(StatusCode.EXIT_SUCCESS);
    }

    // 게임룸 방장 조회하기
    @GetMapping("/rooms/{roomId}/ownerInfo")
    public ResponseEntity<Map<String, String>> ownerInfo(@PathVariable Long roomId) {
        return ResponseUtil.response(gameRoomService.ownerInfo(roomId));
    }
}

