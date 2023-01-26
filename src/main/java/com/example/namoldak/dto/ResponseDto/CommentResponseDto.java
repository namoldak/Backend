package com.example.namoldak.dto.ResponseDto;

import com.example.namoldak.domain.Comment;
import com.example.namoldak.domain.Member;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class CommentResponseDto {
    private Long id;
    private String nickname;
    private String comment;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime modifiedAt;
//    private List<CommentResponseDto> children = new ArrayList<>();


    public CommentResponseDto(Comment comment) {
        this.id          = comment.getId();
        this.nickname    = comment.getNickname();
        this.comment     = comment.getComment();
        this.createdAt   = comment.getCreatedAt();
        this.modifiedAt  = comment.getModifiedAt();
    }

//    public CommentResponseDto(Comment comment, List<CommentResponseDto> commentResponseDtoList) {
//        this.id = comment.getId();
//        this.nickname = comment.getNickname();
//        this.comment = comment.getComment();
//        this.children = commentResponseDtoList;
//        this.createdAt = comment.getCreatedAt();
//        this.modifiedAt = comment.getModifiedAt();
//    }
}
