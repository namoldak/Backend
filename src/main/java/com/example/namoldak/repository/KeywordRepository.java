package com.example.namoldak.repository;

import com.example.namoldak.domain.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {


    @Query("select k from Keyword k where k.category = :category order by rand() limit 4")
    List<Keyword> findByCategory(@Param("category") String category);

}
