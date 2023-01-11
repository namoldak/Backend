package com.example.namoldak.controller;

import com.example.namoldak.domain.ChatMessage;
import com.example.namoldak.domain.ChatRoom;
import com.example.namoldak.service.ChatRoomService;
import com.example.namoldak.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 기능 : 채팅 컨트롤러
@RequiredArgsConstructor
@RestController
public class ChatController {

    private final ChatService chatService;
    private final ChatRoomService chatRoomService;

    @MessageMapping("/chat/message")
    public void message(ChatMessage message) {
        chatService.meesage(message);
    }

    @PostMapping("/test/chatRoom/{roomId}")
    public void creatChatRoom(@PathVariable Long roomId){
        chatRoomService.createChatRoom(roomId);
    }

    @GetMapping("/test/chatRoom/{roomId}")
    public HashMap<String, String> showChatRoom(@PathVariable Long roomId) throws JSONException {
        HashMap<String, String> objDatas = chatRoomService.findChatRoomById(roomId).getClients();
        return objDatas;
    }
}
