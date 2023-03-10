package com.example.namoldak.service;

import com.example.namoldak.domain.*;
import com.example.namoldak.domainModel.GameCommand;
import com.example.namoldak.domainModel.GameQuery;
import com.example.namoldak.domainModel.MemberCommand;
import com.example.namoldak.domainModel.MemberQuery;
import com.example.namoldak.dto.RequestDto.GameRoomRequestDto;
import com.example.namoldak.dto.ResponseDto.GameRoomResponseDto;
import com.example.namoldak.dto.ResponseDto.GameRoomResponseListDto;
import com.example.namoldak.dto.ResponseDto.MemberResponseDto;
import com.example.namoldak.repository.SessionRepository;
import com.example.namoldak.util.GlobalResponse.CustomException;
import com.example.namoldak.util.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

import static com.example.namoldak.util.GlobalResponse.code.StatusCode.*;

// 기능 : 게임룸 서비스
@Slf4j
@RequiredArgsConstructor
@Service
public class GameRoomService {
    // 의존성 주입
    private final GameService gameService;
    private final MemberQuery memberQuery;
    private final MemberCommand memberCommand;
    private final GameQuery gameQuery;
    private final GameCommand gameCommand;
    private final SessionRepository sessionRepository = SessionRepository.getInstance();


    // 게임룸 전체 조회
    @Transactional
    public GameRoomResponseListDto mainPage(Pageable pageable) {

        // DB에 저장된 모든 Room들을 리스트형으로 저장 + 페이징 처리
        Page<GameRoom> rooms = gameQuery.findGameRoomByPageable(pageable);

        // DB에 저장된 모든 Room들을 리스트로 가져와
        List<GameRoom> roomList = gameQuery.findAllGameRoomList();

        // 필요한 키값들을 반환하기 위해서 미리 Dto 리스트 선언
        List<GameRoomResponseDto> gameRoomList = new ArrayList<>();

        for (GameRoom room : rooms){
            // 모든 Room들이 모여있는 rooms에서 하나씩 추출 -> Room 객체 활용해서 GameRoomMember DB에서 찾은 후 리스트에 저장
            List<GameRoomAttendee> gameRoomAttendeeList = gameQuery.findAttendeeByGameRoom(room);
            // 필요한 키값들을 반환하기 위해서 미리 Dto 리스트 선언
            List<MemberResponseDto> memberList = new ArrayList<>();
            for (GameRoomAttendee gameRoomAttendee : gameRoomAttendeeList) {
                // GameRoomMember에 저장된 멤버 아이디로 DB 조회 후 데이터 저장
                Member eachMember = memberQuery.findMemberById(gameRoomAttendee.getMember().getId());

                // MemberResponseDto에 빌더 방식으로 각각의 데이터 값 넣어주기
                MemberResponseDto memberResponseDto = MemberResponseDto.builder()
                        .memberId(eachMember.getId())
                        .email(eachMember.getEmail())
                        .nickname(eachMember.getNickname())
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
                    .status(room.isStatus())
                    .build();

            // memberList에 데이터가 있다면 gameRoomList에 gameRoomResponseDto 추가
            // for문 끝날 때 까지 반복
            if (!memberList.isEmpty()) {
                gameRoomList.add(gameRoomResponseDto);
            }
        }
        int totalPage = rooms.getTotalPages();
        return new GameRoomResponseListDto(totalPage, gameRoomList);
    }

    // 게임룸 생성
    @Transactional
    public Map<String, String> makeGameRoom(Member member, GameRoomRequestDto gameRoomRequestDto) {

        // 게임방 만든 횟수 추가
        member.updateMakeRoom(1L);
        memberCommand.saveMember(member);

        // 빌더 활용해서 GameRoom 엔티티 데이터 채워주기
        GameRoom gameRoom = GameRoom.builder()
                .gameRoomName(gameRoomRequestDto.getGameRoomName())
                .gameRoomPassword(gameRoomRequestDto.getGameRoomPassword())
                .owner(member.getNickname())
                .status(true)
                .build();

        // DB에 데이터 저장
        gameCommand.saveGameRoom(gameRoom);

        // 생성자로 gameRoom, member 데이터를 담은 GameRoomMember 객체 완성
        GameRoomAttendee gameRoomAttendee = new GameRoomAttendee(gameRoom, member);

        // GameRoomMember DB에 해당 데이터 저장
        gameCommand.saveGameRoomAttendee(gameRoomAttendee);

        // data에 데이터를 담아주기 위해 HashMap 생성
        Map<String, String> roomInfo = new HashMap<>();

        // 앞에 키 값에 뒤에 밸류 값을 넣어줌
        roomInfo.put("gameRoomName", gameRoom.getGameRoomName());
        roomInfo.put("roomId", Long.toString(gameRoom.getGameRoomId()));
        roomInfo.put("gameRoomPassword", gameRoom.getGameRoomPassword());
        roomInfo.put("owner", gameRoom.getOwner());
        roomInfo.put("status", String.valueOf(gameRoom.isStatus()));

        return roomInfo;
    }

    // 게임룸 입장
    @Transactional
    public Map<String, String> enterGame(Long roomId, Member member) {

        // roomId로 DB에서 데이터 찾아와서 담음
        GameRoom enterGameRoom = gameQuery.findGameRoomByRoomIdLock(roomId);

        // 방의 상태가 false면 게임이 시작 중이거나 가득 찬 상태이기 때문에 출입이 불가능
        if (!enterGameRoom.isStatus()) {
            // 뒤로 넘어가면 안 되니까 return으로 호다닥 끝내버림
            throw new CustomException(ALREADY_PLAYING);
        }

        // 입장하려는 게임방을 이용해서 GameRoomMember DB에서 유저 정보 전부 빼와서 리스트형에 저장 (입장 정원 확인 용도)
        List<GameRoomAttendee> gameRoomAttendeeList = gameQuery.findAttendeeByGameRoom(enterGameRoom);

        // 만약 방에 4명이 넘어가면
        if (gameRoomAttendeeList.size() > 3) {
            // 입장 안 된다고 입구컷
            throw new CustomException(CANT_ENTER);
        }

        // 멤버가 방에 입장한 횟수 1개 증가
        member.updateEnterGame(1L);
        memberCommand.saveMember(member);

        // for문으로 리스트에서 gameRoomMember 하나씩 빼주기
        for (GameRoomAttendee gameRoomAttendee : gameRoomAttendeeList) {
            // gameRoomMember에서 얻은 유저 아이디로 Member 객체 저장
            Member member1 = memberQuery.findMemberById(gameRoomAttendee.getMember().getId());
            // 현재 들어가려는 유저의 ID와 게임에 들어가있는 멤버의 ID가 똑같으면 입구컷 해버림
            if (member.getId().equals(member1.getId())) {
//                return new PrivateResponseBody(StatusCode.MEMBER_DUPLICATED, "이미 입장해있닭!!");
                throw new CustomException(MEMBER_DUPLICATED);
            }
        }

        GameRoomAttendee gameRoomAttendee = new GameRoomAttendee(enterGameRoom, member);

        // DB에 데이터 저장
        gameCommand.saveGameRoomAttendee(gameRoomAttendee);

        Map<String, Object> contentSet = new HashMap<>();
        contentSet.put("owner", enterGameRoom.getOwner());
        contentSet.put("memberCnt", gameRoomAttendeeList.size());
        contentSet.put("enterComment", roomId + "번 방에" + String.valueOf(member.getId()) + "님이 입장하셨습니닭!");

        // 게임 메세지 전송
        gameService.sendGameMessage(roomId, GameMessage.MessageType.ENTER, contentSet, null, member.getNickname());

        // 해시맵으로 데이터 정리해서 보여주기
        Map<String, String> roomInfo = new HashMap<>();

        roomInfo.put("gameRoomName", enterGameRoom.getGameRoomName());
        roomInfo.put("roomId", String.valueOf(enterGameRoom.getGameRoomId()));
        roomInfo.put("owner", enterGameRoom.getOwner());
        roomInfo.put("status", String.valueOf(enterGameRoom.isStatus()));

        return roomInfo;
    }

    // 비정상 게임룸 접속자 방지
    public void enterVerify(Long roomId, UserDetailsImpl userDetails) {
        // 비회원일 경우 에러 메세지 보내기
        if (userDetails == null) {
            throw new CustomException(INVALID_TOKEN);
        }

        // 검증을 위한 카운트 미리 선언
        int cnt = 0;

        // 해당 방의 모든 참가자들 리스트로 저장
        List<GameRoomAttendee> gameRoomAttendeeList = gameQuery.findAttendeeByRoomId(roomId);

        // 참가자의 닉네임과 접속한 사람의 닉네임이 동일하면 cnt 1개씩 올림
        for (GameRoomAttendee gameRoomAttendee : gameRoomAttendeeList) {
            if (userDetails.getMember().getNickname().equals(gameRoomAttendee.getMemberNickname())){
                cnt++;
            }
        }

        // cnt가 1이 아닐 경우 뭔가가 오류가 있기 때문에 들어갈 수 없다고 에러 메세지 띄워줌
        if (cnt != 1) {
            throw new CustomException(BAD_REQUEST);
        }
    }

    // 게임룸 키워드 조회
    public GameRoomResponseListDto searchGame(Pageable pageable, String keyword) {
        // 게임룸 이름을 keyword(검색어)로 잡고 조회 + 페이징 처리
        Page<GameRoom> rooms = gameQuery.findGameRoomByContainingKeyword(pageable, keyword);

        if(rooms.isEmpty()){
            throw new CustomException(NOT_EXIST_ROOMS);
        }

        List<GameRoomResponseDto> gameRoomList = new ArrayList<>();
        for (GameRoom room : rooms) {
            // 게임룸에 입장해 있는 멤버 조회
            List<GameRoomAttendee> gameRoomAttendeeList = gameQuery.findAttendeeByGameRoom(room);
            List<MemberResponseDto> memberList = new ArrayList<>();

            for (GameRoomAttendee gameRoomAttendee : gameRoomAttendeeList){
                Member eachMember = memberQuery.findMemberById(gameRoomAttendee.getMember().getId());

                // 멤버로부터 필요한 정보인 id, email, nickname만 Dto에 담아주기
                MemberResponseDto memberResponseDto = MemberResponseDto.builder()
                        .memberId(eachMember.getId())
                        .email(eachMember.getEmail())
                        .nickname(eachMember.getNickname())
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
                    .status(room.isStatus())
                    .build();
            // 방에 멤버가 1명 이상이라면, 담아줬던 데이터 저장하기
            if (!memberList.isEmpty()) {
                gameRoomList.add(gameRoomResponseDto);
            }
        }
        // 저장된 정보가 담긴 리스트를 반환
        int totalPage = rooms.getTotalPages();
        return new GameRoomResponseListDto(totalPage, gameRoomList);
    }

    // 방 나가기
    @Transactional
    public void roomExit(Long roomId, Member member) {
        // 나가려고 하는 방 정보 DB에서 불러오기
        GameRoom enterGameRoom = gameQuery.findGameRoomByRoomId(roomId);

        // 나가려고 하는 GameRoomMember를 member 객체로 DB에서 조회
        GameRoomAttendee gameRoomAttendee = gameQuery.findAttendeeByMember(member);

        // 위에서 구한 GameRoomMemeber 객체로 DB 데이터 삭제
        gameCommand.deleteGameRoomAttendee(gameRoomAttendee);

        // 게임방에 남아있는 유저들 구하기
        List<GameRoomAttendee> existGameRoomAttendee = gameQuery.findAttendeeByGameRoom(enterGameRoom);

        // 남아있는 유저의 수가 0명이라면 게임방 DB에서 데이터 삭제
        if (existGameRoomAttendee.size() == 0) {
            // 혼자 있을 때 방에서 나간 횟수 증가
            member.updateSoloExit(1L);
            memberCommand.saveMember(member);
            gameCommand.deleteGameRoom(enterGameRoom);

            // 게임 채팅방도 삭제해줌
            sessionRepository.deleteAllclientsInRoom(roomId);
        }

        // 게임이 시잓된 상태에서 나갔을 경우
        if (!enterGameRoom.isStatus()){
                // 게임을 끝내버림
                gameService.forcedEndGame(roomId, member.getNickname());
        }

        // 방을 나갈 경우의 알림 문구와 나간 이후의 방 인원 수를 저장하기 위한 해시맵
        Map<String, Object> contentSet = new HashMap<>();
        contentSet.put("memberCnt", existGameRoomAttendee.size());
        contentSet.put("alert", member.getNickname() + " 님이 방을 나가셨습니닭!");

        // 누가 나갔는지 알려줄 메세지 정보 세팅
        gameService.sendGameMessage(roomId, GameMessage.MessageType.LEAVE, contentSet, null, member.getNickname());

        // 만약에 나간 사람이 그 방의 방장이고 남은 인원이 0명이 아닐 경우에
        if (member.getNickname().equals(enterGameRoom.getOwner()) && !existGameRoomAttendee.isEmpty()){
            // 남은 사람들의 수 만큼 랜덤으로 돌려서 나온 멤버 ID
            String nextOwner = existGameRoomAttendee.get((int) (Math.random() * existGameRoomAttendee.size())).getMemberNickname();
            enterGameRoom.setOwner(nextOwner);
            gameService.sendGameMessage(roomId, GameMessage.MessageType.NEWOWNER, null, null, nextOwner);
        }
    }

    // 방장 정보 조회
    public Map<String, String> ownerInfo(Long roomId) {
        // 전달받은 roomId로 DB 조회 후 저장
        GameRoom enterRoom = gameQuery.findGameRoomByRoomId(roomId);
        // 방에서 방장의 닉네임을 저장
        String ownerNickname = enterRoom.getOwner();
        // 닉네임을 통해서 유저 객체를 불러온 후에 ID를 저장
        Member member = memberQuery.findMemberByNickname(ownerNickname);
        String ownerId = member.getId().toString();

        // 데이터를 전달할 해시맵 생성 후 넣어주기
        Map<String, String> ownerInfo = new HashMap<>();
        ownerInfo.put("ownerId", ownerId);
        ownerInfo.put("ownerNickname", ownerNickname);

        return ownerInfo;
    }

    // signalHandler에서 세션 끊김을 감지했을 때 게임방에서 참가자 정보를 정리
    public void exitGameRoomAboutSession(String nickname, Long roomId) {
        Member member = memberQuery.findMemberByNickname(nickname);
        List<GameRoomAttendee> gameRoomAttendeeList = gameQuery.findAttendeeByRoomId(roomId);
        for(GameRoomAttendee gameRoomAttendee : gameRoomAttendeeList) {
            if(nickname.equals(gameRoomAttendee.getMemberNickname())){
                roomExit(roomId, member);
            }
        }
    }
}
