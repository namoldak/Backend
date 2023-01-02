package com.example.namoldak.service;

import com.example.namoldak.domain.GameRoom;
import com.example.namoldak.domain.GameRoomMember;
import com.example.namoldak.domain.Member;
import com.example.namoldak.dto.GameRoomRequestDto;
import com.example.namoldak.dto.PrivateResponseBody;
import com.example.namoldak.exception.StatusCode;
import com.example.namoldak.repository.GameRoomMemberRepository;
import com.example.namoldak.repository.GameRoomRepository;
import com.example.namoldak.repository.MemberRepository;
import com.example.namoldak.util.jwt.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class GameRoomService {
    private final JwtUtil jwtUtil;
    private final GameRoomRepository gameRoomRepository;
    private final GameRoomMemberRepository gameRoomMemberRepository;
    private final MemberRepository memberRepository;

//    public Member authorizeToken(HttpServletRequest request){
//
//        // Access 토큰 유효성 확인
//        if (request.getHeader("Authorization") == null) {
//            // Header에서 가져온 토큰이 비어있다면 예외 처리 진행
//            throw new IllegalArgumentException("토큰이 없잖슴!!");
//        }
//        String token = jwtUtil.resolveToken(request);
//        Claims claims = jwtUtil.getUserInfoFromToken(token);
//        Member member = memberRepository.findByEmail(claims.getSubject()).orElseThrow(
//                () -> new IllegalArgumentException("토큰 오류"));
//        return member;
//    }

//    @Transactional
//    public ResponseEntity<?> mainPage(int pageNum){
//        int size = 4;
//        int sizeInPage = pageNum * 4;
//
//        List<GameRoom> gameRoomList = gameRoomRepository.findAll();
//        for (GameRoom gameRoom : gameRoomList) {
//            List<GameRoomMember> gameRoomMemberList = gameRoomMemberRepository.findByGameRoom(gameRoom);
//        }
//
//
//
//
//    }

    @Transactional
    public ResponseEntity<?> makeGameRoom(Member member, GameRoomRequestDto gameRoomRequestDto){

        GameRoom gameRoom = GameRoom.builder()
                .gameRoomName(gameRoomRequestDto.getGameRoomName())
                .gameRoomPassword(gameRoomRequestDto.getGameRoomPassword())
                .owner(member.getNickname())
                .status("true")
                .build();

        gameRoomRepository.save(gameRoom);

        GameRoomMember gameRoomMember = new GameRoomMember(gameRoom, member);
        gameRoomMemberRepository.save(gameRoomMember);

        HashMap<String, String> roomInfo = new HashMap<>();

        roomInfo.put("gameRoomName", gameRoom.getGameRoomName());
        roomInfo.put("roomId", Long.toString(gameRoom.getGameRoomId()));
        roomInfo.put("gameRoomPassword", gameRoom.getGameRoomPassword());
        roomInfo.put("owner", gameRoom.getOwner());
        roomInfo.put("status", gameRoom.getStatus());

        return new ResponseEntity<>(new PrivateResponseBody<>(StatusCode.OK, roomInfo), HttpStatus.OK);
    }
}
