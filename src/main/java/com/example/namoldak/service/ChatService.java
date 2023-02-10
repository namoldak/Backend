package com.example.namoldak.service;

import com.example.namoldak.domain.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

// 기능 : 메세지 제어 및 카메라 제어
@Slf4j
@RequiredArgsConstructor
@Service
public class ChatService{
    private final SimpMessageSendingOperations sendingOperations;

    // 메세지 제어
    public void meesage(ChatMessage message) {
        ChatMessage exportMessage;
        exportMessage = ChatMessage.builder()
                .type(message.getType())
                .sender(message.getSender())
                .message(message.getMessage())
                .build();
        sendingOperations.convertAndSend("/sub/gameRoom/" + message.getRoomId(), exportMessage);
    }

    // 카메라 제어 (카메라를 끈 유저를 알기위한 API)
    public void cameraControl(ChatMessage message) {
        ChatMessage exportMessage;
        exportMessage = ChatMessage.builder()
                .type(message.getType())
                .nickname(message.getNickname())
                .build();

        sendingOperations.convertAndSend("/sub/gameRoom/" + message.getRoomId(), exportMessage);
    }
}
