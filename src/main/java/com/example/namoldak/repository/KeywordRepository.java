package com.example.namoldak.repository;

import com.example.namoldak.domain.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// 기능 : 게임 카테고리 및 키워드 레포
@Repository
public interface KeywordRepository extends JpaRepository<Keyword, Long> {
    List<Keyword> findByCategory(String category);

}
