package com.example.namoldak.repository;

import com.example.namoldak.domain.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

// 기능: 게임 시작시 주어지는 랜덤 키워드 레포
public interface KeywordRepository extends JpaRepository<Keyword, Long> {

    // 랜덤 키워드 4인일 경우
    @Query(value = "select * from keyword k where k.category = :category order by rand() limit 4", nativeQuery = true)
    List<Keyword> findTop4ByCategory(@Param("category") String category);

    // 랜덤 키워드 3인일 경우
    @Query(value = "select * from keyword k where k.category = :category order by rand() limit 3", nativeQuery = true)
    List<Keyword> findTop3ByCategory(@Param("category") String category);
}
