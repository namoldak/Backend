package com.example.namoldak.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatMessage <T>{
    public enum MessageType {
        TALK, ENTER, OFFER, ICE, ANSWER
    }
    private String type;
    private String roomId; // 방번호
    private String sender; // 메시지 보낸사람
    private T content;
    private String message; // 메시지

    private String offer;
    private String ice;
    private String candidate;
    private String answer;
}
