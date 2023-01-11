package com.example.namoldak.service;

import com.example.namoldak.domain.ChatRoom;
import com.example.namoldak.repository.ChatRoomClientRepository;
import com.example.namoldak.repository.ChatRoomRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// 기능 : 챗룸 생성 서비스
@Slf4j
@Service
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomClientRepository chatRoomClientRepository;

    @Autowired
    public ChatRoomService(ChatRoomRepository chatRoomRepository, ChatRoomClientRepository chatRoomClientRepository) {
        this.chatRoomRepository = chatRoomRepository;
        this.chatRoomClientRepository = chatRoomClientRepository;
    }

    public ChatRoom createChatRoom(Long roomId) {
        log.info("======================== 채팅방 생성 2 : " + roomId);
        ChatRoom chatRoom = new ChatRoom(roomId);
        log.info("======================== 채팅방 생성 3 : " + roomId);
        return chatRoomRepository.saveChatRoom(chatRoom);
    }

    public ChatRoom findChatRoomById(final Long id) {
        // simple get() because of parser errors handling
        return chatRoomRepository.findRoomById(id);
    }
}
