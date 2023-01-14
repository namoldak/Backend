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
    private final RepositoryService repositoryService;

    // 댓글 작성
    public void createComment(Long postId, CommentRequestDto commentRequestDto, Member member) {
        // 매개변수로 받아온 포스트 Id를 활용해서 Post 객체 저장
        Post post = repositoryService.findPostById(postId);
        // Comment 생성자로 객체 생성 후 반환
        Comment comment = new Comment(commentRequestDto, member, post);
        // 코멘트 저장
        commentRepository.save(comment);
    }

    // 댓글 수정
    public void updateComment(Long commentId, CommentRequestDto commentRequestDto, Member member) {
        // 매개변수로 받아온 코멘트 Id를 활용해서 Comment 객체 저장
        Comment comment = repositoryService.findCommentById(commentId);

        // 매개변수로 받아온 닉네임과 코멘트의 작성자가 동일하지 않다면 예외 처리
        if (!member.getNickname().equals(comment.getNickname())) {
            throw new CustomException(StatusCode.NO_AUTH_MEMBER);
        }
        // 코멘트 업데이트
        comment.update(commentRequestDto);
    }

    // 댓글 삭제
    public void deleteComment(Long commentId, Member member) {
        // 매개변수로 받아온 코멘트 Id를 활용해서 Comment 객체 저장
        Comment comment = repositoryService.findCommentById(commentId);

        // 매개변수로 받아온 닉네임과 코멘트의 작성자가 동일하지 않다면 예외 처리
        if (!member.getNickname().equals(comment.getNickname())) {
            throw new CustomException(StatusCode.NO_AUTH_MEMBER);
        }
        // 코멘트 삭제
        commentRepository.delete(comment);
    }
}

