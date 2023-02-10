package com.example.namoldak.repository;

import com.example.namoldak.domain.Member;
import com.example.namoldak.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import javax.transaction.Transactional;
import java.util.List;

// 기능 : 포스트 정보 레포
public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findAllByCategory(Pageable pageable, String category);   // 카테고리 별 전체 조회 (페이징O)
    Page<Post> findAllByMemberAndCategoryOrderByCreatedAtDesc(Pageable pageable, Member member, String category); // 카테고리별 자신이 쓴 글 조회
    Page<Post> findAllByCategoryContainingAndTitleContaining(Pageable pageable, String category, String keyword); // 카테고리별 검색 조회
    List<Post> findAllByMemberAndCategory(Member member, String category); // 내가 쓴 피드백 조회용
    List<Post> findAllByCategory(String category);  //카테고리별 전체 조회 (페이징X)
    @Transactional
    void deleteAllByMember(Member member);  // 해당 멤버가 작성한 글 모두 삭제
    boolean existsByMember(Member member);  // 해당 멤버가 작성한 글 존재여부 확인
}
