package com.example.namoldak.dto.ResponseDto;

import com.example.namoldak.domain.Comment;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import java.time.LocalDateTime;

// 기능 : 댓글 단건 응답 Dto
@Getter
public class CommentResponseDto {
    private Long id;
    private String nickname;
    private String comment;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime modifiedAt;

    public CommentResponseDto(Comment comment) {
        this.id          = comment.getId();
        this.nickname    = comment.getNickname();
        this.comment     = comment.getComment();
        this.createdAt   = comment.getCreatedAt();
        this.modifiedAt  = comment.getModifiedAt();
    }
}
