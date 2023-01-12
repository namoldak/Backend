package com.example.namoldak.controller;

import com.example.namoldak.dto.RequestDto.AnswerDto;
import com.example.namoldak.service.GameRearService;
import com.example.namoldak.util.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

// 기능 : 게임 진행 관련 부가기능 컨트롤
@Slf4j
@RequiredArgsConstructor
@RestController
public class GameRearController {
    private final GameRearService gameRearService;

    // 게임 끝내기
    @MessageMapping("/pub/game/{gameRoomId}/endGame")
    public void endGame(@DestinationVariable Long gameRoomId) {
        gameRearService.endGame(gameRoomId);
    }

    // 정답
    @MessageMapping("/pub/game/{gameRoomId}/answer")
    public void gameAnswer(@AuthenticationPrincipal UserDetailsImpl userDetails,
                           @DestinationVariable Long gameRoomId,
                           @RequestBody AnswerDto answerDto) {

        log.info("정답 - 게임 메세지 : {}, 게임방 아이디 : {}, 정답 : {}", userDetails.getMember(), gameRoomId, answerDto);
        gameRearService.gameAnswer(userDetails.getMember(), gameRoomId, answerDto);
    }
}
