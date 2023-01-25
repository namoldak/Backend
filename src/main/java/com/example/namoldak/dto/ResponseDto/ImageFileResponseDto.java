package com.example.namoldak.dto.ResponseDto;

import com.example.namoldak.domain.ImageFile;
import lombok.Getter;

@Getter
public class ImageFileResponseDto {
    private String path;

    public ImageFileResponseDto(ImageFile imageFile) {
        this.path = imageFile.getPath();
    }
}