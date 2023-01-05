package com.example.namoldak.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GameMessage<T> {

    private String roomId;
    private String senderId;
    private String sender;
    private T content;
    private GameMessage.MessageType type;

    public enum MessageType {
        OWNER, ENTER, RULE, READY, START, KEYWORD,
        FAIL, SKIP, SUCCESS, WINNER, ENDGAME,
        LEAVE, NEWOWNER
    }
}