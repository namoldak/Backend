package com.example.namoldak.util.webSocket;

import com.example.namoldak.domain.GameRoomAttendee;
import com.example.namoldak.domain.Member;
import com.example.namoldak.repository.SessionRepository;
import com.example.namoldak.dto.RequestDto.WebSocketMessage;
import com.example.namoldak.dto.ResponseDto.WebSocketResponseMessage;
import com.example.namoldak.service.GameRoomService;
import com.example.namoldak.service.RepositoryService;
import com.example.namoldak.util.GlobalResponse.CustomException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.websocket.SessionException;
import java.io.IOException;
import java.util.*;

import static com.example.namoldak.util.GlobalResponse.code.StatusCode.CHAT_ROOM_NOT_FOUND;

// 기능 : WebRTC를 위한 시그널링 서버 부분으로 요청타입에 따라 분기 처리
@Slf4j
@Component
public class SignalHandler extends TextWebSocketHandler {

    @Autowired
    private GameRoomService gameRoomService;
    @Autowired
    private RepositoryService repositoryService;
    private final SessionRepository sessionRepository = SessionRepository.getInstance();  // 세션 데이터 저장소
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String MSG_TYPE_JOIN_ROOM = "join_room";
    private static final String MSG_TYPE_OFFER = "offer";
    private static final String MSG_TYPE_ANSWER = "answer";
    private static final String MSG_TYPE_CANDIDATE = "candidate";

    @Override
    public void afterConnectionEstablished(final WebSocketSession session) {
        // 웹소켓이 연결되면 실행되는 메소드
    }

    // 시그널링 처리 메소드
    @Override
    protected void handleTextMessage(final WebSocketSession session, final TextMessage textMessage) {

        try {
            WebSocketMessage message = objectMapper.readValue(textMessage.getPayload(), WebSocketMessage.class);
            String userName = message.getSender();
            Long roomId = message.getRoomId();

            switch (message.getType()) {
                // 처음 입장
                case MSG_TYPE_JOIN_ROOM:

                    if (sessionRepository.hasRoom(roomId)) {
                        // 해당 챗룸이 존재하면
                        // 세션 저장 1) : 게임방 안의 session List에 새로운 Client session정보를 저장
                        sessionRepository.addClient(roomId, session);
                    } else {
                        // 해당 챗룸이 존재하지 않으면
                        // 세션 저장 1) : 새로운 게임방 정보와 새로운 Client session정보를 저장
                        sessionRepository.addClientInNewRoom(roomId, session);
                    }

                    // 세션 저장 2) : 이 세션이 어느 방에 들어가 있는지 저장
                    sessionRepository.saveRoomIdToSession(session, roomId);

                    // 세션 저장 3) : 방 안에 닉네임들 저장
                    sessionRepository.addNicknameInRoom(session.getId(), message.getNickname());

                    Map<String, WebSocketSession> joinClientList = sessionRepository.getClientList(roomId);

                    // 방안 참가자 중 자신을 제외한 나머지 사람들의 Session ID를 List로 저장
                    List<String> exportSessionList = new ArrayList<>();
                    for (Map.Entry<String, WebSocketSession> entry : joinClientList.entrySet()) {
                        if (entry.getValue() != session) {
                            exportSessionList.add(entry.getKey());
                        }
                    }

                    Map<String, String> exportNicknameList = new HashMap<>();
                    for (Map.Entry<String, WebSocketSession> entry : joinClientList.entrySet()) {
                        if (entry.getValue() != session) {
                            exportNicknameList.put(entry.getKey(), sessionRepository.getNicknameInRoom(entry.getKey()));
                        }
                    }

                    // 접속한 본인에게 방안 참가자들 정보를 전송
                    sendMessage(session,
                            new WebSocketResponseMessage().builder()
                                    .type("all_users")
                                    .sender(userName)
                                    .data(message.getData())
                                    .allUsers(exportSessionList)
                                    .allUsersNickNames(exportNicknameList)
                                    .candidate(message.getCandidate())
                                    .sdp(message.getSdp())
                                    .build());

                    break;

                case MSG_TYPE_OFFER:
                case MSG_TYPE_ANSWER:
                case MSG_TYPE_CANDIDATE:

                    if (sessionRepository.hasRoom(roomId)) {
                        Map<String, WebSocketSession> oacClientList = sessionRepository.getClientList(roomId);

                        if (oacClientList.containsKey(message.getReceiver())) {
                            WebSocketSession ws = oacClientList.get(message.getReceiver());
                            if(!ws.isOpen()){
                                log.info("========================================== 끊겼나?");
                            }
                            sendMessage(ws,
                                    new WebSocketResponseMessage().builder()
                                            .type(message.getType())
                                            .sender(session.getId())            // 보낸사람 session Id
                                            .senderNickName(message.getNickname())
                                            .receiver(message.getReceiver())    // 받을사람 session Id
                                            .data(message.getData())
                                            .offer(message.getOffer())
                                            .answer(message.getAnswer())
                                            .candidate(message.getCandidate())
                                            .sdp(message.getSdp())
                                            .build());
                            log.info("3. =================================================== {}", message.getType());
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
            log.info("=================== SignalHandler Json처리 에러 : {} ", e.getMessage());
        }
    }

    // 웹소켓 연결이 끊어지면 실행되는 메소드
    @Override
    public void afterConnectionClosed(final WebSocketSession session, final CloseStatus status) {
        log.info("========================================= 도대체 왜 끊기는 거임 ㅆ");
        String nickname = sessionRepository.getNicknameInRoom(session.getId());
        // 끊어진 세션이 어느방에 있었는지 조회
        Long roomId = sessionRepository.getRoomId(session);

        // 1) 방 참가자들 세션 정보들 사이에서 삭제
        sessionRepository.deleteClient(roomId, session);

        // 2) 별도 해당 참가자 세션 정보도 삭제
        sessionRepository.deleteRoomIdToSession(session);

        // 3) 별도 해당 닉네임 리스트에서도 삭제
        sessionRepository.deleteNicknameInRoom(session.getId());

        // 본인 제외 모두에게 전달
        for(Map.Entry<String, WebSocketSession> oneClient : sessionRepository.getClientList(roomId).entrySet()){
            sendMessage(oneClient.getValue(),
                    new WebSocketResponseMessage().builder()
                            .type("leave")
                            .sender(session.getId())
                            .receiver(oneClient.getKey())
                            .build());
        }
        Optional<Member> member = repositoryService.findMemberByNickname(nickname);
        List<GameRoomAttendee> gameRoomAttendeeList = repositoryService.findAttendeeByRoomId(roomId);
        for(GameRoomAttendee gameRoomAttendee : gameRoomAttendeeList) {
            if(nickname.equals(gameRoomAttendee.getMemberNickname())){
                gameRoomService.roomExit(roomId, member.get());
            }
        }
    }

    // 메세지 발송
    private void sendMessage(WebSocketSession session, WebSocketResponseMessage message) {
        try {
            String json = objectMapper.writeValueAsString(message);
            synchronized (session){
                session.sendMessage(new TextMessage(json));
            }
//            ConcurrentWebSocketSessionDecorator cws = new ConcurrentWebSocketSessionDecorator(session, 20000, 2048*2024);
//            cws.sendMessage(new TextMessage(json));
        } catch (IOException e) {
            log.info("============== 발생한 에러 메세지: {}", e.getMessage());
        }
    }
}

