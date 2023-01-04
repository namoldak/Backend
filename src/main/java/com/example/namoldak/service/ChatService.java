package com.example.namoldak.service;

import com.example.namoldak.domain.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ChatService {

    // Redis를 이용하는 방법에는 두가지가 있다 RedisTemplate, RedisRepository
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic channelTopic;

    public void meesage(ChatMessage message) {


        // Websocket에 발행된 메시지를 redis로 발행(publish) 매개인자1: 채널, 매개인자2 : 메세지, 해당 채널을 구독하는 모두에게 메세지를 전달
        redisTemplate.convertAndSend(channelTopic.getTopic(), message);
    }
}
