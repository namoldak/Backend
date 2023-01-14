package com.example.namoldak.repository;

import com.example.namoldak.domain.GameStartSet;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import javax.annotation.PostConstruct;

// 기능 : Redis에 GameSet 저장
@Repository
public interface GameStartSetRepository extends CrudRepository<GameStartSet, Long> {
}
