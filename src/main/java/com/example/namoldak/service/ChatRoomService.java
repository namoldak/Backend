//package com.example.namoldak.service;
//
//import com.example.namoldak.domain.ChatRoom;
//import com.example.namoldak.repository.ChatRoomRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//
//// 기능 : 챗룸 생성 서비스
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class ChatRoomService {
//    private final ChatRoomRepository chatRoomRepository;
//
//    public ChatRoom createChatRoom(Long roomId) {
//        ChatRoom chatRoom = new ChatRoom(roomId);
//        return chatRoomRepository.saveChatRoom(chatRoom);
//    }
//
//    public ChatRoom findChatRoomById(final Long id) {
//        return chatRoomRepository.findRoomById(id);
//    }
//}
