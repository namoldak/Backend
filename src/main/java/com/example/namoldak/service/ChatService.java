package com.example.namoldak.service;

import com.example.namoldak.domain.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

// 기능 : 시그널링 서버 역할 하는 서비스
@Slf4j
@RequiredArgsConstructor
@Service
public class ChatService {
    private final SimpMessageSendingOperations sendingOperations;

    public void meesage(ChatMessage message) {

        ChatMessage exportMessage;

        switch (message.getType()){
            case "ENTER":
                exportMessage = ChatMessage.builder()
                        .type(message.getType())
                        .sender(message.getSender())
                        .message("[공지] " + message.getSender() + "님이 입장하셨습니다.")
                        .build();

                sendingOperations.convertAndSend("/sub/gameroom/" + message.getRoomId(), exportMessage);
                break;

            case "ICE":
                exportMessage = ChatMessage.builder()
                        .type(message.getType())
                        .sender(message.getSender())
                        .ice(message.getIce())
                        .build();

                sendingOperations.convertAndSend("/sub/gameroom/" + message.getRoomId(), exportMessage);
                break;

            case "OFFER":
                exportMessage = ChatMessage.builder()
                        .type(message.getType())
                        .sender(message.getSender())
                        .offer(message.getOffer())
                        .build();

                sendingOperations.convertAndSend("/sub/gameroom/" + message.getRoomId(), exportMessage);
                break;

            case "ANSWER":
                exportMessage = ChatMessage.builder()
                        .type(message.getType())
                        .sender(message.getSender())
                        .answer(message.getAnswer())
                        .build();

                sendingOperations.convertAndSend("/sub/gameroom/" + message.getRoomId(), exportMessage);
                break;

            default:
                sendingOperations.convertAndSend("/sub/gameroom/" + message.getRoomId(), message);
        }
    }
}
