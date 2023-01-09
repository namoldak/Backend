package com.example.namoldak.dto.ResponseDto;

import com.example.namoldak.domain.Comment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CommentResponseDto {
    private Long id;
    private String nickname;
    private String comment;
    private List<CommentResponseDto> children = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;


    public CommentResponseDto(Comment comment){
        this.id = comment.getId();
        this.nickname = comment.getNickname();
        this.comment = comment.getComment();
        this.createdAt = comment.getCreatedAt();
        this.modifiedAt = comment.getModifiedAt();
    }
}
