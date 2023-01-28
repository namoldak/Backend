package com.example.namoldak.repository;

import com.example.namoldak.domain.ImageFile;
import com.example.namoldak.domain.Member;
import com.example.namoldak.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findAllByCategory(Pageable pageable, String category);
    Page<Post> findAllByMemberAndCategoryOrderByCreatedAtDesc(Pageable pageable, Member member, String category);
    Page<Post> findByTitleContaining(Pageable pageable, String keyword);
    List<Post> findAllByMemberAndCategory(Member member, String category);
    List<Post> findAllByCategory(String category);
    @Transactional
    void deleteAllByMember(Member member);
    boolean existsByMember(Member member);
}
