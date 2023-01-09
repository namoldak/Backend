package com.example.namoldak.dto.RequestDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 기능 : 포스트 업로드에 필요한 데이터를 담을 Dto
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostRequestDto {
    private String title;
    private String content;
    private String category;
}
