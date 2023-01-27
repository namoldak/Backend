package com.example.namoldak.controller;

import com.example.namoldak.dto.RequestDto.CommentRequestDto;
import com.example.namoldak.dto.ResponseDto.CommentResponseDto;
import com.example.namoldak.service.CommentService;
import com.example.namoldak.util.GlobalResponse.GlobalResponseDto;
import com.example.namoldak.util.GlobalResponse.ResponseUtil;
import com.example.namoldak.util.GlobalResponse.code.StatusCode;
import com.example.namoldak.util.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/{postId}/comments")
    public ResponseEntity<CommentResponseDto> createComment(
            @PathVariable Long postId,
            @RequestBody CommentRequestDto commentRequestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseUtil.response(commentService.createComment(postId, commentRequestDto, userDetails.getMember()));
    }

    @PostMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<CommentResponseDto> createReply(@PathVariable Long postId,
                                         @PathVariable Long commentId,
                                         @RequestBody CommentRequestDto commentRequestDto,
                                         @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseUtil.response(commentService.createReply(postId, commentId, commentRequestDto, userDetails.getMember()));
    }

    @PutMapping("/comments/{commentId}")
    public ResponseEntity<CommentResponseDto> updateComment(
            @PathVariable Long commentId,
            @RequestBody CommentRequestDto commentRequestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseUtil.response(commentService.updateComment(commentId, commentRequestDto, userDetails.getMember()));
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<GlobalResponseDto> deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        commentService.deleteComment(commentId, userDetails.getMember());
        return ResponseUtil.response(StatusCode.DELETE_OK);
    }
}
