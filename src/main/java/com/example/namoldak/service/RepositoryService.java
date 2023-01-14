package com.example.namoldak.service;

import com.example.namoldak.domain.Comment;
import com.example.namoldak.domain.Post;
import com.example.namoldak.repository.CommentRepository;
import com.example.namoldak.repository.PostRepository;
import com.example.namoldak.util.GlobalResponse.CustomException;
import com.example.namoldak.util.GlobalResponse.code.StatusCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class RepositoryService {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;


    //////////////TODO 댓글 관련
    // 댓글 ID로 댓글 찾아오기
    public Comment findCommentById(Long commentId){
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new CustomException(StatusCode.COMMENT_NOT_FOUND)
        );
        return comment;
    }

    // 포스트 객체로 모든 댓글 리스트형으로 찾아오기
    public List<Comment> findAllCommentByPost(Post post){
        List<Comment> comments = commentRepository.findByPost(post);
        return comments;
    }

    //////////////TODO 포스트 관련
    // 포스트 ID로 포스트 찾아오기
    public Post findPostById(Long postId){
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new CustomException(StatusCode.POST_NOT_FOUND)
        );
        return post;
    }
    // 페이징 처리해서 모든 포스트 불러오기
    public Page<Post> findAllPostByPageable(Pageable pageable){
        Page<Post> postList = postRepository.findAll(pageable);
        return postList;
    }

    // 카테고리로 분류해서 모든 포스트 불러오기
    public Page<Post> findAllPostByPageableAndCategory(Pageable pageable, String category){
        Page<Post> postList = postRepository.findAllByCategoryOrderByCreatedAtDesc(pageable, category);
        return postList;
    }
}
