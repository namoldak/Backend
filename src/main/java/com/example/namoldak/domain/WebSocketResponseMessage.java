package com.example.namoldak.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.json.JSONObject;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;
import java.util.Map;
import java.util.Objects;

// 기능 : 프론트에 응답하는 시그널링용 Message
@Builder
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
public class WebSocketResponseMessage {
    private String sender;
    private String type;
    private String data;
    private Long roomId;
    private List<String> allUsers;
    private String receiver;
    private Object offer;
    private Object answer;
    private Object candidate;
    private Object sdp;

    public void setFrom(String from) {
        this.sender = from;
    }
    public void setType(String type) {
        this.type = type;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setOffer(Objects offer) {
        this.offer = offer;
    }
    public void setCandidate(Object candidate) {
        this.candidate = candidate;
    }

    public void setSdp(Object sdp) {
        this.sdp = sdp;
    }
}
