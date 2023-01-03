package com.example.namoldak.service;

import com.example.namoldak.domain.GameRoom;
import com.example.namoldak.domain.GameRoomMember;
import com.example.namoldak.domain.Member;
import com.example.namoldak.dto.RequestDto.GameRoomRequestDto;
import com.example.namoldak.dto.ResponseDto.GameRoomResponseDto;
import com.example.namoldak.dto.ResponseDto.MemberResponseDto;
import com.example.namoldak.dto.ResponseDto.PrivateResponseBody;
import com.example.namoldak.exception.StatusCode;
import com.example.namoldak.repository.GameRoomMemberRepository;
import com.example.namoldak.repository.GameRoomRepository;
import com.example.namoldak.repository.MemberRepository;
import com.example.namoldak.util.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class GameRoomService {
    // 의존성 주입
    private final JwtUtil jwtUtil;
    private final GameRoomRepository gameRoomRepository;
    private final GameRoomMemberRepository gameRoomMemberRepository;
    private final MemberRepository memberRepository;

    // 게임룸 전체 조회
    @Transactional
    public List<GameRoomResponseDto> mainPage(Pageable pageable){

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
                if(!memberList.isEmpty()){
                    gameRoomList.add(gameRoomResponseDto);
                }
//            }
        }
        return gameRoomList;
    }

    // 게임룸 생성
    @Transactional
    public ResponseEntity<?> makeGameRoom(Member member, GameRoomRequestDto gameRoomRequestDto){

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
    public ResponseEntity<?> enterGame(Long roomId, Member member){
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

        // 해시맵으로 데이터 정리해서 보여주기
        HashMap<String, String> roomInfo = new HashMap<>();

        roomInfo.put("gameRoomName", enterGameRoom.get().getGameRoomName());
        roomInfo.put("roomId", String.valueOf(enterGameRoom.get().getGameRoomId()));
        roomInfo.put("owner", enterGameRoom.get().getOwner());
        roomInfo.put("status", enterGameRoom.get().getStatus());

        return new ResponseEntity<>(new PrivateResponseBody<>(StatusCode.OK, roomInfo), HttpStatus.OK);
    }
}
