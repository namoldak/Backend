package com.example.namoldak.controller;

import com.example.namoldak.dto.RequestDto.AnswerDto;
import com.example.namoldak.service.GameRearService;
import com.example.namoldak.util.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

// 기능 : 게임 진행 관련 부가기능 컨트롤
@Slf4j
@RequiredArgsConstructor
@Controller
public class GameRearController {
    private final GameRearService gameRearService;

    @PostMapping("/game/{roomId}/endgame")
    public void endGame(@PathVariable Long roomId) {
        gameRearService.endGame(roomId);
    }

    // 정답
    @PostMapping("/pub/game/{gameroomId}/answer")
    public void gameAnswer(@AuthenticationPrincipal UserDetailsImpl userDetails,
                           @PathVariable Long gameroomid,
            AnswerDto answerDto) {

        log.info("정답 - 게임 메세지 : {}, 게임방 아이디 : {}, 정답 : {}", userDetails.getMember(), gameroomid, answerDto);
        gameRearService.gameAnswer(userDetails.getMember(), gameroomid, answerDto);
    }
}
