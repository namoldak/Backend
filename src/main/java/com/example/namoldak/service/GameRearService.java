package com.example.namoldak.service;

import com.example.namoldak.domain.*;
import com.example.namoldak.dto.RequestDto.GameDto;
import com.example.namoldak.dto.ResponseDto.VictoryDto;
import com.example.namoldak.repository.*;
import com.example.namoldak.domain.GameMessage;
import com.example.namoldak.domain.Member;
import com.example.namoldak.util.GlobalResponse.CustomException;
import com.example.namoldak.util.GlobalResponse.code.StatusCode;
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
public class GameRearService{
    private final SimpMessageSendingOperations sendingOperations;
    private final GameRoomAttendeeRepository gameRoomAttendeeRepository;
    private final GameRoomRepository gameRoomRepository;
    private final MemberRepository memberRepository;
    private final GameStartSetRepository gameStartSetRepository;

    // 게임 강제 종료
    @Transactional
    public void forcedEndGame(Long roomId){

        // 현재 게임방 정보 불러오기
        Optional<GameRoom> enterGameRoom = gameRoomRepository.findById(roomId);

        // 발송할 메세지 데이터 저장
        GameMessage gameMessage = new GameMessage<>();
        gameMessage.setRoomId(Long.toString(roomId));
        gameMessage.setSenderId("");
        gameMessage.setSender("양계장 주인");
        gameMessage.setContent("게임 진행 가능한 최소 인원이 충족되지 못 하여 게임이 종료된닭.");
        gameMessage.setType(GameMessage.MessageType.ENDGAME);
        sendingOperations.convertAndSend("/sub/gameRoom/" + roomId, gameMessage);

        // Redis DB에서 게임 셋팅 삭제
        gameStartSetRepository.deleteById(roomId);

        // 현재 방 상태 정보를 true로 변경
        enterGameRoom.get().setStatus("true");
    }

    // 게임 정상 종료
    @Transactional
    public void endGame(Long gameRoomId){
        // 승리자와 패배자를 list로 반환할 DTO 생성
        VictoryDto victoryDto = new VictoryDto();

        // 방 게임셋 정보 불러오기
        GameStartSet gameStartSet = gameStartSetRepository.findById(gameRoomId).orElseThrow(
                ()-> new CustomException(StatusCode.GAME_SET_NOT_FOUND)
        );

        // 현재 게임룸 데이터 불러오기
        Optional<GameRoom> enterGameRoom = gameRoomRepository.findById(gameRoomId);

        // 불러온 게임룸으로 들어간 GameRoomMember들 구하기
        List<GameRoomAttendee> gameRoomAttendeeList = gameRoomAttendeeRepository.findByGameRoom(enterGameRoom);

        // 닉네임을 구하기 위해서 멤버 객체를 담을 리스트 선언
        List<Member> memberList = new ArrayList<>();

        // for문으로 하나씩 빼서 DB 조회 후 List에 넣어주기
        for (GameRoomAttendee gameRoomAttendee : gameRoomAttendeeList){
            Optional<Member> member = memberRepository.findById(gameRoomAttendee.getMember().getId());
            // 멤버 총 게임 횟수 증가
            member.get().updateTotalGame(1L);
            memberRepository.save(member.get());
            memberList.add(member.get());
        }

        // member의 닉네임이 정답자와 같지 않을 경우 전부 Loser에 저장하고 같을 경우 Winner에 저장
        for (Member member : memberList){
            if (!member.getNickname().equals(gameStartSet.getWinner())){
                victoryDto.setLoser(member.getNickname());
                // 멤버 패배 기록 추가
                member.updateLoseNum(1L);
                memberRepository.save(member);
            } else {
                victoryDto.setWinner(member.getNickname());
                // 멤버 승리 기록 추가
                member.updateWinNum(1L);
                memberRepository.save(member);
            }
        }

        // 발송할 메세지 데이터 저장
        GameMessage gameMessage = new GameMessage();
        gameMessage.setRoomId(Long.toString(gameRoomId));
        gameMessage.setSender("양계장 주인");
        gameMessage.setContent(victoryDto);
        gameMessage.setType(GameMessage.MessageType.ENDGAME);
        sendingOperations.convertAndSend("/sub/gameRoom/" + gameRoomId, gameMessage);

        // DB에서 게임 셋팅 삭제
        gameStartSetRepository.deleteById(gameRoomId);

        // 현재 방 상태 정보를 true로 변경
        enterGameRoom.get().setStatus("true");
    }

    // 정답
    @Transactional
    public void gameAnswer(Long gameRoomId, GameDto gameDto){
        // 모달창에 작성한 정답
        String answer = gameDto.getAnswer().replaceAll(" ", "");
        log.info("============================== 정답 : " + answer);

        // gameStartSet 불러오기
        GameStartSet gameStartSet = gameStartSetRepository.findById(gameRoomId).orElseThrow(
                ()-> new CustomException(StatusCode.GAME_SET_NOT_FOUND)
        );

        GameMessage gameMessage = new GameMessage();

        // 정답을 맞추면 게임 끝
        if (gameStartSet.getKeywordToMember().get(gameDto.getNickname()).equals(answer)){

            // 정답자
            gameStartSet.setWinner(gameDto.getNickname());
            gameStartSetRepository.save(gameStartSet);

            // stomp로 메세지 전달
            gameMessage.setRoomId(Long.toString(gameRoomId));
            gameMessage.setSender("양계장 주인");
            gameMessage.setContent(gameDto.getNickname() + "님이 작성하신" + answer + "은(는) 정답입니닭!");
            gameMessage.setNickname(gameDto.getNickname());
            gameMessage.setType(GameMessage.MessageType.SUCCESS);

            // 방 안의 구독자 모두가 메세지 받음
            sendingOperations.convertAndSend("/sub/gameRoom/" + gameRoomId, gameMessage);
        } else {
            // stomp로 메세지 전달
            gameMessage.setRoomId(Long.toString(gameRoomId));
            gameMessage.setSender("양계장 주인");
            gameMessage.setContent(gameDto.getNickname() + "님이 작성하신" + answer + "은(는) 정답이 아닙니닭!");
            gameMessage.setNickname(gameDto.getNickname());
            gameMessage.setType(GameMessage.MessageType.FAIL);

            sendingOperations.convertAndSend("/sub/gameRoom/" + gameRoomId, gameMessage);
        }
    }
}
