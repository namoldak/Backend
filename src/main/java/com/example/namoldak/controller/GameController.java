package com.example.namoldak.controller;

import com.example.namoldak.service.GameService;
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
public class GameController {

    private final GameService gameService;

    // 건너뛰기
    @MessageMapping("/pub/game/{gameroomId}/skip")
    public void gameSkip(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @DestinationVariable Long gameroomid) {

        log.info("건너뛰기 - 게임 메세지 : {}, 게임방 아이디 : {}", userDetails.getMember(), gameroomid);
        gameService.gameSkip(userDetails.getMember(), gameroomid);
    }

    @MessageMapping("/pub/game/{gameroomid}/spotlight")
    public void spotlight(
            @DestinationVariable Long gameroomid) {

        log.info("스포트라이트 - 게임방 아이디 : {}", gameroomid);
        gameService.spotlight(gameroomid);
    }
}
