package com.example.namoldak.repository;

import com.example.namoldak.domain.GameStartSet;
import com.example.namoldak.domain.GameStartSet2;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;

@Repository
@RequiredArgsConstructor
public class GameStartSetRepository2 {

    private static final String GAME_SET = "GAME_SET";
    private final RedisTemplate<String, Object> redisTemplate;
    private HashOperations<String, Long, GameStartSet2> opsHashGameSet;

    @PostConstruct
    private void init() {
        opsHashGameSet = redisTemplate.opsForHash();
    }

    // 특정 게임셋을 불러와야함
    public GameStartSet2 findGameSetById(Long roomId) {
        return opsHashGameSet.get(GAME_SET , roomId);
    }

    // 게임 Set 생성
    public GameStartSet2 saveGameSet(GameStartSet2 gameStartSet){
        opsHashGameSet.put(GAME_SET, gameStartSet.getRoomId(), gameStartSet);
        return gameStartSet;
    }
    // 채팅룸 삭제
    public void deleteGameSet(Long roomId){
        opsHashGameSet.delete(GAME_SET, roomId);
    }
}
