package com.example.namoldak.domainModel;

import com.example.namoldak.domain.Comment;
import com.example.namoldak.domain.Post;
import com.example.namoldak.repository.CommentRepository;
import com.example.namoldak.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostCommand {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    //////////////TODO 포스트 관련
    // 포스트 저장하기
    public Post savePost(Post post){
        postRepository.save(post);
        return post;
    }

    // 포스트 삭제하기
    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }

    //////////////TODO 댓글 관련
    // 댓글 저장
    public void saveComment(Comment comment) {
        commentRepository.save(comment);
    }

    // 댓글 삭제
    public void deleteComment(Comment comment) {
        commentRepository.delete(comment);
    }

}
