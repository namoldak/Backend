package com.example.namoldak.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.json.JSONObject;

import java.util.List;
import java.util.Objects;

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
    private List<JSONObject> allUsers;
    //    private Map<String, WebSocketSession> allUsers;
    private Object offer;
    private Object candidate;
    private Object sdp;
//    public WebSocketMessage(){
//
//    }
//    public WebSocketMessage(String sender,
//                            String type,
//                            String data,
//                            List<JSONObject> allUsers,
//                            Object offer,
//                            Object candidate,
//                            Object sdp) {
//        this.sender = sender;
//        this.type = type;
//        this.data = data;
//        this.allUsers = allUsers;
//        this.offer = offer;
//        this.candidate = candidate;
//        this.sdp = sdp;
//    }

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

//    @Override
//    public boolean equals(final Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        final WebSocketMessage message = (WebSocketMessage) o;
//        return Objects.equals(getSender(), message.getSender()) &&
//                Objects.equals(getType(), message.getType()) &&
//                Objects.equals(getData(), message.getData()) &&
//                Objects.equals(getCandidate(), message.getCandidate()) &&
//                Objects.equals(getSdp(), message.getSdp());
//    }
//
//    @Override
//    public int hashCode() {
//
//        return Objects.hash(getSender(), getType(), getData(), getCandidate(), getSdp());
//    }
//
//    @Override
//    public String toString() {
//        return "WebSocketMessage{" +
//                "from='" + sender + '\'' +
//                ", type='" + type + '\'' +
//                ", data='" + data + '\'' +
//                ", candidate=" + candidate +
//                ", sdp=" + sdp +
//                '}';
//    }
}
