package com.example.namoldak.domainModel;

import com.example.namoldak.domain.*;
import com.example.namoldak.repository.GameRoomAttendeeRepository;
import com.example.namoldak.repository.GameRoomRepository;
import com.example.namoldak.repository.GameStartSetRepository;
import com.example.namoldak.repository.RewardReposiroty;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

// 기능 : 게임 도메인 관련 DB CUD 관리
@Service
@RequiredArgsConstructor
public class GameCommand {

    private final GameRoomRepository gameRoomRepository;
    private final GameRoomAttendeeRepository gameRoomAttendeeRepository;
    private final GameStartSetRepository gameStartSetRepository;
    private final RewardReposiroty rewardReposiroty;

    //////////////TODO GameRoom 관련
    // 게임방 저장하기
    public void saveGameRoom(GameRoom gameRoom) {
        gameRoomRepository.save(gameRoom);
    }

    // 게임방 삭제하기
    public void deleteGameRoom(GameRoom gameRoom) {
        gameRoomRepository.delete(gameRoom);
    }

    //////////////TODO GameRoomAttendee 관련
    // 참가자 저장
    public void saveGameRoomAttendee(GameRoomAttendee gameRoomAttendee) {
        gameRoomAttendeeRepository.save(gameRoomAttendee);
    }

    // 참가자 삭제
    public void deleteGameRoomAttendee(GameRoomAttendee gameRoomAttendee) {
        gameRoomAttendeeRepository.delete(gameRoomAttendee);
    }

    //////////////TODO GameStartSet 관련
    // GameStartSet 저장하기
    public void saveGameStartSet(GameStartSet gameStartSet) {
        gameStartSetRepository.save(gameStartSet);
    }

    // GameStartSet 객체로 DB에서 삭제하기
    public void deleteGameStartSetByRoomId(Long roomId) {
        gameStartSetRepository.deleteByRoomId(roomId);
    }

    //////////////TODO Reward 관련
    // 리워드 저장하기
    public void saveReward(Reward reward) {
        rewardReposiroty.save(reward);
    }

}
