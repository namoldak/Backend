package com.example.namoldak.repository;

import com.example.namoldak.domain.ImageFile;
import com.example.namoldak.domain.Member;
import com.example.namoldak.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import javax.transaction.Transactional;
import java.util.List;

// 기능 : 이미지 저장 정보 레포
public interface ImageFileRepository extends JpaRepository<ImageFile, Long> {

    List<ImageFile> findAllByPost(Post post);  // 포스트 객체로 글 조회
    List<ImageFile> findAllByMember(Member member); // 해당 멤버에 대한 이미지들을 모두 조회
    @Transactional
    void deleteAllByMember(Member member);  // 해당 멤버에 대한 모든 이미지 파일을 지움
    @Transactional
    void deleteAllByPost(Post post); // 포스트 객체로 글 삭제
    boolean existsByMember(Member member); // 해당 멤버가 작성한 포스트 존재여부 확인


}