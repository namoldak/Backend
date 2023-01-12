//package com.example.namoldak.repository;
//
//import com.example.namoldak.domain.ChatRoom;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.redis.core.HashOperations;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.stereotype.Repository;
//
//import javax.annotation.PostConstruct;
//import java.util.List;
//
//// 기능 : Redis안에 저장되는 챗룸 레포로 key값은 Room ID, value값은 해당 챗룸의 모든 참가자들의 name과 session이 저장된 객체
//@Slf4j
//@Repository
//@RequiredArgsConstructor
//public class ChatRoomRepository {
//    private static final String CHAT_ROOMS = "CHAT_ROOM";
//    private final RedisTemplate<String, Object> redisTemplate;
//    private HashOperations<String, Long, ChatRoom> opsHashChatRoom;
//
//    @PostConstruct
//    private void init() {
//        opsHashChatRoom = redisTemplate.opsForHash();
//    }
//
//    // 모든 채팅방 조회
//    public List<ChatRoom> findAllRoom() {
//        return opsHashChatRoom.values(CHAT_ROOMS);
//    }
//
//    // 특정 채팅방 조회
//    public ChatRoom findRoomById(Long id) {
//        return opsHashChatRoom.get(CHAT_ROOMS, id);
//    }
//
//    // 채팅룸 생성
//    public ChatRoom saveChatRoom(ChatRoom chatRoom){
//        opsHashChatRoom.put(CHAT_ROOMS, chatRoom.getRoomId(), chatRoom);
//        return chatRoom;
//    }
//    // 채팅룸 삭제
//    public void deleteRoom(String chatRoomId){
//        opsHashChatRoom.delete(CHAT_ROOMS, chatRoomId);
//    }
//}
