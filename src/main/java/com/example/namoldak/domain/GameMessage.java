package com.example.namoldak.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 기능 : 채팅에 적용되는 관리자 메세지용 Dto
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GameMessage<T> {

    private String roomId;
    private String senderId;
    private String sender;
    private String nickname;
    private T content;
    private GameMessage.MessageType type;

    public enum MessageType {
        JOIN, OWNER, ENTER, RULE, READY, START, KEYWORD, SPOTLIGHT,
        FAIL, SKIP, SUCCESS, WINNER, ENDGAME,
        LEAVE, NEWOWNER, END, REWARD, STUPID
    }
}
