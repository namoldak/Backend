package com.example.namoldak.repository;

import com.example.namoldak.domain.ImageFile;
import com.example.namoldak.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.util.List;

public interface ImageFileRepository extends JpaRepository<ImageFile, Long> {

    List<ImageFile> findAllByPost(Post post);
    @Transactional
    void deleteAllByPost(Post post);

}