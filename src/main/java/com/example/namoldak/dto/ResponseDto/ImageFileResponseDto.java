package com.example.namoldak.dto.ResponseDto;

import com.example.namoldak.domain.ImageFile;
import lombok.Getter;

// 기능 : 이미지 파일 경로를 반환하는 Dto
@Getter
public class ImageFileResponseDto {
    private String path;

    public ImageFileResponseDto(ImageFile imageFile) {
        this.path = imageFile.getPath();
    }
}