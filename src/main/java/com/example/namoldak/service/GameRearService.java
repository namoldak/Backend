package com.example.namoldak.service;

import com.example.namoldak.domain.GameMessage;
import com.example.namoldak.domain.GameRoomMember;
import com.example.namoldak.domain.GameStartSet;
import com.example.namoldak.domain.Member;
import com.example.namoldak.dto.RequestDto.AnswerDto;
import com.example.namoldak.repository.GameRoomMemberRepository;
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
    private final GameStartSetRepository gameStartSetRepository;
    private final SimpMessageSendingOperations sendingOperations;
    private final GameRoomMemberRepository gameRoomMemberRepository;

    @Transactional
    public void isAnswer(Long roomId, Member member, AnswerDto answerDto){
        // 정답으로 뭐 입력했는지 DTO에서 가져와서 저장
        String answer = answerDto.getAnswer();

        // 방 Id로 GameStartSet에 저장되어있는 데이터 불러와서 저장
        GameStartSet gameStartSet = gameStartSetRepository.findByRoomId(roomId);
        GameRoomMember gameRoomMember = gameRoomMemberRepository.findByMember(member);

        // 입력한 값과 정답이 일치할 경우
        if (gameStartSet.getKeyword().equals(answer)){
        // 보낼 메시지 데이터 넣기
        GameMessage gameMessage = new GameMessage<>();
        gameMessage.setRoomId(Long.toString(roomId));
        gameMessage.setSender(member.getNickname());
        gameMessage.setSenderId(String.valueOf(gameRoomMember.getGameRoomMemberId()));
        gameMessage.setContent(member.getNickname() + " 님이 입력하신 " + answer + " 은(는) 정답입니다!");
        gameMessage.setType(GameMessage.MessageType.ANSWER);

        // 메세지 발송
        sendingOperations.convertAndSend("/sub/gameroom" + roomId, gameMessage);

        // 입력한 값과 정답이 일치하지 않을 경우
        } else if (!gameStartSet.getKeyword().equals(answer)) {
            GameMessage gameMessage = new GameMessage<>();
            gameMessage.setRoomId(Long.toString(roomId));
            gameMessage.setSender(member.getNickname());
            gameMessage.setSenderId(String.valueOf(gameRoomMember.getGameRoomMemberId()));
            gameMessage.setContent(member.getNickname() + " 님이 입력하신 " + answer + " 은(는) 정답이 아닙니다!");
            gameMessage.setType(GameMessage.MessageType.ANSWER);

            // 메세지 발송
            sendingOperations.convertAndSend("/sub/gameroom" + roomId, gameMessage);
        }
    }

    @Transactional
    public void endGame(Long roomId){

    }
}
