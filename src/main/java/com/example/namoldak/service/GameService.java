package com.example.namoldak.service;

import com.example.namoldak.domain.*;
import com.example.namoldak.dto.ResponseDto.PrivateResponseBody;
import com.example.namoldak.dto.ResponseDto.ResponseDto;
import com.example.namoldak.repository.GameRoomMemberRepository;
import com.example.namoldak.repository.GameRoomRepository;
import com.example.namoldak.repository.KeywordRepository;
import com.example.namoldak.repository.MemberRepository;
import com.example.namoldak.util.GlobalResponse.CustomException;
import com.example.namoldak.util.GlobalResponse.code.StatusCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class GameService {

    private final GameRoomRepository gameRoomRepository;
    private final GameRoomMemberRepository gameRoomMemberRepository;
    private final KeywordRepository keywordRepository;
    private final MemberRepository memberRepository;
    private final SimpMessageSendingOperations messagingTemplate;

    // 게임 시작
    @Transactional
    public ResponseEntity<?> gameStart(Long gameRoomId, Member member) {

        // 현재 입장한 게임방의 정보를 가져옴
        GameRoom gameRoom = gameRoomRepository.findByGameRoomId(gameRoomId).orElseThrow(
                () -> new CustomException(StatusCode.NOT_FOUND_ROOM)
        );

        // 게임 시작은 방장만이 할 수 있음
        if (!member.getNickname().equals(gameRoom.getOwner())) {
            return new ResponseEntity<>(new PrivateResponseBody(StatusCode.UNAUTHORIZE, null), HttpStatus.BAD_REQUEST);
        }

        // 게임방에 입장한 멤버들 DB(GameRoomMember)에서 가져오기
        List<GameRoomMember> gameRoomMembers = gameRoomMemberRepository.findByGameRoom(gameRoom);

        // 게임방의 상태를 start 상태로 업데이트
        gameRoom.setStatus("start");

        //TODO 멤버들에게 뿌려지게 될 키워드 전체 목록 불러오기
        List<Keyword> keywordList = keywordRepository.findByCategory(keyword.getCategory());

        // 랜덤으로 키워드 하나 뽑기
//        Keyword keyword1 = keywordList.get((int) Math.random() * keywordList.size());

        // 뽑힌 키워드와 카테고리가 일치하는 다른 키워드들 조회 -> 어레이리스트에 담기
//        List<Keyword> selectedKeywords = new ArrayList<>();
//
//        for (Keyword sameKeyword : keywordList) {
//            if (sameKeyword.getCategory().equals(keyword1.getCategory())) {
//                selectedKeywords.add(sameKeyword);
//            }
//        }

        HashMap<String, String> keywordToMember = new HashMap<>();

        List<Optional<Member>> memberList = new ArrayList<>();
        // 웹소켓으로 방에 참가한 인원 리스트 전달을 위한 리스트
        // 닉네임만 필요하기에 닉네임만 담음
        List<String> memberNicknameList = new ArrayList<>();
        for (GameRoomMember gameRoomMember : gameRoomMembers) {
            Optional<Member> member1 = memberRepository.findById(gameRoomMember.getMember().getId());
            memberList.add(member1);
            memberNicknameList.add(member1.get().getNickname());
        }

        // 게임룸 멤버한테 키워드 배당
        for (int i = 0; i < gameRoomMembers.size(); i++) {
            keywordToMember.put(memberList.get(i).get().getNickname(), keywordList.get(i).getWord());
        }

        JSONObject keywordMember = new JSONObject(keywordToMember);

        // GameStartSet에 해당 방의 카테고리와 멤버 각각의 키워드가 어떤 것인지 저장
        GameStartSet gameStartSet = GameStartSet.builder()
                .keyword(keywordMember.toString())
                .category(keywordList.get(0).getCategory())
                .roomId(gameRoomId)
                .spotNum(0)
                .round(1)
                .build();


        // 웹소켓으로 전달드릴 content 내용
        HashMap<String, Object> startset = new HashMap<>();
        startset.put("category", gameStartSet.getCategory()); // 카테고리
        startset.put("keyword", gameStartSet.getKeyword()); // 키워드
        startset.put("memberlist", memberNicknameList); // 방에 존재하는 모든 유저들


        GameMessage gameMessage = new GameMessage<>();
        gameMessage.setRoomId(Long.toString(gameRoomId)); // 현재 게임방 id
        gameMessage.setSenderId(""); // 준비된 유저의 id
        gameMessage.setSender("주인님"); // 준비된 유저의 닉네임
        gameMessage.setContent(startset); // 준비됐다는 내용
        gameMessage.setType(GameMessage.MessageType.START); // 메세지 타입

        // 게임 시작 알림을 방에 구독이 된 유저들에게 알려줌
        messagingTemplate.convertAndSend("/sub/gameroom/" + gameRoomId, gameMessage);

        return new ResponseEntity<>(new ResponseDto(200,"게임 시작"),HttpStatus.OK);
    }

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
