package com.example.namoldak.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.socket.WebSocketSession;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

// 기능 : Redis에 저장되는 챗룸 안 참가자들 session ID 정보 객체
@Getter
@Setter
public class ChatRoom implements Serializable {
    // redis에 저장되는 객체들은 Serialize 가능해야함
    private static final long serialVersionUID = 6494678977089006639L;
    private Long roomId;
    private Map<String, WebSocketSession> clients = new HashMap<>();

    public ChatRoom(Long id) {
        this.roomId = id;
    }

//    public static ChatRoom create(Long roomId) {
//        ChatRoom chatRoom = new ChatRoom(roomId);
//        return chatRoom;
//    }

//    @Override
//    public boolean equals(final Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        final ChatRoom room = (ChatRoom) o;
//        return Objects.equals(getId(), room.getId()) &&
//                Objects.equals(getClients(), room.getClients());
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(getId(), getClients());
//    }
}
