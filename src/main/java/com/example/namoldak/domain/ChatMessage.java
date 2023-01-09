package com.example.namoldak.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

// 기능 : 프론트부터 들어오는 Websocket 메세지와 서버로부터 프론트로 전달하는 Websocket 메세지 Dto
@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatMessage <T> {
    public enum MessageType {
        TALK, ENTER, OFFER, ICE, ANSWER
    }

    private String type;         // 메세지 타입
    private String roomId;       // 방번호
    private String sender;       // 메시지 보낸사람
    private String message;      // 메시지

    // 시그널링 타입
    private String offer;
    private String ice;
    private String candidate;
    private String answer;
}
