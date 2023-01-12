package com.example.namoldak.util.webSocket;

import com.example.namoldak.repository.SessionRepository;
import com.example.namoldak.dto.RequestDto.WebSocketMessage;
import com.example.namoldak.dto.ResponseDto.WebSocketResponseMessage;
import com.example.namoldak.util.GlobalResponse.CustomException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import java.io.IOException;
import java.util.*;
import static com.example.namoldak.util.GlobalResponse.code.StatusCode.CHAT_ROOM_NOT_FOUND;

// 기능 : WebRTC를 위한 시그널링 서버 부분으로 요청타입에 따라 분기 처리
@Slf4j
@Component
public class SignalHandler extends TextWebSocketHandler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final SessionRepository sessionRepositoryRepo = SessionRepository.getInstance();  // 세션 데이터 저장소
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String MSG_TYPE_JOIN_ROOM = "join_room";
    private static final String MSG_TYPE_OFFER = "offer";
    private static final String MSG_TYPE_ANSWER = "answer";
    private static final String MSG_TYPE_CANDIDATE = "candidate";
    private static final String MSG_TYPE_LEAVE = "leave";

    @Override
    public void afterConnectionClosed(final WebSocketSession session, final CloseStatus status) {
        // 웹소켓 연결이 끊어지면 실행되는 메소드

        // 끊어진 세션이 어느방에 있었는지 조회
        Long roomId = sessionRepositoryRepo.getRoomId(session);
        // session 삭제
        removeSession(session, roomId);
    }

    @Override
    public void afterConnectionEstablished(final WebSocketSession session) {
        // 웹소켓이 연결되면 실행되는 메소드
    }

    @Override
    protected void handleTextMessage(final WebSocketSession session, final TextMessage textMessage) {

        try {
            WebSocketMessage message = objectMapper.readValue(textMessage.getPayload(), WebSocketMessage.class);
            String userName = message.getSender();
            String data = message.getData();
            Long roomId = message.getRoomId();

            log.info("======================================== origin message INFO");
            log.info("============== message.getType : " + message.getType());
            log.info("============== message.getRoomId : " + roomId.toString());

            switch (message.getType()) {
                // 처음 입장
                case MSG_TYPE_JOIN_ROOM:


                    if (sessionRepositoryRepo.hasRoom(roomId)) {
                        // 해당 챗룸이 존재하면
                        // 세션 저장 1) : 게임방 안의 session List에 새로운 Client session정보를 저장
                        sessionRepositoryRepo.addClient(roomId, session);

                    } else {
                        // 해당 챗룸이 존재하지 않으면
                        // 세션 저장 1) : 새로운 게임방 정보와 새로운 Client session정보를 저장
                        sessionRepositoryRepo.addClientInNewRoom(roomId, session);
                    }

                    // 세션 저장 2) : 이 세션이 어느 방에 들어가 있는지 저장
                    sessionRepositoryRepo.saveRoomIdToSession(session, roomId);

                    // 방안 참가자 중 자신을 제외한 나머지 사람들의 Session ID를 List로 저장
                    List<String> exportClientList = new ArrayList<>();
                    for (Map.Entry<String, WebSocketSession> entry : sessionRepositoryRepo.getClientList(roomId).entrySet()) {
                        if (entry.getValue() != session) {
                            exportClientList.add(entry.getKey());
                        }
                    }

                    // 접속한 본인에게 방안 참가자들 정보를 전송
                    sendMessage(session,
                            new WebSocketResponseMessage().builder()
                                    .type("all_users")
                                    .sender(userName)
                                    .data(message.getData())
                                    .allUsers(exportClientList)
                                    .candidate(message.getCandidate())
                                    .sdp(message.getSdp())
                                    .build());

                    break;

                case MSG_TYPE_OFFER:
                case MSG_TYPE_ANSWER:
                case MSG_TYPE_CANDIDATE:

                    if (sessionRepositoryRepo.hasRoom(roomId)) {
                        Map<String, WebSocketSession> clientList = sessionRepositoryRepo.getClientList(roomId);

                        if (clientList.containsKey(message.getReceiver())) {
                            WebSocketSession ws = clientList.get(message.getReceiver());
                            sendMessage(ws,
                                    new WebSocketResponseMessage().builder()
                                            .type(message.getType())
                                            .sender(session.getId())            // 보낸사람 session Id
                                            .receiver(message.getReceiver())    // 받을사람 session Id
                                            .data(message.getData())
                                            .offer(message.getOffer())
                                            .answer(message.getAnswer())
                                            .candidate(message.getCandidate())
                                            .sdp(message.getSdp())
                                            .build());
                        }
                    } else {
                        throw new CustomException(CHAT_ROOM_NOT_FOUND);
                    }
                    break;

                case MSG_TYPE_LEAVE:
                    // session 삭제
                    removeSession(session,roomId);
                    break;

                default:
                    log.info("======================================== DEFAULT");
                    log.info("============== 타입이 정의되지 않았습니다 : " + message.getType());

            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

        // 메세지 발송
        private void sendMessage(WebSocketSession session, WebSocketResponseMessage message) {
            try {
                String json = objectMapper.writeValueAsString(message);
                session.sendMessage(new TextMessage(json));
            } catch (IOException e) {
                log.info("============== 발생한 에러 메세지: " + e.getMessage());
            }
        }

        // 접속을 끊은 세션에 대해 2가지 데이터에서 삭제하고 해당 방 나머지 참가자들에게 끊긴 세션에 대한 정보를 전송
        public void removeSession(WebSocketSession session, Long roomId) {

            // 1) 방 참가자들 세션 정보들 사이에서 삭제
            sessionRepositoryRepo.deleteClient(roomId, session);

            // 2) 별도 해당 참가자 세션 정보도 삭제
            sessionRepositoryRepo.deleteRoomIdToSession(session);

            // 본인 제외 모두에게 전달
            Map<String, WebSocketSession> clientList = sessionRepositoryRepo.getClientList(roomId);
            for(Map.Entry<String, WebSocketSession> oneClient : clientList.entrySet()){
                sendMessage(oneClient.getValue(),
                        new WebSocketResponseMessage().builder()
                                .type("leave")
                                .sender(session.getId())
                                .receiver(oneClient.getKey())
                                .build());
            }
        }
}

