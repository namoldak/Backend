package com.example.namoldak.repository;

import com.example.namoldak.domain.Comment;
import com.example.namoldak.domain.ImageFile;
import com.example.namoldak.domain.Member;
import com.example.namoldak.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface ImageFileRepository extends JpaRepository<ImageFile, Long> {

    List<ImageFile> findAllByPost(Post post);
    @Transactional
    void deleteAllByMember(Member member);
    @Transactional
    void deleteAllByPost(Post post);

    boolean existsByMember(Member member);


}