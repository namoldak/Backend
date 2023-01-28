package com.example.namoldak.repository;

import com.example.namoldak.domain.Member;
import com.example.namoldak.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findAllByCategory(Pageable pageable, String category);
    Page<Post> findAllByMemberAndCategoryOrderByCreatedAtDesc(Pageable pageable, Member member, String category);
    List<Post> findAllByMemberAndCategory(Member member, String category);
    List<Post> findAllByCategory(String category);
}
