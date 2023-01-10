package com.example.namoldak.service;

import com.example.namoldak.domain.ChatRoom;
import com.example.namoldak.repository.ChatRoomClientRepository;
import com.example.namoldak.repository.ChatRoomRepository;
import com.example.namoldak.util.webSocket.Parser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;


import java.util.*;

// 기능 : 챗룸 생성 서비스

@Slf4j
@Service
//@RequiredArgsConstructor
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomClientRepository chatRoomClientRepository;
    private final Parser parser;
    // repository substitution since this is a very simple realization
//    private final Set<ChatRoom> chatRooms = new TreeSet<>(Comparator.comparing(ChatRoom::getId));

    @Autowired
    public ChatRoomService(ChatRoomRepository chatRoomRepository, ChatRoomClientRepository chatRoomClientRepository, final Parser parser) {
        this.chatRoomRepository = chatRoomRepository;
        this.chatRoomClientRepository = chatRoomClientRepository;
        this.parser = parser;
    }

    public ChatRoom createChatRoom(Long roomId) {
        log.info("======================== 채팅방 생성 2 : " + roomId);
        ChatRoom chatRoom = new ChatRoom(roomId);
        log.info("======================== 채팅방 생성 3 : " + roomId);
        return chatRoomRepository.saveChatRoom(chatRoom);
    }

    public ChatRoom saveChatRoomToClient(String sessionId, ChatRoom chatRoom){
        return chatRoomClientRepository.saveClient(sessionId, chatRoom);
    }

    public ChatRoom findChatRoomById(final Long id) {
        // simple get() because of parser errors handling
        return chatRoomRepository.findRoomById(id);
    }

    public ChatRoom findChatRoomBySessionId(String sessionId){
        return chatRoomClientRepository.findClientBySessionId(sessionId);
    }

    public Map<String, WebSocketSession> getClients(final ChatRoom chatRoom) {
        return Optional.ofNullable(chatRoom)
                .map(r -> Collections.unmodifiableMap(r.getClients()))
                .orElse(Collections.emptyMap());
    }

    // 해당 챗룸에 이름과 세션을 추가
    public WebSocketSession addClientToChatRoom(final ChatRoom chatRoom, final String name, final WebSocketSession session) {
        return chatRoom.getClients().put(name, session);
    }
//
//    public Set<ChatRoom> getRooms() {
//        final TreeSet<ChatRoom> defensiveCopy = new TreeSet<>(Comparator.comparing(ChatRoom::getId));
//        defensiveCopy.addAll(chatRooms);
//        return defensiveCopy;
//    }
//
//    public Boolean addRoom(final ChatRoom chatRoom) {
//        return chatRooms.add(chatRoom);
//    }

    public WebSocketSession removeClientByName(final ChatRoom chatRoom, final String name) {
        return chatRoom.getClients().remove(name);
    }
}
