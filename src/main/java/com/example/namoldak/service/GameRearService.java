package com.example.namoldak.service;

import com.example.namoldak.domain.*;
import com.example.namoldak.dto.RequestDto.AnswerDto;
import com.example.namoldak.dto.ResponseDto.VictoryDto;
import com.example.namoldak.repository.*;

import com.example.namoldak.domain.GameMessage;
import com.example.namoldak.domain.GameStartSet;
import com.example.namoldak.domain.Member;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// 기능 : 게임 진행 부가 기능 서비스
@Slf4j
@RequiredArgsConstructor
@Service
public class GameRearService {
    private final GameStartSetRepository gameStartSetRepository;
    private final SimpMessageSendingOperations sendingOperations;
    private final GameRoomMemberRepository gameRoomMemberRepository;
    private final GameRoomRepository gameRoomRepository;
    private final MemberRepository memberRepository;

    // 게임 강제 종료
    @Transactional
    public void forcedEndGame(Long roomId) {

        // 현재 게임방 정보 불러오기
        Optional<GameRoom> enterGameRoom = gameRoomRepository.findById(roomId);

        // 게임방의 셋팅 정보 불러오기
        GameStartSet gameStartSet = gameStartSetRepository.findByRoomId(roomId);

        // 발송할 메세지 데이터 저장
        GameMessage gameMessage = new GameMessage<>();
        gameMessage.setRoomId(Long.toString(roomId));
        gameMessage.setSenderId("");
        gameMessage.setSender("");
        gameMessage.setContent("게임 진행 가능한 최소 인원이 충족되지 못 하여 게임이 종료됩니다.");
        gameMessage.setType(GameMessage.MessageType.ENDGAME);
        sendingOperations.convertAndSend("/sub/gameroom" + roomId, gameMessage);

        // DB에서 게임 셋팅 삭제
        gameStartSetRepository.delete(gameStartSet);

        // 현재 방 상태 정보를 true로 변경
        enterGameRoom.get().setStatus("true");
    }

    // 게임 정상 종료
    @Transactional
    public void endGame(Long roomId) {
        // 승리자와 패배자를 list로 반환할 DTO 생성
        VictoryDto victoryDto = new VictoryDto();

        // 방 게임셋 정보 불러오기
        GameStartSet gameStartSet = gameStartSetRepository.findByRoomId(roomId);

        // 현재 게임룸 데이터 불러오기
        Optional<GameRoom> enterGameRoom = gameRoomRepository.findById(roomId);

        // 불러온 게임룸으로 들어간 GameRoomMember들 구하기
        List<GameRoomMember> gameRoomMemberList = gameRoomMemberRepository.findByGameRoom(enterGameRoom);

        // 닉네임을 구하기 위해서 멤버 객체를 담을 리스트 선언
        List<Member> memberList = new ArrayList<>();

        // for문으로 하나씩 빼서 DB 조회 후 List에 넣어주기
        for (GameRoomMember gameRoomMember : gameRoomMemberList) {
            Optional<Member> member = memberRepository.findById(gameRoomMember.getMember_Id());
            memberList.add(member.get());
        }

        // member의 닉네임이 정답자와 같지 않을 경우 전부 Loser에 저장하고 같을 경우 Winner에 저장
        for (Member member : memberList) {
            if (member.getNickname() != (gameStartSet.getWinner())) {
                victoryDto.setLoser(member.getNickname());
            } else {
                victoryDto.setWinner(member.getNickname());
            }
        }

        // 발송할 메세지 데이터 저장
        GameMessage<VictoryDto> gameMessage = new GameMessage<>();
        gameMessage.setRoomId(Long.toString(roomId));
        gameMessage.setSenderId("");
        gameMessage.setSender("");
        gameMessage.setContent(victoryDto);
        gameMessage.setType(GameMessage.MessageType.ENDGAME);
        sendingOperations.convertAndSend("/sub/gameroom" + roomId, gameMessage);

        // DB에서 게임 셋팅 삭제
        gameStartSetRepository.delete(gameStartSet);

        // 현재 방 상태 정보를 true로 변경
        enterGameRoom.get().setStatus("true");
    }

    // 정답
    @Transactional
    public void gameAnswer(Member member, Long gameRoomId, AnswerDto answerDto) {

        // 모달창에 작성한 정답
        String answer = answerDto.getAnswer();

        // gameStartSet 불러오기
        GameStartSet gameStartset = gameStartSetRepository.findByRoomId(gameRoomId);

        GameMessage gameMessage = new GameMessage();

        // 정답을 맞추면 게임 끝
        if (gameStartset.getKeyword().equals(answerDto.getAnswer())) {

            // 정답자
            gameStartset.setWinner(member.getNickname());

            // stomp로 메세지 전달
            gameMessage.setRoomId(Long.toString(gameRoomId));
            gameMessage.setSenderId(String.valueOf(member.getId()));
            gameMessage.setSender(member.getNickname());
            gameMessage.setContent(gameMessage.getSender() + "님이 작성하신" + answer + "은(는) 정답입니다!");
            gameMessage.setType(GameMessage.MessageType.SUCCESS);

            // 방 안의 구독자 모두가 메세지 받음
            sendingOperations.convertAndSend("/sub/gameroom/" + gameRoomId, gameMessage);
        } else {
            // stomp로 메세지 전달
            gameMessage.setRoomId(Long.toString(gameRoomId));
            gameMessage.setSenderId(String.valueOf(member.getId()));
            gameMessage.setSender(member.getNickname());
            gameMessage.setContent(gameMessage.getSender() + "님이 작성하신" + answer + "은(는) 정답이 아닙니다.");
            gameMessage.setType(GameMessage.MessageType.FAIL);

            sendingOperations.convertAndSend("/sub/gameroom/" + gameRoomId, gameMessage);
        }
    }
}
