package com.example.namoldak.service;

import com.example.namoldak.domain.ChatMessage;
import com.example.namoldak.util.GlobalResponse.CustomException;
import com.example.namoldak.util.GlobalResponse.code.StatusCode;
import com.google.common.util.concurrent.RateLimiter;
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
    private RateLimiter rateLimiter = RateLimiter.create(4.0);


    public void meesage(ChatMessage message) {
        if (rateLimiter.tryAcquire()) {
            ChatMessage exportMessage;
            exportMessage = ChatMessage.builder()
                    .type(message.getType())
                    .sender(message.getSender())
                    .message(message.getMessage())
                    .build();
            sendingOperations.convertAndSend("/sub/gameRoom/" + message.getRoomId(), exportMessage);
            log.info("===================== 정상 출력");
        } else {
            log.info("==================== 그만 쳐라");
            throw new CustomException(StatusCode.ACCESS_DENIED);
        }
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
