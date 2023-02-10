package com.example.namoldak.repository;

import com.example.namoldak.domain.Comment;
import com.example.namoldak.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import javax.transaction.Transactional;

// 기능 : 댓글 정보 레포
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findAllByPost_IdOrderByCreatedAtDesc(Pageable pageable, Long id);
    @Transactional
    void deleteAllByMember(Member member);
    boolean existsByMember(Member member);
}

