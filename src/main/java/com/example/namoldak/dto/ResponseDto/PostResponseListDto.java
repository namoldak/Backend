package com.example.namoldak.dto.ResponseDto;

import lombok.Getter;
import java.util.List;

// 기능 : 포스트 List 반환 Dto
@Getter
public class PostResponseListDto{
    private int totalPage;
    private int postCnt;
    private List<PostResponseDto> postResponseDtoList;

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
