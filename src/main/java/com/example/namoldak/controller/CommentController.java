package com.example.namoldak.controller;

import com.example.namoldak.dto.RequestDto.CommentRequestDto;
import com.example.namoldak.dto.ResponseDto.CommentResponseDto;
import com.example.namoldak.service.CommentService;
import com.example.namoldak.util.GlobalResponse.GlobalResponseDto;
import com.example.namoldak.util.GlobalResponse.ResponseUtil;
import com.example.namoldak.util.GlobalResponse.code.StatusCode;
import com.example.namoldak.util.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

// 기능 : 댓글 컨트롤러
@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // 댓글 전체 불러오기
    @GetMapping("/{postId}/comments/all")
    public ResponseEntity<?> getAllComment(@PathVariable Long postId,
                                           @PageableDefault(page = 0, size = 10) Pageable pageable) {
        return ResponseUtil.response(commentService.getAllComment(postId, pageable));
    }

    // 댓글 작성
    @PostMapping("/{postId}/comments")
    public ResponseEntity<CommentResponseDto> createComment(
            @PathVariable Long postId,
            @RequestBody CommentRequestDto commentRequestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseUtil.response(commentService.createComment(postId, commentRequestDto, userDetails.getMember()));
    }

    // 대댓글 작성
    @PostMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<CommentResponseDto> createReply(@PathVariable Long postId,
                                         @PathVariable Long commentId,
                                         @RequestBody CommentRequestDto commentRequestDto,
                                         @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseUtil.response(commentService.createReply(postId, commentId, commentRequestDto, userDetails.getMember()));
    }

    // 댓글, 대댓글 수정
    @PutMapping("/comments/{commentId}")
    public ResponseEntity<CommentResponseDto> updateComment(
            @PathVariable Long commentId,
            @RequestBody CommentRequestDto commentRequestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseUtil.response(commentService.updateComment(commentId, commentRequestDto, userDetails.getMember()));
    }

    // 댓글, 대댓글 삭제
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<GlobalResponseDto> deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        commentService.deleteComment(commentId, userDetails.getMember());
        return ResponseUtil.response(StatusCode.DELETE_OK);
    }
}
