package com.example.namoldak.controller;

import com.example.namoldak.domain.ChatMessage;
import com.example.namoldak.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RestController;

// 기능 : 채팅 컨트롤러
@RequiredArgsConstructor
@RestController
@Slf4j
public class ChatController {

    private final ChatService chatService;

    // stomp 채팅용 요청 URL
    @MessageMapping("/chat/message")
    public void message(ChatMessage message) {
        chatService.meesage(message);
    }

    @MessageMapping("/chat/camera")
    public void cameraControl(ChatMessage message) {
        chatService.cameraControl(message);
    }
}
