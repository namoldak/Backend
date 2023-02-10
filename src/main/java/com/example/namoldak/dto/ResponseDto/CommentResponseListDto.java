package com.example.namoldak.dto.ResponseDto;

import lombok.Getter;
import java.util.List;

// 기능 : 댓글 List 응답 Dto
@Getter
public class CommentResponseListDto {

    private int totalPage;
    private List<CommentResponseDto> commentResponseDtoList;

    public CommentResponseListDto(int totalPage, List<CommentResponseDto> commentResponseDtoList) {
        this.totalPage = totalPage;
        this.commentResponseDtoList = commentResponseDtoList;
    }
}