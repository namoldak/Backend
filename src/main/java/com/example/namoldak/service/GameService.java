package com.example.namoldak.service;

import com.example.namoldak.domain.GameRoom;
import com.example.namoldak.repository.GameRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRoomRepository gameRoomRepository;

    public void spotlight(Long gameRoomId){
        // 게임룸 조회 (게임룸 상태를 조회하기 위한 조회)
        GameRoom playRoom = gameRoomRepository.findByGameRoomId(gameRoomId);


    }
}
