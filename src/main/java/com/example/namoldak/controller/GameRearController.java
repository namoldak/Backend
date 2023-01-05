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

@Slf4j
@RequiredArgsConstructor
@Controller
public class GameRearController {

    private final GameRearService gameRearService;

    // 정답
    @MessageMapping("/pub/game/{gameroomId}/answer")
    public void gameAnswer(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @DestinationVariable Long gameroomid,
            AnswerDto answerDto) {

        log.info("정답 - 게임 메세지 : {}, 게임방 아이디 : {}, 정답 : {}", userDetails.getMember(), gameroomid, answerDto);
        gameRearService.gameAnswer(userDetails.getMember(), gameroomid, answerDto);
    }
}
