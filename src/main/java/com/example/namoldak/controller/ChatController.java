package com.example.namoldak.controller;

import com.example.namoldak.domain.ChatMessage;
import com.example.namoldak.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

// 기능 : 채팅 컨트롤러
@RequiredArgsConstructor
@Controller
public class ChatController {

    private final ChatService chatService;

    @MessageMapping("/chat/message")
    public void message(ChatMessage message) {
        chatService.meesage(message);
    }
}
