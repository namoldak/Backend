package com.example.namoldak.controller;

import com.example.namoldak.dto.RequestDto.AnswerDto;
import com.example.namoldak.service.GameRearService;
import com.example.namoldak.util.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

@RequiredArgsConstructor
@Controller
public class GameRearController {
    private final GameRearService gameRearService;

    @MessageMapping("/lier/game/{roomId}/isAnswer")
    public void isAnswer(@DestinationVariable("roomId") Long roomId, @AuthenticationPrincipal UserDetailsImpl userDetails, AnswerDto answerDto){
        gameRearService.isAnswer(roomId, userDetails.getMember(), answerDto);
    }
}
