package com.example.namoldak.service;

import com.example.namoldak.domain.GameMessage;
import com.example.namoldak.domain.GameStartSet;
import com.example.namoldak.domain.Member;
import com.example.namoldak.dto.RequestDto.AnswerDto;
import com.example.namoldak.repository.GameStartSetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class GameRearService {

    private final SimpMessageSendingOperations messagingTemplate;
    private final GameStartSetRepository gameStartSetRepository;

    // 정답
    @Transactional
    public void gameAnswer(Member member, Long gameroomid, AnswerDto answerDto) {

        // 모달창에 작성한 정답
        String answer = answerDto.getAnswer();

        // gameStartSet 불러오기
        GameStartSet gameStartset = gameStartSetRepository.findByRoomId(gameroomid);

        GameMessage gameMessage = new GameMessage();

        // 정답을 맞추면 게임 끝
        if (gameStartset.getKeyword().equals(answerDto.getAnswer())) {

            // 정답자
            gameStartset.setWinner(member.getNickname());

            // stomp로 메세지 전달
            gameMessage.setRoomId(Long.toString(gameroomid));
            gameMessage.setSenderId(String.valueOf(member.getId()));
            gameMessage.setSender(member.getNickname());
            gameMessage.setContent(gameMessage.getSender() + "님이 작성하신" + answer + "은(는) 정답입니다!");
            gameMessage.setType(GameMessage.MessageType.SUCCESS);

            // 방 안의 구독자 모두가 메세지 받음
            messagingTemplate.convertAndSend("/sub/gameroom/" + gameroomid, gameMessage);
        } else {
            // stomp로 메세지 전달
            gameMessage.setRoomId(Long.toString(gameroomid));
            gameMessage.setSenderId(String.valueOf(member.getId()));
            gameMessage.setSender(member.getNickname());
            gameMessage.setContent(gameMessage.getSender() + "님이 작성하신" + answer + "은(는) 정답이 아닙니다.");
            gameMessage.setType(GameMessage.MessageType.FAIL);

            messagingTemplate.convertAndSend("/sub/gameroom/" + gameroomid, gameMessage);
        }
    }
}
