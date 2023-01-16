package com.example.namoldak.dto.RequestDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 기능 : 게임 정답 Dto
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AnswerDto {
    private String answer;
    private String nickname;
}
