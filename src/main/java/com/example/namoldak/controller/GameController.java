package com.example.namoldak.controller;

import com.example.namoldak.service.GameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
@Slf4j
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;


    // pub 사용 스포트라이트
    //TODO 이것도 Websocket 이용해서 들어가네
    @MessageMapping("/game/{gameroomid}/spotlight")
    public void spotlight(
            @DestinationVariable Long gameroomid) {

        log.info("스포트라이트 - 게임방 아이디 : {}", gameroomid);
        gameService.spotlight(gameroomid);
    }
}
