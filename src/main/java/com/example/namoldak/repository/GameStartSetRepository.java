package com.example.namoldak.repository;

import com.example.namoldak.domain.GameStartSet;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import javax.annotation.PostConstruct;

// 기능 : Redis에 GameSet 저장
@Repository
@RequiredArgsConstructor
public class GameStartSetRepository {

    private static final String GAME_SET = "GAME_SET";
    private final RedisTemplate<String, Object> redisTemplate;
    private HashOperations<String, Long, GameStartSet> opsHashGameSet;

    @PostConstruct
    private void init(){
        opsHashGameSet = redisTemplate.opsForHash();
    }

    // 특정 게임셋을 불러와야함
    public GameStartSet findGameSetById(Long roomId){
        return opsHashGameSet.get(GAME_SET , roomId);
    }

    // 게임 Set 생성
    public GameStartSet saveGameSet(GameStartSet gameStartSet){
        opsHashGameSet.put(GAME_SET, gameStartSet.getRoomId(), gameStartSet);
        return gameStartSet;
    }
    // 채팅룸 삭제
    public void deleteGameSet(Long roomId){
        opsHashGameSet.delete(GAME_SET, roomId);
    }
}
