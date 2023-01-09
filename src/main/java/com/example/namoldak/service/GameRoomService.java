package com.example.namoldak.service;

import com.example.namoldak.domain.GameMessage;
import com.example.namoldak.domain.GameRoom;
import com.example.namoldak.domain.GameRoomAttendee;
import com.example.namoldak.domain.Member;
import com.example.namoldak.dto.RequestDto.GameRoomRequestDto;
import com.example.namoldak.dto.ResponseDto.GameRoomResponseDto;
import com.example.namoldak.dto.ResponseDto.MemberResponseDto;
import com.example.namoldak.repository.ChatRoomRepository;
import com.example.namoldak.repository.GameRoomAttendeeRepository;
import com.example.namoldak.repository.GameRoomRepository;
import com.example.namoldak.repository.MemberRepository;
import com.example.namoldak.util.GlobalResponse.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import static com.example.namoldak.util.GlobalResponse.code.StatusCode.*;

// 기능 : 게임룸 서비스
@Slf4j
@RequiredArgsConstructor
@Service
public class GameRoomService {
    // 의존성 주입
    private final GameRoomRepository gameRoomRepository;
    private final GameRoomAttendeeRepository gameRoomAttendeeRepository;
    private final MemberRepository memberRepository;
    private final ChatRoomService chatRoomService;
    private final SimpMessageSendingOperations messagingTemplate;
    private final ChatRoomRepository chatRoomRepository;
    private final GameRearService gameRearService;


    // 게임룸 전체 조회
    @Transactional
    public List<GameRoomResponseDto> mainPage(Pageable pageable) {

        // DB에 저장된 모든 Room들을 리스트형으로 저장 + 페이징 처리
        Page<GameRoom> rooms = gameRoomRepository.findAll(pageable);

        // 필요한 키값들을 반환하기 위해서 미리 Dto 리스트 선언
        List<GameRoomResponseDto> gameRoomList = new ArrayList<>();

        for (GameRoom room : rooms){
            // 모든 Room들이 모여있는 rooms에서 하나씩 추출 -> Room 객체 활용해서 GameRoomMember DB에서 찾은 후 리스트에 저장
            List<GameRoomAttendee> gameRoomAttendeeList = gameRoomAttendeeRepository.findByGameRoom(room);
            // 필요한 키값들을 반환하기 위해서 미리 Dto 리스트 선언
            List<MemberResponseDto> memberList = new ArrayList<>();
            for (GameRoomAttendee gameRoomAttendee : gameRoomAttendeeList) {
                // GameRoomMember에 저장된 멤버 아이디로 DB 조회 후 데이터 저장
                Optional<Member> eachMember = memberRepository.findById(gameRoomAttendee.getMember().getId());

                // MemberResponseDto에 빌더 방식으로 각각의 데이터 값 넣어주기
                MemberResponseDto memberResponseDto = MemberResponseDto.builder()
                        .memberId(eachMember.get().getId())
                        .email(eachMember.get().getEmail())
                        .nickname(eachMember.get().getNickname())
                        .build();
                // 완성된 Dto 리스트에 추가
                memberList.add(memberResponseDto);
            }
            // 그냥 Member가 아니라 GameRoomMember를 담아야 하기 때문에 GameRoomResponseDto로 다시 한번 감쌈
            GameRoomResponseDto gameRoomResponseDto = GameRoomResponseDto.builder()
                    .id(room.getGameRoomId())
                    .roomName(room.getGameRoomName())
                    .roomPassword(room.getGameRoomPassword())
                    .member(memberList)
                    .memberCnt(memberList.size())
                    .owner(room.getOwner())
                    .status(room.getStatus())
                    .build();

            // memberList에 데이터가 있다면 gameRoomList에 gameRoomResponseDto 추가
            // for문 끝날 때 까지 반복
            if (!memberList.isEmpty()) {
                gameRoomList.add(gameRoomResponseDto);
            }
        }
        return gameRoomList;
    }

    // 게임룸 생성
    @Transactional
    public HashMap<String, String> makeGameRoom(Member member, GameRoomRequestDto gameRoomRequestDto) {

        // 빌더 활용해서 GameRoom 엔티티 데이터 채워주기
        GameRoom gameRoom = GameRoom.builder()
                .gameRoomName(gameRoomRequestDto.getGameRoomName())
                .gameRoomPassword(gameRoomRequestDto.getGameRoomPassword())
                .owner(member.getNickname())
                .status("true")
                .build();

        // DB에 데이터 저장
        gameRoomRepository.save(gameRoom);

        // 생성자로 gameRoom, member 데이터를 담은 GameRoomMember 객체 완성
        GameRoomAttendee gameRoomAttendee = new GameRoomAttendee(gameRoom, member);

        // GameRoomMember DB에 해당 데이터 저장
        gameRoomAttendeeRepository.save(gameRoomAttendee);

        // 채팅방 생성
        chatRoomService.createChatRoom(gameRoom.getGameRoomId().toString(), gameRoom.getGameRoomName());

        // data에 데이터를 담아주기 위해 HashMap 생성
        HashMap<String, String> roomInfo = new HashMap<>();

        // 앞에 키 값에 뒤에 밸류 값을 넣어줌
        roomInfo.put("gameRoomName", gameRoom.getGameRoomName());
        roomInfo.put("roomId", Long.toString(gameRoom.getGameRoomId()));
        roomInfo.put("gameRoomPassword", gameRoom.getGameRoomPassword());
        roomInfo.put("owner", gameRoom.getOwner());
        roomInfo.put("status", gameRoom.getStatus());

//        return new ResponseEntity<>(new PrivateResponseBody<>(StatusCode.OK, roomInfo), HttpStatus.OK);
        return roomInfo;
    }

    // 게임룸 입장
    @Transactional
    public HashMap<String, String>  enterGame(Long roomId, Member member) {

        // roomId로 DB에서 데이터 찾아와서 담음
        Optional<GameRoom> enterGameRoom = gameRoomRepository.findById(roomId);

        // 방의 상태가 false면 게임이 시작 중이거나 가득 찬 상태이기 때문에 출입이 불가능
        if (enterGameRoom.get().getStatus().equals("false")){
            // 뒤로 넘어가면 안 되니까 return으로 호다닥 끝내버림
//            return new PrivateResponseBody(StatusCode.ALREADY_PLAYING, "게임이 시작해서 못 들어간닭!!");
            throw new CustomException(ALREADY_PLAYING);
        }

        // 입장하려는 게임방을 이용해서 GameRoomMember DB에서 유저 정보 전부 빼와서 리스트형에 저장 (입장 정원 확인 용도)
        List<GameRoomAttendee> gameRoomAttendeeList = gameRoomAttendeeRepository.findByGameRoom(enterGameRoom);

        // 만약 방에 4명이 넘어가면
        if (gameRoomAttendeeList.size() > 3){
            // 입장 안 된다고 입구컷
//            return new PrivateResponseBody(StatusCode.CANT_ENTER, "정원이 다 차있닭!!");
            throw new CustomException(CANT_ENTER);
        }

        // for문으로 리스트에서 gameRoomMember 하나씩 빼주기
        for (GameRoomAttendee gameRoomAttendee : gameRoomAttendeeList){
            // gameRoomMember에서 얻은 유저 아이디로 Member 객체 저장
            Optional<Member> member1 = memberRepository.findById(gameRoomAttendee.getMember().getId());
            // 현재 들어가려는 유저의 ID와 게임에 들어가있는 멤버의 ID가 똑같으면 입구컷 해버림
            if (member.getId().equals(member1.get().getId())){
//                return new PrivateResponseBody(StatusCode.MEMBER_DUPLICATED, "이미 입장해있닭!!");
                throw new CustomException(MEMBER_DUPLICATED);
            }
        }

        GameRoomAttendee gameRoomAttendee = new GameRoomAttendee(enterGameRoom, member);

        // DB에 데이터 저장
        gameRoomAttendeeRepository.save(gameRoomAttendee);

        HashMap<String, Object> contentSet = new HashMap<>();

        GameMessage gameMessage = new GameMessage<>();
        gameMessage.setRoomId(String.valueOf(roomId));
        gameMessage.setSenderId(String.valueOf(member.getId()));
        gameMessage.setSender(member.getNickname());

        contentSet.put("owner", enterGameRoom.get().getOwner());
        contentSet.put("memberCnt", gameRoomAttendeeList.size());
        contentSet.put("enterComment", gameMessage.getRoomId() + "번 방에" + gameMessage.getSenderId() + "님이 입장하셨습니닭!");

        gameMessage.setContent(contentSet);
        gameMessage.setType(GameMessage.MessageType.ENTER);

        messagingTemplate.convertAndSend("/sub/gameRoom/" + roomId, gameMessage);

        // 해시맵으로 데이터 정리해서 보여주기
        HashMap<String, String> roomInfo = new HashMap<>();

        roomInfo.put("gameRoomName", enterGameRoom.get().getGameRoomName());
        roomInfo.put("roomId", String.valueOf(enterGameRoom.get().getGameRoomId()));
        roomInfo.put("owner", enterGameRoom.get().getOwner());
        roomInfo.put("status", enterGameRoom.get().getStatus());

//        return new PrivateResponseBody<>(StatusCode.OK, roomInfo);
        return roomInfo;
    }

    // 게임룸 키워드 조회
    public List<GameRoomResponseDto> searchGame(Pageable pageable, String keyword) {
        // 게임룸 이름을 keyword(검색어)로 잡고 조회 + 페이징 처리
        Page<GameRoom> rooms = gameRoomRepository.findByGameRoomNameContaining(pageable, keyword);

        if(rooms.isEmpty()){
            throw new CustomException(NOT_EXIST_ROOMS);
        }

        List<GameRoomResponseDto> gameRoomList = new ArrayList<>();
        for (GameRoom room : rooms) {
            // 게임룸에 입장해 있는 멤버 조회
            List<GameRoomAttendee> gameRoomAttendeeList = gameRoomAttendeeRepository.findByGameRoom(room);
            List<MemberResponseDto> memberList = new ArrayList<>();

            for (GameRoomAttendee gameRoomAttendee : gameRoomAttendeeList){
                Optional<Member> eachMember = memberRepository.findById(gameRoomAttendee.getMember().getId());

                // 멤버로부터 필요한 정보인 id, email, nickname만 Dto에 담아주기
                MemberResponseDto memberResponseDto = MemberResponseDto.builder()
                        .memberId(eachMember.get().getId())
                        .email(eachMember.get().getEmail())
                        .nickname(eachMember.get().getNickname())
                        .build();
                // 담긴 정보 저장
                memberList.add(memberResponseDto);
            }
            // 게임룸에 필요한 정보를 Dto에 담아주기
            GameRoomResponseDto gameRoomResponseDto = GameRoomResponseDto.builder()
                    .id(room.getGameRoomId())
                    .roomName(room.getGameRoomName())
                    .roomPassword(room.getGameRoomPassword())
                    .member(memberList)
                    .memberCnt(memberList.size())
                    .owner(room.getOwner())
                    .status(room.getStatus())
                    .build();
            // 방에 멤버가 1명 이상이라면, 담아줬던 데이터 저장하기
            if (!memberList.isEmpty()) {
                gameRoomList.add(gameRoomResponseDto);
            }
        }
        // 저장된 정보가 담긴 리스트를 반환
        return gameRoomList;
    }

    @Transactional
    public void roomExit(Long RoomId, Member member) {
        // 나가려고 하는 방 정보 DB에서 불러오기
        GameRoom enterGameRoom = gameRoomRepository.findById(RoomId).orElseThrow(
                () -> new CustomException(NOT_EXIST_ROOMS)
        );

        // 나가려고 하는 GameRoomMember를 member 객체로 DB에서 조회
        GameRoomAttendee gameRoomAttendee = gameRoomAttendeeRepository.findByMember(member);

        // 위에서 구한 GameRoomMemeber 객체로 DB 데이터 삭제
        gameRoomAttendeeRepository.delete(gameRoomAttendee);

        // 게임방에 남아있는 유저들 구하기
        List<GameRoomAttendee> existGameRoomAttendee = gameRoomAttendeeRepository.findByGameRoom(enterGameRoom);

        // 남아있는 유저의 수가 0명이라면 게임방 DB에서 데이터 삭제
        if (existGameRoomAttendee.size() == 0) {
            gameRoomRepository.delete(enterGameRoom);
        }

        // 게임 채팅방도 삭제해줌
        chatRoomRepository.deleteRoom(enterGameRoom.getGameRoomId().toString());

        // 게임이 시작 중인 상태에서 3명 아래로 떨어졌을 경우에
        if (enterGameRoom.getStatus().equals("false")){
            if (existGameRoomAttendee.size() < 3) {
                // 게임을 끝내버림
                gameRearService.forcedEndGame(RoomId);
            }
        }

        // 방을 나갈 경우의 알림 문구와 나간 이후의 방 인원 수를 저장하기 위한 해시맵
        HashMap<String, Object> contentSet = new HashMap<>();

        // 누가 나갔는지 알려줄 메세지 정보 세팅
        GameMessage gameMessage = new GameMessage();
        gameMessage.setRoomId(Long.toString(enterGameRoom.getGameRoomId()));
        gameMessage.setSenderId(Long.toString(member.getId()));
        gameMessage.setSender(member.getNickname());

        contentSet.put("memberCnt", existGameRoomAttendee.size());
        contentSet.put("alert", gameMessage.getSender() + " 님이 방을 나가셨습니닭!");

        gameMessage.setContent(contentSet);
        gameMessage.setType(GameMessage.MessageType.LEAVE);

        // 해당 주소에 있는 사람들에게 게임 메세지 모두 발송
        messagingTemplate.convertAndSend("/sub/gameRoom/" + RoomId, gameMessage);

        // 만약에 나간 사람이 그 방의 방장이고 남은 인원이 0명이 아닐 경우에
        if (member.getNickname().equals(enterGameRoom.getOwner()) && !existGameRoomAttendee.isEmpty()){
            // 남은 사람들의 수 만큼 랜덤으로 돌려서 나온 멤버 ID
            Long nextOwnerId = existGameRoomAttendee.get((int) (Math.random() * existGameRoomAttendee.size())).getGameRoomMemberId();
            // nextOwnerId로 GameRoomMember 정보 저장
            GameRoomAttendee nextOwner = gameRoomAttendeeRepository.findById(nextOwnerId).orElseThrow(
                    () -> new CustomException(LOGIN_MEMBER_ID_FAIL)
            );
            // 들어간 방에 Owner 업데이트
            enterGameRoom.setOwner(nextOwner.getMember().getNickname());
            // 변경된 방장 정보를 방에 있는 모든 사람에게 메세지로 알림
            GameMessage alertOwner = new GameMessage();
            alertOwner.setRoomId(Long.toString(enterGameRoom.getGameRoomId()));
            alertOwner.setSenderId(Long.toString(nextOwner.getMember().getId()));
            alertOwner.setSender(nextOwner.getMember().getNickname());
            alertOwner.setType(GameMessage.MessageType.NEWOWNER);

            messagingTemplate.convertAndSend("/sub/gameRoom" + RoomId, alertOwner);
        }
    }
}
