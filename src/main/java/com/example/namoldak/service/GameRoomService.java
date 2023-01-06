package com.example.namoldak.service;

import com.example.namoldak.domain.GameMessage;
import com.example.namoldak.domain.GameRoom;
import com.example.namoldak.domain.GameRoomMember;
import com.example.namoldak.domain.Member;
import com.example.namoldak.dto.RequestDto.GameRoomRequestDto;
import com.example.namoldak.dto.ResponseDto.GameRoomResponseDto;
import com.example.namoldak.dto.ResponseDto.MemberResponseDto;
import com.example.namoldak.dto.ResponseDto.PrivateResponseBody;
import com.example.namoldak.repository.ChatRoomRepository;
import com.example.namoldak.repository.GameRoomMemberRepository;
import com.example.namoldak.repository.GameRoomRepository;
import com.example.namoldak.repository.MemberRepository;
import com.example.namoldak.util.GlobalResponse.CustomException;
import com.example.namoldak.util.GlobalResponse.code.StatusCode;
import com.example.namoldak.util.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import static com.example.namoldak.util.GlobalResponse.code.StatusCode.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class GameRoomService {
    // 의존성 주입
    private final JwtUtil jwtUtil;
    private final GameRoomRepository gameRoomRepository;
    private final GameRoomMemberRepository gameRoomMemberRepository;
    private final MemberRepository memberRepository;
    private final ChatRoomService chatRoomService;
    private final SimpMessageSendingOperations messagingTemplate;
    private final ChatRoomRepository chatRoomRepository;

    // 게임룸 전체 조회
    @Transactional
    public List<GameRoomResponseDto> mainPage(Pageable pageable) {

        // DB에 저장된 모든 Room들을 리스트형으로 저장
        Page<GameRoom> rooms = gameRoomRepository.findAll(pageable);

        // 필요한 키값들을 반환하기 위해서 미리 Dto 리스트 선언
        List<GameRoomResponseDto> gameRoomList = new ArrayList<>();

        for (GameRoom room : rooms) {
            // 모든 Room들이 모여있는 rooms에서 하나씩 추출 -> Room 객체 활용해서 GameRoomMember DB에서 찾은 후 리스트에 저장
            List<GameRoomMember> gameRoomMemberList = gameRoomMemberRepository.findByGameRoom(room);
            // 필요한 키값들을 반환하기 위해서 미리 Dto 리스트 선언
            List<MemberResponseDto> memberList = new ArrayList<>();
            for (GameRoomMember gameRoomMember : gameRoomMemberList) {
                // GameRoomMember에 저장된 멤버 아이디로 DB 조회 후 데이터 저장
                Optional<Member> eachMember = memberRepository.findById(gameRoomMember.getMember().getId());

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
//            }
        }
        return gameRoomList;
    }

    // 게임룸 생성
    @Transactional
    public ResponseEntity<?> makeGameRoom(Member member, GameRoomRequestDto gameRoomRequestDto) {

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
        GameRoomMember gameRoomMember = new GameRoomMember(gameRoom, member);
        // GameRoomMember DB에 해당 데이터 저장
        gameRoomMemberRepository.save(gameRoomMember);

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

        return new ResponseEntity<>(new PrivateResponseBody<>(StatusCode.OK, roomInfo), HttpStatus.OK);
    }

    // 게임룸 입장
    @Transactional
    public ResponseEntity<?> enterGame(Long roomId, Member member) {
//        GameRoomResponseDto gameRoomResponseDto;
        // roomId로 DB에서 데이터 찾아와서 담음
        Optional<GameRoom> enterGameRoom = gameRoomRepository.findById(roomId);

        // 방의 상태가 false면 게임이 시작 중이거나 가득 찬 상태이기 때문에 출입이 불가능
        if (enterGameRoom.get().getStatus().equals("false")) {
            // 뒤로 넘어가면 안 되니까 return으로 호다닥 끝내버림
            return new ResponseEntity<>(new PrivateResponseBody(StatusCode.ALREADY_PLAYING, null), HttpStatus.BAD_REQUEST);
        }

        // 입장하려는 게임방을 이용해서 GameRoomMember DB에서 유저 정보 전부 빼와서 리스트형에 저장 (입장 정원 확인 용도)
        List<GameRoomMember> gameRoomMemberList = gameRoomMemberRepository.findByGameRoom(enterGameRoom);

        // 만약 방에 4명이 넘어가면
        if (gameRoomMemberList.size() > 4) {
            // 입장 안 된다고 입구컷
            return new ResponseEntity<>(new PrivateResponseBody(StatusCode.CANT_ENTER, null), HttpStatus.BAD_REQUEST);
        }

//        // 빌더 패턴으로 Entity에 데이터 넣기
//        GameRoomMember gameRoomMember = GameRoomMember.builder()
//                .gameRoom(enterGameRoom.get())
//                .member(member)
//                .gameRoomMemberId()
//                .build();

        GameRoomMember gameRoomMember = new GameRoomMember(enterGameRoom, member);

        // DB에 데이터 저장
        gameRoomMemberRepository.save(gameRoomMember);

        HashMap<String, Object> contentSet = new HashMap<>();

        GameMessage gameMessage = new GameMessage<>();
        gameMessage.setRoomId(String.valueOf(roomId));
        gameMessage.setSenderId(String.valueOf(member.getId()));
        gameMessage.setSender(member.getNickname());

        contentSet.put("owner", enterGameRoom.get().getOwner());
        contentSet.put("memberCnt", gameRoomMemberList.size());
        contentSet.put("enterComment", gameMessage.getRoomId() + "번 방에" + gameMessage.getSenderId() + "님이 입장하셨습니다.");

        gameMessage.setContent(contentSet);
        gameMessage.setType(GameMessage.MessageType.ENTER);

        messagingTemplate.convertAndSend("/sub/gameroom/" + roomId, gameMessage);

        // 해시맵으로 데이터 정리해서 보여주기
        HashMap<String, String> roomInfo = new HashMap<>();

        roomInfo.put("gameRoomName", enterGameRoom.get().getGameRoomName());
        roomInfo.put("roomId", String.valueOf(enterGameRoom.get().getGameRoomId()));
        roomInfo.put("owner", enterGameRoom.get().getOwner());
        roomInfo.put("status", enterGameRoom.get().getStatus());

        return new ResponseEntity<>(new PrivateResponseBody<>(StatusCode.OK, roomInfo), HttpStatus.OK);
    }

    // 게임룸 키워드 조회
    public List<GameRoomResponseDto> searchGame(Pageable pageable, String keyword) {

        Page<GameRoom> rooms = gameRoomRepository.findByGameRoomNameContaining(pageable, keyword);

        List<GameRoomResponseDto> gameRoomList = new ArrayList<>();

        for (GameRoom room : rooms) {
            List<GameRoomMember> gameRoomMemberList = gameRoomMemberRepository.findByGameRoom(room);
            List<MemberResponseDto> memberList = new ArrayList<>();

            for (GameRoomMember gameRoomMember : gameRoomMemberList) {

                Optional<Member> eachMember = memberRepository.findById(gameRoomMember.getMember().getId());

                MemberResponseDto memberResponseDto = MemberResponseDto.builder()
                        .memberId(eachMember.get().getId())
                        .email(eachMember.get().getEmail())
                        .nickname(eachMember.get().getNickname())
                        .build();

                memberList.add(memberResponseDto);
            }

            GameRoomResponseDto gameRoomResponseDto = GameRoomResponseDto.builder()
                    .id(room.getGameRoomId())
                    .roomName(room.getGameRoomName())
                    .roomPassword(room.getGameRoomPassword())
                    .member(memberList)
                    .memberCnt(memberList.size())
                    .owner(room.getOwner())
                    .status(room.getStatus())
                    .build();

            if (!memberList.isEmpty()) {
                gameRoomList.add(gameRoomResponseDto);
            }
        }
        return gameRoomList;
    }

    @Transactional
    public ResponseEntity<?> roomExit(Long RoomId, Member member) {
        // 나가려고 하는 방 정보 DB에서 불러오기
        GameRoom enterGameRoom = gameRoomRepository.findById(RoomId).orElseThrow(
                () -> new CustomException(NOT_EXIST_ROOMS)
        );
        // 나가려고 하는 GameRoomMember를 member 객체로 DB에서 조회
        GameRoomMember gameRoomMember = gameRoomMemberRepository.findByMember(member);
        // 위에서 구한 GameRoomMemeber 객체로 DB 데이터 삭제
        gameRoomMemberRepository.delete(gameRoomMember);
        // 게임방에 남아있는 유저들 구하기
        List<GameRoomMember> existGameRoomMember = gameRoomMemberRepository.findByGameRoom(enterGameRoom);
        // 남아있는 유저의 수가 0명이라면 게임방 DB에서 데이터 삭제
        if (existGameRoomMember.size() == 0) {
            gameRoomRepository.delete(enterGameRoom);
        }
        // 게임 채팅방도 삭제해줌
        chatRoomRepository.deleteRoom(enterGameRoom.getGameRoomId().toString());

        // 방을 나갈 경우의 알림 문구와 나간 이후의 방 인원 수를 저장하기 위한 해시맵
        HashMap<String, Object> contentSet = new HashMap<>();

        // 누가 나갔는지 알려줄 메세지 정보 세팅
        GameMessage gameMessage = new GameMessage();
        gameMessage.setRoomId(Long.toString(enterGameRoom.getGameRoomId()));
        gameMessage.setSenderId(Long.toString(member.getId()));
        gameMessage.setSender(member.getNickname());

        contentSet.put("memberCnt", existGameRoomMember.size());
        contentSet.put("alert", gameMessage.getSender() + " 님이 방을 나가셨습니다");

        gameMessage.setContent(contentSet);
        gameMessage.setType(GameMessage.MessageType.LEAVE);

        // 해당 주소에 있는 사람들에게 게임 메세지 모두 발송
        messagingTemplate.convertAndSend("/sub/gameroom/" + RoomId, gameMessage);

        // 만약에 나간 사람이 그 방의 방장이고 남은 인원이 0명이 아닐 경우에
        if (member.getNickname().equals(enterGameRoom.getOwner()) && !existGameRoomMember.isEmpty()){
            // 남은 사람들의 수 만큼 랜덤으로 돌려서 나온 멤버 ID
            Long nextOwnerId = existGameRoomMember.get((int) (Math.random() * existGameRoomMember.size())).getGameRoomMemberId();
            // nextOwnerId로 GameRoomMember 정보 저장
            GameRoomMember nextOwner = gameRoomMemberRepository.findById(nextOwnerId).orElseThrow(
                    () -> new CustomException(LOGIN_MEMBER_ID_FAIL)
            );
            // 들어간 방에 Owner 업데이트
            enterGameRoom.update(nextOwner.getMember().getNickname());
            // 변경된 방장 정보를 방에 있는 모든 사람에게 메세지로 알림
            GameMessage alertOwner = new GameMessage();
            alertOwner.setRoomId(Long.toString(enterGameRoom.getGameRoomId()));
            alertOwner.setSenderId(Long.toString(nextOwner.getMember().getId()));
            alertOwner.setSender(nextOwner.getMember().getNickname());
            alertOwner.setType(GameMessage.MessageType.NEWOWNER);

            messagingTemplate.convertAndSend("/sub/gameroom" + RoomId, alertOwner);
        }
        return new ResponseEntity<>(new PrivateResponseBody<>(StatusCode.OK, "방을 나가셨습니다."), HttpStatus.OK);
    }
}
