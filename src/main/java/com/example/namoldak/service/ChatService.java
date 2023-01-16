package com.example.namoldak.service;

import com.example.namoldak.domain.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

// 기능 : 시그널링 서버 역할 하는 서비스
@Slf4j
@RequiredArgsConstructor
@Service
public class ChatService{
    private final SimpMessageSendingOperations sendingOperations;

    public void meesage(ChatMessage message){

        ChatMessage exportMessage;
        exportMessage = ChatMessage.builder()
                .type(message.getType())
                .sender(message.getSender())
                .message(message.getMessage())
                .build();

        sendingOperations.convertAndSend("/sub/gameRoom/" + message.getRoomId(), exportMessage);
    }

    public void cameraControl(ChatMessage message) {
        ChatMessage exportMessage;
        exportMessage = ChatMessage.builder()
                .type(message.getType())
                .nickname(message.getNickname())
                .build();

        sendingOperations.convertAndSend("/sub/gameRoom/" + message.getRoomId(), exportMessage);
    }
}
