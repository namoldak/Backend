package com.example.namoldak.controller;

import com.example.namoldak.service.GameService;
import com.example.namoldak.util.GlobalResponse.ResponseUtil;
import com.example.namoldak.util.GlobalResponse.code.StatusCode;
import com.example.namoldak.util.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

// 기능 : 게임 진행 관련 주요 서비스들을 컨트롤
@Slf4j
@RequiredArgsConstructor
@RestController
public class GameController {

    private final GameService gameService;

    // 게임 시작
    @MessageMapping("/game/{gameRoomId}/start")
    public ResponseEntity<?> gameStart(@DestinationVariable Long gameRoomId,
//    @PostMapping("/pub/game/{gameRoomId}/start")
//    public ResponseEntity<?> gameStart(@PathVariable Long gameRoomId,
                                       @AuthenticationPrincipal UserDetailsImpl userDetails) {
        gameService.gameStart(gameRoomId, userDetails.getMember());
        return ResponseUtil.response(StatusCode.GAME_START);
    }

    // 건너뛰기
    @MessageMapping("/game/{gameRoomId}/skip")
    public void gameSkip(@AuthenticationPrincipal UserDetailsImpl userDetails,
                         @DestinationVariable Long gameRoomId) {

        log.info("건너뛰기 - 게임 메세지 : {}, 게임방 아이디 : {}", userDetails.getMember(), gameRoomId);
        gameService.gameSkip(userDetails.getMember(), gameRoomId);
    }

    // 발언권 부여
    @MessageMapping("/game/{gameRoomId}/spotlight")
    public ResponseEntity<?> spotlight(
            @DestinationVariable Long gameRoomId) {
//    @PostMapping("/pub/game/{gameRoomId}/spotlight")
//    public ResponseEntity<?>  spotlight(
//            @PathVariable Long gameRoomId) {

        log.info("스포트라이트 - 게임방 아이디 : {}", gameRoomId);
        return ResponseUtil.response(gameService.spotlight(gameRoomId));
    }
}
