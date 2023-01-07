package com.example.namoldak.service;

import com.example.namoldak.domain.ChatRoom;
import com.example.namoldak.dto.ResponseDto.PrivateResponseBody;
import com.example.namoldak.repository.ChatRoomRepository;
import com.example.namoldak.util.GlobalResponse.code.StatusCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

// 기능 : 챗룸 생성 서비스
@RequiredArgsConstructor
@Service
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;

    public ResponseEntity<PrivateResponseBody> createChatRoom(String roomId, String name){

        ChatRoom chatRoom = ChatRoom.create(name, roomId);
        return new ResponseEntity<>(new PrivateResponseBody
                (StatusCode.OK, chatRoomRepository.saveRoom(chatRoom)), HttpStatus.OK);
    }
}
