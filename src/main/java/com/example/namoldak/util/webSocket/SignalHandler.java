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
import java.util.stream.Collectors;

import static com.example.namoldak.util.GlobalResponse.code.StatusCode.CHAT_ROOM_NOT_FOUND;

// 기능 : WebRTC를 위한 시그널링 서버 부분으로 요청타입에 따라 분기 처리
@Slf4j
@Component
public class SignalHandler extends TextWebSocketHandler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

//    private final SessionRepository sessionRepositoryRepo = SessionRepository.getInstance();  // 세션 데이터 저장소
    // 세션 저장 1) clientsInRoom : 방 Id를 key 값으로 하여 방마다 가지고 있는 Client들의 session Id 와 session 객체를 저장
    private final Map<Long, Map<String, WebSocketSession>> clientsInRoom = new HashMap<>();
    // 세션 저장 2) roomIdToSession : 참가자들 각각의 데이터로 session 객체를 key 값으로 하여 해당 객체가 어느방에 속해있는지를 저장
    private final Map<WebSocketSession, Long> roomIdToSession = new HashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String MSG_TYPE_JOIN_ROOM = "join_room";
    private static final String MSG_TYPE_OFFER = "offer";
    private static final String MSG_TYPE_ANSWER = "answer";
    private static final String MSG_TYPE_CANDIDATE = "candidate";

    @Override
    public void afterConnectionEstablished(final WebSocketSession session) {
        // 웹소켓이 연결되면 실행되는 메소드
        log.info("======================================== 웹소켓 연결 : {}", session);
    }

    @Override
    protected void handleTextMessage(final WebSocketSession session, final TextMessage textMessage) {

        try {
            WebSocketMessage message = objectMapper.readValue(textMessage.getPayload(), WebSocketMessage.class);
            String userName = message.getSender();
            String data = message.getData();
            Long roomId = message.getRoomId();

            log.info("======================================== origin message INFO");
            log.info("============== session.Id : {}, getType : {},  getRoomId : {}", session.getId(), message.getType(), roomId.toString());

            switch (message.getType()) {
                // 처음 입장
                case MSG_TYPE_JOIN_ROOM:

//                    if (sessionRepositoryRepo.hasRoom(roomId)) {
                    if (clientsInRoom.containsKey(roomId)) {
                        log.info("==========join 0 : 방 있음 : {} ", roomId);
//                        log.info("==========join 1 : (join 전) Client List - {}", sessionRepositoryRepo.getClientList(roomId));
                        log.info("==========join 1 : (join 전) Client List - {}", clientsInRoom.get(roomId));


                        // 해당 챗룸이 존재하면
                        // 세션 저장 1) : 게임방 안의 session List에 새로운 Client session정보를 저장
//                        sessionRepositoryRepo.addClient(roomId, session);
                        clientsInRoom.get(roomId).put(session.getId(), session);;

                    } else {
                        log.info("==========join 0 : 방 없음 : {}", roomId);
                        // 해당 챗룸이 존재하지 않으면
                        // 세션 저장 1) : 새로운 게임방 정보와 새로운 Client session정보를 저장
//                        sessionRepositoryRepo.addClientInNewRoom(roomId, session);
                        Map<String, WebSocketSession> newClient = new HashMap<>();
                        newClient.put(session.getId(), session);
                        clientsInRoom.put(roomId, newClient);
                    }

//                    log.info("==========join 2 : (join 후) Client List - {}", sessionRepositoryRepo.getClientList(roomId));
                    log.info("==========join 2 : (join 후) Client List - {}", clientsInRoom.get(roomId));


                    // 세션 저장 2) : 이 세션이 어느 방에 들어가 있는지 저장
//                    sessionRepositoryRepo.saveRoomIdToSession(session, roomId);
                    roomIdToSession.put(session, roomId);

//                    log.info("==========join 3 : 지금 세션이 들어간 방 : {}", sessionRepositoryRepo.getRoomId(session));
                    log.info("==========join 3 : 지금 세션이 들어간 방 : {}", roomIdToSession.get(session));

                    // 방안 참가자 중 자신을 제외한 나머지 사람들의 Session ID를 List로 저장
                    List<String> exportClientList = new ArrayList<>();
//                    for (Map.Entry<String, WebSocketSession> entry : sessionRepositoryRepo.getClientList(roomId).entrySet()) {
                    for (Map.Entry<String, WebSocketSession> entry : clientsInRoom.get(roomId).entrySet()) {
                        if (entry.getValue() != session) {
                            exportClientList.add(entry.getKey());
                        }
                    }

                    log.info("==========join 4 : allUsers로 Client List  :" + exportClientList);

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

//                    if (sessionRepositoryRepo.hasRoom(roomId)) {
                    if (clientsInRoom.containsKey(roomId)) {
//                        Map<String, WebSocketSession> clientList = sessionRepositoryRepo.getClientList(roomId);
                        Map<String, WebSocketSession> clientList = clientsInRoom.get(roomId);

                        log.info("=========={} 5 : 보내는 사람 - {}, 받는 사람 - {}" + message.getType(), session.getId(), message.getReceiver());

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

                default:
                    log.info("======================================== DEFAULT");
                    log.info("============== 들어온 타입 : " + message.getType());

            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void afterConnectionClosed(final WebSocketSession session, final CloseStatus status) {
        // 웹소켓 연결이 끊어지면 실행되는 메소드
        log.info("======================================== 웹소켓 연결 해제로 afterConnectionClosed 실행");
        // 끊어진 세션이 어느방에 있었는지 조회
//        Long roomId = sessionRepositoryRepo.getRoomId(session);
        Long roomId = roomIdToSession.get(session);
//        log.info("==========leave 1 : (삭제 전) Client List - {}" , sessionRepositoryRepo.getClientList(roomId));
        log.info("==========leave 1 : (삭제 전) Client List - {}" , clientsInRoom.get(roomId));

        // 1) 방 참가자들 세션 정보들 사이에서 삭제
//        sessionRepositoryRepo.deleteClient(roomId, session);
        Map<String, WebSocketSession> clientList = clientsInRoom.get(roomId);
        String removeKey = "";
        for(Map.Entry<String, WebSocketSession> oneClient : clientList.entrySet()){
            if(oneClient.getKey().equals(session.getId())){
                removeKey = oneClient.getKey();
            }
        }
        log.info("========== 지워질 session id : " + removeKey);
        clientList.remove(removeKey);

        // 끊어진 세션을 제외한 나머지 세션들을 다시 저장
        clientsInRoom.put(roomId, clientList);

//        log.info("==========leave 2 : (삭제 후) Client List - {}", sessionRepositoryRepo.getClientList(roomId));
        log.info("==========leave 2 : (삭제 후) Client List - {}", clientsInRoom.get(roomId));

//         log.info("==========leave 3 : (삭제 전) roomId to Session - {}", sessionRepositoryRepo.searchRooIdToSessionList(roomId));
        log.info("==========leave 3 : (삭제 전) roomId to Session - {}",
                roomIdToSession.entrySet()
                        .stream()
                        .filter(entry ->  entry.getValue() == roomId)
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

        // 2) 별도 해당 참가자 세션 정보도 삭제
//        sessionRepositoryRepo.deleteRoomIdToSession(session);
        roomIdToSession.remove(session);

//       log.info("==========leave 4 : (삭제 후) roomId to Session - {}", sessionRepositoryRepo.searchRooIdToSessionList(roomId));
        log.info("==========leave 4 : (삭제 후) roomId to Session - {}",
                roomIdToSession.entrySet()
                .stream()
                .filter(entry ->  entry.getValue() == roomId)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

        // 본인 제외 모두에게 전달
//        for(Map.Entry<String, WebSocketSession> oneClient : sessionRepositoryRepo.getClientList(roomId).entrySet()){
        for(Map.Entry<String, WebSocketSession> oneClient : clientsInRoom.get(roomId).entrySet()){
            sendMessage(oneClient.getValue(),
                    new WebSocketResponseMessage().builder()
                            .type("leave")
                            .sender(session.getId())
                            .receiver(oneClient.getKey())
                            .build());
        }
    }

    // 메세지 발송
    private void sendMessage(WebSocketSession session, WebSocketResponseMessage message) {
        try {
            String json = objectMapper.writeValueAsString(message);
            log.info("=========={} 발송 to : {} ", message.getType(), session.getId());
            session.sendMessage(new TextMessage(json));
        } catch (IOException e) {
            log.info("============== 발생한 에러 메세지: {}", e.getMessage());
        }
    }
}

