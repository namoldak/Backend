package com.example.namoldak.repository;

import com.example.namoldak.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findAllByCategoryOrderByCreatedAtDesc(Pageable pageable, String category);

    List<Post> findAllByCategory(String category);
}
