package com.example.namoldak.controller;

import com.example.namoldak.domain.ChatMessage;
import com.example.namoldak.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@RequiredArgsConstructor
@Controller
public class ChatController {

    private final ChatService chatService;
    @MessageMapping("/chat/message")                // 클라이언트에서 보내는 메시지를 매핑
    public void message(ChatMessage message) {
        chatService.meesage(message);
    }
}
