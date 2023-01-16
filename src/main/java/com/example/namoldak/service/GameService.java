package com.example.namoldak.service;

import com.example.namoldak.domain.*;
import com.example.namoldak.dto.RequestDto.AnswerDto;
import com.example.namoldak.repository.*;
import com.example.namoldak.util.GlobalResponse.CustomException;
import com.example.namoldak.util.GlobalResponse.code.StatusCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static com.example.namoldak.util.GlobalResponse.code.StatusCode.*;
import static com.example.namoldak.util.GlobalResponse.code.StatusCode.GAME_SET_NOT_FOUND;

// 기능 : 게임 진행 서비스
@Slf4j
@RequiredArgsConstructor
@Service
public class GameService {

    private final GameRoomRepository gameRoomRepository;
    private final GameRoomAttendeeRepository gameRoomAttendeeRepository;
    private final KeywordRepository keywordRepository;
    private final MemberRepository memberRepository;
    private final SimpMessageSendingOperations messagingTemplate;
    private final GameStartSetRepository gameStartSetRepository;
    private final GameRearService gameRearService;


    // 게임 시작
    @Transactional
    public void gameStart(Long gameRoomId, Member member) {

        // 현재 입장한 게임방의 정보를 가져옴
        GameRoom gameRoom = gameRoomRepository.findByGameRoomId(gameRoomId).orElseThrow(
                () -> new CustomException(StatusCode.NOT_FOUND_ROOM)
        );

//        // 게임 시작은 방장만이 할 수 있음
//        if (!member.getNickname().equals(gameRoom.getOwner())) {
//            throw new CustomException(StatusCode.UNAUTHORIZE);
//        }


        // 게임방에 입장한 멤버들 DB(GameRoomMember)에서 가져오기
        List<GameRoomAttendee> gameRoomAttendees = gameRoomAttendeeRepository.findByGameRoom(gameRoom);

        // 게임방의 상태를 start 상태로 업데이트
        gameRoom.setStatus("false");

        // 멤버들에게 뿌려지게 될 키워드 전체 목록 불러오기
//        List<Keyword> keywordList1 = keywordRepository.findAll();

        // 랜덤으로 키워드 하나 뽑기
//        Keyword keyword1 = keywordList1.get((int) (Math.random() * keywordList1.size()) + 1);

        // 위에서 랜덤으로 뽑은 키워드의 카테고리
//        String category = keyword1.getCategory();
        String category = Category.getRandom().name();

        // 같은 카테고리를 가진 키워드 리스트 만들기
        List<Keyword> keywordList;

        if (gameRoomAttendees.size() == 4) {
            // 참여 멤버가 4명 이라면, 랜덤으로 키워드 4장이 담긴 리스트를 만들어 준다.
            keywordList = keywordRepository.findTop4ByCategory(category);
        } else if (gameRoomAttendees.size() == 3) {
            // 참여 멤버가 3명 이라면, 랜덤으로 키워드 3장이 담긴 리스트를 만들어 준다.
            keywordList = keywordRepository.findTop3ByCategory(category);
        } else {
            throw new CustomException(NOT_ENOUGH_MEMBER);
        }

        HashMap<String, String> keywordToMember = new HashMap<>();

        List<Optional<Member>> memberList = new ArrayList<>();
        // 웹소켓으로 방에 참가한 인원 리스트 전달을 위한 리스트
        // 닉네임만 필요하기에 닉네임만 담음
        List<String> memberNicknameList = new ArrayList<>();

        for (GameRoomAttendee gameRoomAttendee : gameRoomAttendees) {
            memberNicknameList.add(gameRoomAttendee.getMemberNickname());
        }

        //게임룸 멤버한테 키워드 배당
        for (int i = 0; i < gameRoomAttendees.size(); i++) {
            keywordToMember.put(memberNicknameList.get(i), keywordList.get(i).getWord());
        }

        log.info("게임방 정보 : " + String.valueOf(gameRoomId));
        log.info("카테고리 정보 : " + category);
        log.info("멤버별 키워드 정보 : " + keywordToMember);

        GameStartSet gameStartSet = GameStartSet.builder()
                .roomId(gameRoomId)
                .category(category)
                .keywordToMember(keywordToMember)
                .round(0)
                .spotNum(0)
                .winner("")
                .build();

        log.info("게임방 정보 : " + String.valueOf(gameRoomId));
        log.info("카테고리 정보 : " + category);
        log.info("멤버별 키워드 정보 : " + keywordToMember);

        // StartSet 저장
        gameStartSetRepository.save(gameStartSet);

        log.info("게임방 정보 : " + String.valueOf(gameRoomId));
        log.info("카테고리 정보 : " + category);
        log.info("멤버별 키워드 정보 : " + keywordToMember);

        GameStartSet SearchOneGameStartSet = gameStartSetRepository.findById(gameRoomId).orElseThrow(
                ()-> new CustomException(GAME_SET_NOT_FOUND)
        );
        log.info("카테고리 : " + SearchOneGameStartSet.getCategory());
        for (String memberNick : memberNicknameList) {
            log.info("키워드 : " + keywordToMember.get(memberNick));
        }

        // 웹소켓으로 전달드릴 content 내용
        HashMap<String, Object> startSet = new HashMap<>();
        startSet.put("category", gameStartSet.getCategory()); // 카테고리
        startSet.put("keyword", gameStartSet.getKeywordToMember()); // 키워드
        startSet.put("memberList", memberNicknameList); // 방에 존재하는 모든 유저들


        GameMessage gameMessage = new GameMessage<>();
        gameMessage.setRoomId(Long.toString(gameRoomId)); // 현재 게임방 id
        gameMessage.setSenderId(""); // 준비된 유저의 id
        gameMessage.setSender("양계장 주인"); // 준비된 유저의 닉네임
        gameMessage.setContent(startSet); // 준비됐다는 내용
        gameMessage.setType(GameMessage.MessageType.START); // 메세지 타입

        // 게임 시작 알림을 방에 구독이 된 유저들에게 알려줌
        messagingTemplate.convertAndSend("/sub/gameRoom/" + gameRoomId, gameMessage);
    }

    // 건너뛰기
    @Transactional
    public void gameSkip(AnswerDto answerDto, Long gameRoomId) {

        // stomp로 메세지 전달
        GameMessage gameMessage = new GameMessage();
        gameMessage.setRoomId(Long.toString(gameRoomId)); // 현재 방 id
        gameMessage.setSender(answerDto.getNickname()); // 로그인한 유저의 닉네임
        gameMessage.setContent(gameMessage.getSender() + "님이 건너뛰기를 선택하셨습니다.");
        gameMessage.setType(GameMessage.MessageType.SKIP);

        // 방 안의 구독자 모두가 메세지 받음
        messagingTemplate.convertAndSend("/sub/gameRoom/" + gameRoomId, gameMessage);
    }

    public GameStartSet spotlight(Long gameRoomId) {

        GameRoom playRoom = gameRoomRepository.findByGameRoomId(gameRoomId).get();

        // 해당 게임룸의 게임셋을 조회
        GameStartSet gameStartSet = gameStartSetRepository.findById(gameRoomId).orElseThrow(
                ()-> new CustomException(GAME_SET_NOT_FOUND)
        );

        // 게임이 진행이 불가한 상태라면 초기화 시켜야 함
        if (playRoom.getStatus().equals("true")) { // false : 게임이 진행 중, true : 게임 시작 전
            gameStartSet.setRound(0);
            gameStartSet.setSpotNum(0);
            gameStartSetRepository.save(gameStartSet);

        }

        // 유저들 정보 조회
        List<GameRoomAttendee> memberListInGame = gameRoomAttendeeRepository.findByGameRoom(playRoom);

        // 라운드 진행 중
        if (gameStartSet.getSpotNum() < memberListInGame.size()) {

            // 현재 스포트라이트 받는 멤버
            Member spotMember = memberRepository.findById(memberListInGame.get(gameStartSet.getSpotNum()).getMember().getId()).orElseThrow(
                    () -> new CustomException(SPOTLIGHT_ERR)
            );

            // 메세지 알림
            GameMessage gameMessage = new GameMessage();
            gameMessage.setRoomId(Long.toString(gameRoomId));                   // 현재 게임룸 id
            gameMessage.setSenderId(String.valueOf(spotMember.getId()));        // 스포트라이트 멤버 id
            gameMessage.setSender(spotMember.getNickname());                    // 스포트라이트 멤버 닉네임
            gameMessage.setContent(gameMessage.getSender() + "님의 차례입니닭!");  // 메세지
            gameMessage.setType(GameMessage.MessageType.SPOTLIGHT);

            messagingTemplate.convertAndSend("/sub/gameRoom/" + gameRoomId, gameMessage);

            // 다음 차례로!
            gameStartSet.setSpotNum(gameStartSet.getSpotNum() +1);
            gameStartSetRepository.save(gameStartSet);

        } else if (gameStartSet.getSpotNum() >= memberListInGame.size()) {


            if (gameStartSet.getRound() < 3) {
                // 한 라운드 종료, 라운드 +1 , 위치 정보 초기화
                gameStartSet.setRound(gameStartSet.getRound() +1);
                gameStartSet.setSpotNum(0);
                gameStartSetRepository.save(gameStartSet);
                spotlight(gameRoomId);

                // 0번부터 시작이다
            } else if (gameStartSet.getRound() == 3) {
                // 메세지 알림 = 여기 말할 이야기
                GameMessage gameMessage = new GameMessage();
                gameMessage.setRoomId(Long.toString(gameRoomId));               // 현재 게임룸 id
                gameMessage.setSenderId("");
                gameMessage.setSender("양계장 주인");
                gameMessage.setContent("너흰 전부 바보닭!!!");
                gameMessage.setType(GameMessage.MessageType.SKIP);

                messagingTemplate.convertAndSend("/sub/gameRoom/" + gameRoomId, gameMessage);

                gameRearService.forcedEndGame(gameRoomId);
            }
        }
        return gameStartSet;
    }
}
