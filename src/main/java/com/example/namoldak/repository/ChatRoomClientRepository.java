package com.example.namoldak.repository;

import com.example.namoldak.domain.ChatRoom;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.PostConstruct;

// 기능 : Redis안에 저장되는 챗 참가자들의 레포로 key값은 session ID, value값은 Room ID
//       해당 참가자의 방이 어딘지 찾을때 사용
@RequiredArgsConstructor
@Repository
public class ChatRoomClientRepository {
    private static final String CHAT_ROOM_CLIENT = "CHAT_ROOM_CLIENT";
    private final RedisTemplate<String, Object> redisTemplate;
    private HashOperations<String, String, ChatRoom> opsHashChatRoomClient;

    @PostConstruct
    private void init() {
        opsHashChatRoomClient = redisTemplate.opsForHash();
    }

    // 특정 Client 조회
    public ChatRoom findClientBySessionId(String sessionId) {
        return opsHashChatRoomClient.get(CHAT_ROOM_CLIENT, sessionId);
    }

    // Client 정보 저장 Key값은 session Id, value값은 chatRoom 객체
    public ChatRoom saveClient(String sessionId, ChatRoom chatRoom){
        opsHashChatRoomClient.put(CHAT_ROOM_CLIENT, sessionId, chatRoom);
        return chatRoom;
    }
    // 채팅룸 삭제
    public void deleteClient(String sessionId){
        opsHashChatRoomClient.delete(CHAT_ROOM_CLIENT, sessionId);
    }
}
