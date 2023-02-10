package com.example.namoldak.service;

import com.example.namoldak.domain.Comment;
import com.example.namoldak.domain.Member;
import com.example.namoldak.domain.Post;
import com.example.namoldak.domainModel.PostCommand;
import com.example.namoldak.domainModel.PostQuery;
import com.example.namoldak.dto.RequestDto.CommentRequestDto;
import com.example.namoldak.dto.ResponseDto.CommentResponseDto;
import com.example.namoldak.dto.ResponseDto.CommentResponseListDto;
import com.example.namoldak.repository.CommentRepository;
import com.example.namoldak.util.GlobalResponse.CustomException;
import com.example.namoldak.util.GlobalResponse.code.StatusCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;

// 기능 : 댓글 서비스
@Service
@Slf4j
@RequiredArgsConstructor
public class CommentService {
    private final PostQuery postQuery;
    private final PostCommand postCommand;
    private final CommentRepository commentRepository;

    // 댓글 전체 불러오기
    public CommentResponseListDto getAllComment(Long id, Pageable pageable) {
        Page<Comment> comments = commentRepository.findAllByPost_IdOrderByCreatedAtDesc(pageable, id);

        List<CommentResponseDto> commentResponseDtoList = new ArrayList<>();
        for (Comment comment : comments) {
            commentResponseDtoList.add(new CommentResponseDto(comment));
        }
        int totalPage = comments.getTotalPages();
        return new CommentResponseListDto(totalPage, commentResponseDtoList);
    }

    // 댓글 작성
    public CommentResponseDto createComment(Long postId, CommentRequestDto commentRequestDto, Member member) {
        // 매개변수로 받아온 포스트 Id를 활용해서 Post 객체 저장
        Post post = postQuery.findPostById(postId);
        // Comment 생성자로 객체 생성 후 반환
        Comment comment = new Comment(commentRequestDto, member, post);
        // 코멘트 저장
        postCommand.saveComment(comment);
        // 객체를 dto에 담아
        return new CommentResponseDto(comment);
    }

    // 대댓글 작성
    public CommentResponseDto createReply(Long postId, Long commentId, CommentRequestDto commentRequestDto, Member member) {
        Post post = postQuery.findPostById(postId);

        Comment comment = postQuery.findCommentById(commentId);

        Comment reply = new Comment(commentRequestDto, member, post, comment);
        postCommand.saveComment(reply);
        return new CommentResponseDto(reply);
    }

    // 댓글, 대댓글 수정
    @Transactional
    public CommentResponseDto updateComment(Long commentId, CommentRequestDto commentRequestDto, Member member) {
        // 매개변수로 받아온 코멘트 Id를 활용해서 Comment 객체 저장
        Comment comment = postQuery.findCommentById(commentId);

        // 매개변수로 받아온 닉네임과 코멘트의 작성자가 동일하지 않다면 예외 처리
        if (!member.getNickname().equals(comment.getNickname())) {
            throw new CustomException(StatusCode.NO_AUTH_MEMBER);
        }
        // 코멘트 업데이트
        comment.update(commentRequestDto);
        return new CommentResponseDto(comment);
    }

    // 댓글, 대댓글 삭제
    @Transactional
    public void deleteComment(Long commentId, Member member) {
        // 매개변수로 받아온 코멘트 Id를 활용해서 Comment 객체 저장
        Comment comment = postQuery.findCommentById(commentId);

        // 매개변수로 받아온 닉네임과 코멘트의 작성자가 동일하지 않다면 예외 처리
        if (!member.getNickname().equals(comment.getNickname())) {
            throw new CustomException(StatusCode.NO_AUTH_MEMBER);
        }
        // 코멘트 삭제
        postCommand.deleteComment(comment);
    }
}

