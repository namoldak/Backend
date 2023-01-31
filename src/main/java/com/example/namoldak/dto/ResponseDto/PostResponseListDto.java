package com.example.namoldak.dto.ResponseDto;

import lombok.Getter;

import java.util.List;

@Getter
public class PostResponseListDto{
    private int totalPage;
    private int postCnt;
    private boolean myPost;
    private List<PostResponseDto> postResponseDtoList;

    public PostResponseListDto(int totalPage, int postCnt, List<PostResponseDto> postResponseDtoList, boolean myPost) {
        this.totalPage           = totalPage;
        this.postCnt             = postCnt;
        this.myPost            = myPost;
        this.postResponseDtoList = postResponseDtoList;
    }

    public PostResponseListDto(int totalPage, int postCnt, List<PostResponseDto> postResponseDtoList) {
        this.totalPage           = totalPage;
        this.postCnt             = postCnt;
        this.postResponseDtoList = postResponseDtoList;
    }

    public PostResponseListDto(int totalPage, List<PostResponseDto> postResponseDtoList) {
        this.totalPage           = totalPage;
        this.postResponseDtoList = postResponseDtoList;
    }
}
