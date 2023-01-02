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

    @Transactional
    public List<GameRoomResponseDto> mainPage(int pageNum){

        // DB에 저장된 모든 Room들을 리스트형으로 저장
        List<GameRoom> rooms = gameRoomRepository.findAll();
        // 필요한 키값들을 반환하기 위해서 미리 Dto 리스트 선언
        List<GameRoomResponseDto> gameRoomList = new ArrayList<>();

        for (GameRoom room : rooms) {
            // 모든 Room들이 모여있는 rooms에서 하나씩 추출 -> Room 객체 활용해서 GameRoomMember DB에서 찾은 후 리스트에 저장
            List<GameRoomMember> gameRoomMemberList = gameRoomMemberRepository.findByGameRoom(room);
            // 필요한 키값들을 반환하기 위해서 미리 Dto 리스트 선언
            List<MemberResponseDto> memberList = new ArrayList<>();
            for (GameRoomMember gameRoomMember : gameRoomMemberList) {
                // GameRoomMember 리스트의 길이 만큼 하나씩 늘려서 Member DB에서 찾아옴
                for (int i = 0; i < gameRoomMemberList.size(); i++) {
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
            }
        }
        return gameRoomList;
    }

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
}
