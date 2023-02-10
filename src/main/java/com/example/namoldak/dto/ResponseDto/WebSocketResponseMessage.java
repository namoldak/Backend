package com.example.namoldak.dto.ResponseDto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;

// 기능 : 프론트에 응답하는 시그널링용 Message
@Builder
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
public class WebSocketResponseMessage {
    private String sender;
    private String senderNickName;
    private String type;
    private String data;
    private Long roomId;
    private List<String> allUsers;
    private Map<String, String> allUsersNickNames;
    private String receiver;
    private Object offer;
    private Object answer;
    private Object candidate;
    private Object sdp;
}
