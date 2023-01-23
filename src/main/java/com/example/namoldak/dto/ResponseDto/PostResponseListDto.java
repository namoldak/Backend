package com.example.namoldak.dto.ResponseDto;

import lombok.Getter;

import java.util.List;

@Getter
public class PostResponseListDto{
    private int totalPage;
    private List<PostResponseDto> postResponseDtoList;

    public PostResponseListDto(int totalPage, List<PostResponseDto> postResponseDtoList) {
        this.totalPage           = totalPage;
        this.postResponseDtoList = postResponseDtoList;
    }
}
