package com.example.namoldak.service;

import com.example.namoldak.domain.Comment;
import com.example.namoldak.domain.Member;
import com.example.namoldak.domain.Post;
import com.example.namoldak.dto.RequestDto.CommentRequestDto;
import com.example.namoldak.repository.CommentRepository;
import com.example.namoldak.repository.PostRepository;
import com.example.namoldak.util.GlobalResponse.CustomException;
import com.example.namoldak.util.GlobalResponse.code.StatusCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    // 댓글 작성
    public void createComment(Long postId, CommentRequestDto commentRequestDto, Member member) {
        Post post= postRepository.findById(postId).orElseThrow(
                () -> new CustomException(StatusCode.POST_NOT_FOUND)
        );
        Comment comment = new Comment(commentRequestDto, member, post);
        commentRepository.save(comment);
    }

    // 댓글 수정
    public void updateComment(Long commentId, CommentRequestDto commentRequestDto, Member member) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new CustomException(StatusCode.COMMENT_NOT_FOUND)
        );

        if (!member.getNickname().equals(comment.getNickname())) {
            throw new CustomException(StatusCode.NO_AUTH_MEMBER);
        }

        comment.update(commentRequestDto);
    }

    // 댓글 삭제
    public void deleteComment(Long commentId, Member member) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new CustomException(StatusCode.COMMENT_NOT_FOUND)
        );

        if (!member.getNickname().equals(comment.getNickname())) {
            throw new CustomException(StatusCode.NO_AUTH_MEMBER);
        }

        commentRepository.delete(comment);
    }
}

