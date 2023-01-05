package com.example.namoldak.service;

import com.example.namoldak.domain.GameMessage;
import com.example.namoldak.domain.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class GameService {

    private final SimpMessageSendingOperations messagingTemplate;

    // 건너뛰기
    @Transactional
    public void gameSkip(Member member, Long gameroomid) {

        // stomp로 메세지 전달
        GameMessage gameMessage = new GameMessage();
        gameMessage.setRoomId(Long.toString(gameroomid)); // 현재 방 id
        gameMessage.setSenderId(String.valueOf(member.getId())); // 로그인한 유저의 id
        gameMessage.setSender(member.getNickname()); // 로그인한 유저의 닉네임
        gameMessage.setContent(gameMessage.getSender() + "님이 건너뛰기를 선택하셨습니다.");
        gameMessage.setType(GameMessage.MessageType.SKIP);

        // 방 안의 구독자 모두가 메세지 받음
        messagingTemplate.convertAndSend("/sub/gameroom/" + gameroomid, gameMessage);
    }
}
