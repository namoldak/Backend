package com.example.namoldak.controller;

import com.example.namoldak.dto.RequestDto.GameDto;
import com.example.namoldak.service.GameService;
import com.example.namoldak.util.GlobalResponse.ResponseUtil;
import com.example.namoldak.util.GlobalResponse.code.StatusCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

// 기능 : 게임 진행 관련 주요 서비스들을 컨트롤
@Slf4j
@RequiredArgsConstructor
@RestController
public class GameController {

    private final GameService gameService;

    // 게임 시작
    @MessageMapping("/game/{roomId}/start")
    public ResponseEntity<?> gameStart(@DestinationVariable Long roomId,
                                       GameDto gameDto) throws JsonProcessingException {
        gameService.gameStart(roomId, gameDto);
        return ResponseUtil.response(StatusCode.GAME_START);
    }

    // 건너뛰기
    @MessageMapping("/game/{roomId}/skip")
    public void gameSkip(GameDto gameDto,
                         @DestinationVariable Long roomId) {

        gameService.gameSkip(gameDto, roomId);
    }

    // 발언권 부여
    @MessageMapping("/game/{roomId}/spotlight")
    public ResponseEntity<?> spotlight(
            @DestinationVariable Long roomId) {
        log.info("스포트라이트 - 게임방 아이디 : {}", roomId);
        return ResponseUtil.response(gameService.spotlight(roomId));
    }

    // 정답
    @MessageMapping("/game/{roomId}/answer")
    public void gameAnswer(@DestinationVariable Long roomId,
                           @RequestBody GameDto gameDto) throws JsonProcessingException {

        gameService.gameAnswer(roomId, gameDto);
    }

    // 게임 끝내기
    @MessageMapping("/game/{roomId}/endGame")
    public void endGame(@DestinationVariable Long roomId) {
        gameService.endGame(roomId);
    }
}
