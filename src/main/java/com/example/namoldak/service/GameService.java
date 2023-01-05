package com.example.namoldak.service;

import com.example.namoldak.domain.*;
import com.example.namoldak.repository.GameRoomMemberRepository;
import com.example.namoldak.repository.GameRoomRepository;
import com.example.namoldak.repository.GameStartSetRepository2;
import com.example.namoldak.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class GameService {

    private final SimpMessageSendingOperations messagingTemplate;

    private final MemberRepository memberRepository;
    private final GameRoomRepository gameRoomRepository;
    private final GameStartSetRepository2 gameStartSetRepository2;
    private final GameRoomMemberRepository gameRoomMemberRepository;

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

    public void spotlight(Long gameRoomId){
        // 게임룸 조회 (게임룸 상태를 조회하기 위한 조회)
        GameRoom playRoom = gameRoomRepository.findByGameRoomId(gameRoomId);

        // 해당 게임룸의 게임셋을 조회
        GameStartSet2 gameStartSet = gameStartSetRepository2.findGameSetById(gameRoomId);

        // 게임이 진행이 불가한 상태라면 초기화 시켜야 함
        if(playRoom.getStatus().equals("false")){ // false? true? 여튼 게임 진행이 아닌 상태
            gameStartSet.setRound(0);
            gameStartSet.setSpotNum(0);
            gameStartSetRepository2.saveGameSet(gameStartSet);
        }

        // 유저들 정보 조회
        List<GameRoomMember> memberListInGame = gameRoomMemberRepository.findByGameRoomOrderByCreatedAt(playRoom);

        // 라운드 진행 중
        if(gameStartSet.getSpotnum() < memberListInGame.size()){

            // 현재 스포트라이트 받는 멤버
//            Member spotMember = memberRepository.findById();

        } else if (gameStartSet.getSpotnum() == memberListInGame.size()){

        }


    }
}
