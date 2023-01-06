package com.example.namoldak.domain;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ChatRoom implements Serializable {
    // redis에 저장되는 객체들은 Serialize 가능해야함
    // 이 값을 지정해주지 않으면 컴파일러가 계산한 값이 부여됨(변동성있음) 게다가 컴파일러는 Serializable class 혹은 Outer Class를 참고하여 만들기 때문에 이 클래스가 변동이 되면
    // serialVersionUID도 변경이 있을 수 있음 (역시 변동성이 있음)
    // 이 UID가 달라지면 기존에 저장된 객체를 읽을 수가 없게 됨. (저장하는 쪽, 불러오는 쪽 컴파일러가 다를경우, 저장하는 시기의 클래스 내용과 불러오는 시기의 클래스의 내용이 다를 경우 등)
    // 데이터를 저장하는데 있어 이런 변동성은 위험하기에 serialVersionUID을 지정.
    private static final long serialVersionUID = 6494678977089006639L;
    private String roomId;
    private String name;

    public static ChatRoom create(String name, String roomId) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.roomId   = roomId;
        chatRoom.name     = name;
        return chatRoom;
    }
}
