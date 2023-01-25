package com.example.namoldak.service;

import com.example.namoldak.domain.*;
import com.example.namoldak.dto.RequestDto.GameDto;
import com.example.namoldak.dto.ResponseDto.VictoryDto;
import com.example.namoldak.util.GlobalResponse.CustomException;
import com.example.namoldak.util.GlobalResponse.code.StatusCode;
import com.fasterxml.jackson.core.JsonProcessingException;
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
    private final SimpMessageSendingOperations messagingTemplate;
    private final RewardService rewardService;
    private final RepositoryService repositoryService;


    // 게임 시작
    @Transactional
    public void gameStart(Long roomId, GameDto gameDto) throws JsonProcessingException {

        // 현재 입장한 게임방의 정보를 가져옴
        GameRoom gameRoom = repositoryService.findGameRoomByRoomId(roomId).orElseThrow(
                () -> new CustomException(StatusCode.NOT_FOUND_ROOM)
        );

        // 게임 시작은 방장만이 할 수 있음
        if (!gameDto.getNickname().equals(gameRoom.getOwner())) {
            throw new CustomException(StatusCode.UNAUTHORIZE);
        }

        // 게임방에 입장한 멤버들 DB(GameRoomMember)에서 가져오기
        List<GameRoomAttendee> gameRoomAttendees = repositoryService.findAttendeeByGameRoom(gameRoom);
        // 게임방의 상태를 start 상태로 업데이트
        gameRoom.setStatus("false");

        // 랜덤으로 뽑은 키워드의 카테고리
        String category = Category.getRandom().name();

        // 같은 카테고리를 가진 키워드 리스트 만들기
        List<Keyword> keywordList;

        if (gameRoomAttendees.size() == 4) {
            // 참여 멤버가 4명 이라면, 랜덤으로 키워드 4장이 담긴 리스트를 만들어 준다.
            keywordList = repositoryService.findTop4KeywordByCategory(category);
        } else if (gameRoomAttendees.size() == 3) {
            // 참여 멤버가 3명 이라면, 랜덤으로 키워드 3장이 담긴 리스트를 만들어 준다.
            keywordList = repositoryService.findTop3KeywordByCategory(category);
        } else {
            throw new CustomException(NOT_ENOUGH_MEMBER);
        }

        HashMap<String, String> keywordToMember = new HashMap<>();

        List<Optional<Member>> memberList = new ArrayList<>();
        // 웹소켓으로 방에 참가한 인원 리스트 전달을 위한 리스트 (닉네임만 필요하기에 닉네임만 담음)
        List<String> memberNicknameList = new ArrayList<>();

        for (GameRoomAttendee gameRoomAttendee : gameRoomAttendees) {
            memberNicknameList.add(gameRoomAttendee.getMemberNickname());
        }

        //게임룸 멤버한테 키워드 배당
        for (int i = 0; i < gameRoomAttendees.size(); i++) {
            keywordToMember.put(memberNicknameList.get(i), keywordList.get(i).getWord());
        }

        GameStartSet gameStartSet = GameStartSet.builder()
                .roomId(roomId)
                .category(category)
                .keywordToMember(repositoryService.getStrFromMap(keywordToMember))
                .round(0)
                .spotNum(0)
                .winner("")
                .gameStartTime(System.currentTimeMillis())
                .build();

        // StartSet 저장
        repositoryService.saveGameStartSet(gameStartSet);

        GameStartSet searchOneGameStartSet = repositoryService.findGameStartSetByRoomId(roomId).orElseThrow(
                ()-> new CustomException(GAME_SET_NOT_FOUND)
        );
        log.info("카테고리 : " + searchOneGameStartSet.getCategory());
        for (String memberNick : memberNicknameList) {
            log.info("키워드 : " + keywordToMember.get(memberNick));
        }

        // 웹소켓으로 전달드릴 content 내용
        HashMap<String, Object> startSet = new HashMap<>();
        startSet.put("category", gameStartSet.getCategory()); // 카테고리
        startSet.put("keyword", repositoryService.getMapFromStr(gameStartSet.getKeywordToMember())); // 키워드
        startSet.put("memberList", memberNicknameList); // 방에 존재하는 모든 유저들

        GameMessage gameMessage = new GameMessage<>();
        gameMessage.setRoomId(Long.toString(roomId)); // 현재 게임방 id
        gameMessage.setSenderId(""); // 준비된 유저의 id
        gameMessage.setSender("양계장 주인"); // 준비된 유저의 닉네임
        gameMessage.setContent(startSet); // 준비됐다는 내용
        gameMessage.setType(GameMessage.MessageType.START); // 메세지 타입

        // 게임 시작 알림을 방에 구독이 된 유저들에게 알려줌
        messagingTemplate.convertAndSend("/sub/gameRoom/" + roomId, gameMessage);
    }

    // 건너뛰기
    @Transactional
    public void gameSkip(GameDto gameDto, Long roomId) {

        // stomp로 메세지 전달
        GameMessage gameMessage = new GameMessage();
        gameMessage.setRoomId(Long.toString(roomId)); // 현재 방 id
        gameMessage.setSender(gameDto.getNickname()); // 로그인한 유저의 닉네임
        gameMessage.setContent(gameDto.getNickname() + "님이 건너뛰기를 선택하셨습니다.");
        gameMessage.setType(GameMessage.MessageType.SKIP);

        // 방 안의 구독자 모두가 메세지 받음
        messagingTemplate.convertAndSend("/sub/gameRoom/" + roomId, gameMessage);
    }

    public GameStartSet spotlight(Long roomId) {

        GameRoom playRoom = repositoryService.findGameRoomByRoomId(roomId).get();

        // 해당 게임룸의 게임셋을 조회
        GameStartSet gameStartSet = repositoryService.findGameStartSetByRoomId(roomId).orElseThrow(
                ()-> new CustomException(GAME_SET_NOT_FOUND)
        );

        // 게임이 진행이 불가한 상태라면 초기화 시켜야 함
        if (playRoom.getStatus().equals("true")) { // false : 게임이 진행 중, true : 게임 시작 전
            gameStartSet.setRound(0);
            gameStartSet.setSpotNum(0);
            repositoryService.saveGameStartSet(gameStartSet);

        }

        // 유저들 정보 조회
        List<GameRoomAttendee> memberListInGame = repositoryService.findAttendeeByGameRoom(playRoom);

        // 라운드 진행 중
        if (gameStartSet.getSpotNum() < memberListInGame.size()) {

            // 현재 스포트라이트 받는 멤버
            Member spotMember = repositoryService.findMemberById(memberListInGame.get(gameStartSet.getSpotNum()).getMember().getId()).orElseThrow(
                    () -> new CustomException(SPOTLIGHT_ERR)
            );

            // 메세지 알림
            GameMessage gameMessage = new GameMessage();
            gameMessage.setRoomId(Long.toString(roomId));                   // 현재 게임룸 id
            gameMessage.setSenderId(String.valueOf(spotMember.getId()));        // 스포트라이트 멤버 id
            gameMessage.setSender(spotMember.getNickname());                    // 스포트라이트 멤버 닉네임
            gameMessage.setContent(gameMessage.getSender() + "님의 차례입니닭!");  // 메세지
            gameMessage.setType(GameMessage.MessageType.SPOTLIGHT);

            messagingTemplate.convertAndSend("/sub/gameRoom/" + roomId, gameMessage);

            // 다음 차례로!
            gameStartSet.setSpotNum(gameStartSet.getSpotNum() +1);
            repositoryService.saveGameStartSet(gameStartSet);

        } else if (gameStartSet.getSpotNum() >= memberListInGame.size()) {


            if (gameStartSet.getRound() < 3) {
                // 한 라운드 종료, 라운드 +1 , 위치 정보 초기화
                gameStartSet.setRound(gameStartSet.getRound() +1);
                gameStartSet.setSpotNum(0);
                repositoryService.saveGameStartSet(gameStartSet);
                spotlight(roomId);

                // 0번부터 시작이다
            } else if (gameStartSet.getRound() == 3) {
                // 메세지 알림 = 여기 말할 이야기
                GameMessage gameMessage = new GameMessage();
                gameMessage.setRoomId(Long.toString(roomId));               // 현재 게임룸 id
                gameMessage.setSenderId("");
                gameMessage.setSender("양계장 주인");
                gameMessage.setContent("너흰 전부 바보닭!!!");
                gameMessage.setType(GameMessage.MessageType.SKIP);

                messagingTemplate.convertAndSend("/sub/gameRoom/" + roomId, gameMessage);

                forcedEndGame(roomId);
            }
        }
        return gameStartSet;
    }

    // 정답
    @Transactional
    public void gameAnswer(Long roomId, GameDto gameDto) throws JsonProcessingException {
        // 모달창에 작성한 정답
        String answer = gameDto.getAnswer().replaceAll(" ", "");

        // gameStartSet 불러오기
        GameStartSet gameStartSet = repositoryService.findGameStartSetByRoomId(roomId).orElseThrow(
                ()-> new CustomException(StatusCode.GAME_SET_NOT_FOUND)
        );

        GameMessage gameMessage = new GameMessage();

        // 정답을 맞추면 게임 끝
        if (repositoryService.getMapFromStr(gameStartSet.getKeywordToMember()).get(gameDto.getNickname()).equals(answer)){

            // 정답자
            gameStartSet.setWinner(gameDto.getNickname());
            repositoryService.saveGameStartSet(gameStartSet);

            // stomp로 메세지 전달
            gameMessage.setRoomId(Long.toString(roomId));
            gameMessage.setSender("양계장 주인");
            gameMessage.setContent(gameDto.getNickname() + "님이 작성하신" + answer + "은(는) 정답입니닭!");
            gameMessage.setNickname(gameDto.getNickname());
            gameMessage.setType(GameMessage.MessageType.SUCCESS);

            // 방 안의 구독자 모두가 메세지 받음
            messagingTemplate.convertAndSend("/sub/gameRoom/" + roomId, gameMessage);
        } else {
            // stomp로 메세지 전달
            gameMessage.setRoomId(Long.toString(roomId));
            gameMessage.setSender("양계장 주인");
            gameMessage.setContent(gameDto.getNickname() + "님이 작성하신" + answer + "은(는) 정답이 아닙니닭!");
            gameMessage.setNickname(gameDto.getNickname());
            gameMessage.setType(GameMessage.MessageType.FAIL);

            messagingTemplate.convertAndSend("/sub/gameRoom/" + roomId, gameMessage);
        }
    }

    // 게임 강제 종료
    @Transactional
    public void forcedEndGame(Long roomId){

        // 현재 게임방 정보 불러오기
        Optional<GameRoom> enterGameRoom = repositoryService.findGameRoomByRoomId(roomId);

        // 발송할 메세지 데이터 저장
        GameMessage gameMessage = new GameMessage<>();
        gameMessage.setRoomId(Long.toString(roomId));
        gameMessage.setSenderId("");
        gameMessage.setSender("양계장 주인");
        gameMessage.setContent("게임 진행 가능한 최소 인원이 충족되지 못 하여 게임이 종료된닭.");
        gameMessage.setType(GameMessage.MessageType.ENDGAME);
        messagingTemplate.convertAndSend("/sub/gameRoom/" + roomId, gameMessage);

        // Redis DB에서 게임 셋팅 삭제
        repositoryService.deleteGameStartSetByRoomId(roomId);

        // 현재 방 상태 정보를 true로 변경
        enterGameRoom.get().setStatus("true");
    }

    // 게임 정상 종료
    @Transactional
    public void endGame(Long roomId){
        // 승리자와 패배자를 list로 반환할 DTO 생성
        VictoryDto victoryDto = new VictoryDto();

        // 방 게임셋 정보 불러오기
        GameStartSet gameStartSet = repositoryService.findGameStartSetByRoomId(roomId).orElseThrow(
                ()-> new CustomException(StatusCode.GAME_SET_NOT_FOUND)
        );

        // 현재 게임룸 데이터 불러오기
        Optional<GameRoom> enterGameRoom = repositoryService.findGameRoomByRoomId(roomId);

        // 불러온 게임룸으로 들어간 GameRoomMember들 구하기
        List<GameRoomAttendee> gameRoomAttendeeList = repositoryService.findAttendeeByGameRoomOptional(enterGameRoom);

        // 닉네임을 구하기 위해서 멤버 객체를 담을 리스트 선언
        List<Member> memberList = new ArrayList<>();

        // for문으로 하나씩 빼서 DB 조회 후 List에 넣어주기
        for (GameRoomAttendee gameRoomAttendee : gameRoomAttendeeList){
            Optional<Member> member = repositoryService.findMemberById(gameRoomAttendee.getMember().getId());
            // 멤버 총 게임 횟수 증가
            member.get().updateTotalGame(1L);
            repositoryService.saveMember(member.get());
            memberList.add(member.get());
            rewardService.createTotalGameReward(member.get());
        }
        Long startTime = gameStartSet.getGameStartTime();
        Long currentTime = System.currentTimeMillis();
        Long playTime = (currentTime - startTime) / 1000;
        // member의 닉네임이 정답자와 같지 않을 경우 전부 Loser에 저장하고 같을 경우 Winner에 저장
        for (Member member : memberList){
            if (!member.getNickname().equals(gameStartSet.getWinner())){
                victoryDto.setLoser(member.getNickname());
                // 멤버 패배 기록 추가
                member.updateLoseNum(1L);
                member.updatePlayTime(playTime);
                repositoryService.saveMember(member);
                rewardService.createLoseReward(member);
            } else {
                victoryDto.setWinner(member.getNickname());
                // 멤버 승리 기록 추가
                member.updateWinNum(1L);
                member.updatePlayTime(playTime);
                repositoryService.saveMember(member);
                rewardService.createWinReward(member);
            }
        }

        // 발송할 메세지 데이터 저장
        GameMessage gameMessage = new GameMessage();
        gameMessage.setRoomId(Long.toString(roomId));
        gameMessage.setSender("양계장 주인");
        gameMessage.setContent(victoryDto);
        gameMessage.setType(GameMessage.MessageType.ENDGAME);
        messagingTemplate.convertAndSend("/sub/gameRoom/" + roomId, gameMessage);

        // DB에서 게임 셋팅 삭제
        repositoryService.deleteGameStartSetByRoomId(roomId);

        // 현재 방 상태 정보를 true로 변경
        enterGameRoom.get().setStatus("true");
    }

}
