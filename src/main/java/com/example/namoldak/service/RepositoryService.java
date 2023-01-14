package com.example.namoldak.service;

import com.example.namoldak.domain.Comment;
import com.example.namoldak.domain.Post;
import com.example.namoldak.repository.CommentRepository;
import com.example.namoldak.repository.PostRepository;
import com.example.namoldak.util.GlobalResponse.CustomException;
import com.example.namoldak.util.GlobalResponse.code.StatusCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class RepositoryService {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    public Post findPostById(Long postId){
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new CustomException(StatusCode.POST_NOT_FOUND)
        );
        return post;
    }

    public Comment findCommentById(Long commentId){
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new CustomException(StatusCode.COMMENT_NOT_FOUND)
        );
        return comment;
    }
}
