package com.example.namoldak.service;

import com.example.namoldak.domain.*;
import com.example.namoldak.dto.RequestDto.GameDto;
import com.example.namoldak.dto.ResponseDto.VictoryDto;
import com.example.namoldak.util.GlobalResponse.CustomException;
import com.example.namoldak.util.GlobalResponse.code.StatusCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

import static com.example.namoldak.util.GlobalResponse.code.StatusCode.*;

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
    public void gameStart(Long roomId, GameDto gameDto) {

        // 현재 입장한 게임방의 정보를 가져옴
        GameRoom gameRoom = repositoryService.findGameRoomByRoomId(roomId);

        // 게임 시작은 방장만이 할 수 있음
        if (!gameDto.getNickname().equals(gameRoom.getOwner())) {
            throw new CustomException(StatusCode.UNAUTHORIZE);
        }

        // 게임방에 입장한 멤버들 DB(GameRoomMember)에서 가져오기
        List<GameRoomAttendee> gameRoomAttendees = repositoryService.findAttendeeByGameRoom(gameRoom);
        // 게임방의 상태를 start 상태로 업데이트
        gameRoom.setStatus(false);

        // 랜덤으로 뽑은 키워드의 카테고리
        String category = Category.getRandom().name();
        // 같은 카테고리를 가진 키워드 리스트 만들기
        List<Keyword> keywordList = getRandomKeyword(gameRoomAttendees.size(), category);
        // 웹소켓으로 방에 참가한 인원 리스트 전달을 위한 리스트 (닉네임만 필요하기에 닉네임만 담음)
        List<String> memberNicknameList = getNicknameList(gameRoomAttendees);
        //게임룸 멤버한테 키워드 배당
        Map<String, String> keywordToMember = matchKeywordToMember(keywordList, memberNicknameList);

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

        GameStartSet searchOneGameStartSet = repositoryService.findGameStartSetByRoomId(roomId);
        log.info("카테고리 : " + searchOneGameStartSet.getCategory());
        for (String memberNick : memberNicknameList) {
            log.info("키워드 : " + keywordToMember.get(memberNick));
        }

        // 웹소켓으로 전달드릴 content 내용
        Map<String, Object> startSet = new HashMap<>();
        startSet.put("category", gameStartSet.getCategory()); // 카테고리
        startSet.put("keyword", repositoryService.getMapFromStr(gameStartSet.getKeywordToMember())); // 키워드
        startSet.put("memberList", memberNicknameList); // 방에 존재하는 모든 유저들
        startSet.put("startAlert", "총 8라운드닭! 초록색으로 하이라이트된 사람만 말할 수 있고 다른 사람들은 마이크 기능이 제한되니까 채팅으로 알려주면 된닭!");

        sendGameMessage(roomId, GameMessage.MessageType.START, startSet, null, null);
    }

    // 건너뛰기
    @Transactional
    public void gameSkip(GameDto gameDto, Long roomId) {

        String msg = gameDto.getNickname() + "님이 건너뛰기를 선택하셨습니다.";
        sendGameMessage(roomId, GameMessage.MessageType.SKIP, msg, null, gameDto.getNickname());
    }


    @Transactional
    public void spotlight(Long roomId) {

        GameRoom playRoom = repositoryService.findGameRoomByRoomId(roomId);

        // 해당 게임룸의 게임셋을 조회
        GameStartSet gameStartSet = repositoryService.findGameStartSetByRoomId(roomId);

        // 게임이 진행이 불가한 상태라면 초기화 시켜야 함
        if (playRoom.isStatus()) { // false : 게임이 진행 중, true : 게임 시작 전
            gameStartSet.setRound(0);
            gameStartSet.setSpotNum(0);
            repositoryService.saveGameStartSet(gameStartSet);
        }

        // 유저들 정보 조회
        List<GameRoomAttendee> memberListInGame = repositoryService.findAttendeeByGameRoom(playRoom);

        // 라운드 진행 중
        if (gameStartSet.getSpotNum() < memberListInGame.size()) {

            // 현재 스포트라이트 받는 멤버
            Member spotMember = repositoryService.findMemberById(memberListInGame.get(gameStartSet.getSpotNum()).getMember().getId());

            // 메세지 알림
            String msg = spotMember.getNickname() + "님의 차례입니닭!";
            sendGameMessage(roomId, GameMessage.MessageType.SPOTLIGHT, msg, spotMember.getNickname(), spotMember.getNickname());

            // 다음 차례로!
            gameStartSet.setSpotNum(gameStartSet.getSpotNum() +1);
            repositoryService.saveGameStartSet(gameStartSet);

        } else if (gameStartSet.getSpotNum() == memberListInGame.size()) {

            if (gameStartSet.getRound() < 1) {
                // 한 라운드 종료, 라운드 +1 , 위치 정보 초기화
                gameStartSet.setRound(gameStartSet.getRound() +1);
                gameStartSet.setSpotNum(0);
                repositoryService.saveGameStartSet(gameStartSet);
                spotlight(roomId);

                // 0번부터 시작이다
            } else if (gameStartSet.getRound() == 1) {
                // 메세지 알림
                String msg = "너흰 전부 바보닭!!!";
                sendGameMessage(roomId, GameMessage.MessageType.STUPID, msg, null, null);

                forcedEndGame(roomId, null);
            }
        }
    }

    // 정답
    @Transactional
    public void gameAnswer(Long roomId, GameDto gameDto) {
        // 모달창에 작성한 정답
        String answer = gameDto.getAnswer().replaceAll(" ", "");

        // gameStartSet 불러오기
        GameStartSet gameStartSet = repositoryService.findGameStartSetByRoomId(roomId);

        // 정답을 맞추면 게임 끝
        if (repositoryService.getMapFromStr(gameStartSet.getKeywordToMember()).get(gameDto.getNickname()).equals(answer)){

            // 정답자
            gameStartSet.setWinner(gameDto.getNickname());
            repositoryService.saveGameStartSet(gameStartSet);

            // 메세지 알림
            String msg = gameDto.getNickname() + "님이 작성하신" + answer + "은(는) 정답입니닭!";
            sendGameMessage(roomId, GameMessage.MessageType.SUCCESS, msg, gameDto.getNickname(), null);
        } else {
            // 메세지 알림
            String msg = gameDto.getNickname() + "님이 작성하신" + answer + "은(는) 정답이 아닙니닭!";
            sendGameMessage(roomId, GameMessage.MessageType.FAIL, msg, gameDto.getNickname(), null);
        }
    }

    // 게임 강제 종료
    @Transactional
    public void forcedEndGame(Long roomId, String nickname){

        // 현재 게임방 정보 불러오기
        GameRoom enterGameRoom = repositoryService.findGameRoomByRoomId(roomId);

        // 메세지 알림
        String msg = nickname == null ? "게임이 종료되었닭!!" : nickname + " 님이 방에서 탈주해서 강제 종료되었닭!!";
        sendGameMessage(roomId, GameMessage.MessageType.FORCEDENDGAME, msg, null, null);

        // DB에서 게임 셋팅 삭제
        repositoryService.deleteGameStartSetByRoomId(roomId);

        // 현재 방 상태 정보를 true로 변경
        enterGameRoom.setStatus(true);
    }

    // 게임 정상 종료
    @Transactional
    public void endGame(Long roomId){
        // 승리자와 패배자를 list로 반환할 DTO 생성
        VictoryDto victoryDto = new VictoryDto();

        // 방 게임셋 정보 불러오기
        GameStartSet gameStartSet = repositoryService.findGameStartSetByRoomId(roomId);

        // 현재 게임룸 데이터 불러오기
        GameRoom enterGameRoom = repositoryService.findGameRoomByRoomId(roomId);

        // 불러온 게임룸으로 들어간 GameRoomMember들 구하기
        List<GameRoomAttendee> gameRoomAttendeeList = repositoryService.findAttendeeByGameRoom(enterGameRoom);

        // 닉네임을 구하기 위해서 멤버 객체를 담을 리스트 선언
        List<Member> memberList = new ArrayList<>();

        // for문으로 하나씩 빼서 DB 조회 후 List에 넣어주기
        for (GameRoomAttendee gameRoomAttendee : gameRoomAttendeeList){
            Member member = repositoryService.findMemberById(gameRoomAttendee.getMember().getId());
            // 멤버 총 게임 횟수 증가
            member.updateTotalGame(1L);
            repositoryService.saveMember(member);
            memberList.add(member);
            rewardService.createTotalGameReward(member);
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

        // 메세지 알림
        sendGameMessage(roomId, GameMessage.MessageType.ENDGAME, gameStartSet.getKeywordToMember(), null, null);

        // DB에서 게임 셋팅 삭제
        repositoryService.deleteGameStartSetByRoomId(roomId);

        // 현재 방 상태 정보를 true로 변경
        enterGameRoom.setStatus(true);
    }

    public <T> void sendGameMessage(Long roomId, GameMessage.MessageType type, T Content, String nickname, String sender) {

        String senderName = sender == null ? "양계장 주인" : sender;

        GameMessage<T> gameMessage = new GameMessage<>();
        gameMessage.setRoomId(Long.toString(roomId));
        gameMessage.setType(type);
        gameMessage.setSender(senderName);
        gameMessage.setContent(Content);
        gameMessage.setNickname(nickname);

        messagingTemplate.convertAndSend("/sub/gameRoom/" + roomId, gameMessage);
    }

    public List<Keyword> getRandomKeyword(int size, String category) {
        // 같은 카테고리를 가진 키워드 리스트 만들기
        List<Keyword> keywordList;

        if (size == 4) {
            // 참여 멤버가 4명 이라면, 랜덤으로 키워드 4장이 담긴 리스트를 만들어 준다.
            keywordList = repositoryService.findTop4KeywordByCategory(category);
        } else if (size == 3) {
            // 참여 멤버가 3명 이라면, 랜덤으로 키워드 3장이 담긴 리스트를 만들어 준다.
            keywordList = repositoryService.findTop3KeywordByCategory(category);
        } else {
            throw new CustomException(NOT_ENOUGH_MEMBER);
        }

        return keywordList;
    }

    public Map<String, String> matchKeywordToMember(List<Keyword> keywordList, List<String> memberNicknameList) {
        Map<String, String> keywordToMember = new HashMap<>();

        //게임룸 멤버한테 키워드 배당
        for (int i = 0; i < keywordList.size(); i++) {
            keywordToMember.put(memberNicknameList.get(i), keywordList.get(i).getWord());
        }

        return keywordToMember;
    }

    public List<String> getNicknameList(List<GameRoomAttendee> gameRoomAttendees) {
        // 웹소켓으로 방에 참가한 인원 리스트 전달을 위한 리스트 (닉네임만 필요하기에 닉네임만 담음)
        List<String> memberNicknameList = new ArrayList<>();

        for (GameRoomAttendee gameRoomAttendee : gameRoomAttendees) {
            memberNicknameList.add(gameRoomAttendee.getMemberNickname());
        }

        return memberNicknameList;
    }
}
