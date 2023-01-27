package com.example.namoldak.dto.ResponseDto;

import com.example.namoldak.domain.Comment;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class CommentResponseListDto {

    private int totalPage;
    private List<CommentResponseDto> commentResponseDtoList;

    public CommentResponseListDto(int totalPage, List<CommentResponseDto> commentResponseDtoList) {
        this.totalPage = totalPage;
        this.commentResponseDtoList = commentResponseDtoList;
    }
}