package com.example.namoldak.service;

import com.example.namoldak.domain.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final SimpMessageSendingOperations sendingOperations;
    private final ChannelTopic channelTopic;

    public void meesage(ChatMessage message) {

        log.info(message.getMessage());
        log.info(message.getRoomId());
        log.info(message.getSender());
        log.info(String.valueOf(message.getType()));
        log.info((String) message.getContent());
        // Websocket에 발행된 메시지를 redis로 발행(publish)
//        redisTemplate.convertAndSend(channelTopic.getTopic(), message);
        sendingOperations.convertAndSend("/sub/gameroom/" + message.getRoomId(), message);
    }
}
