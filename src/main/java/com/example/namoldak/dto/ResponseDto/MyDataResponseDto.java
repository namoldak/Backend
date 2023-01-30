package com.example.namoldak.dto.ResponseDto;

import com.example.namoldak.domain.Member;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MyDataResponseDto {
    private String nickname;
    private String email;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;

    public MyDataResponseDto(Member member) {
        this.nickname = member.getNickname();
        this.email = member.getEmail();
        this.createdAt = member.getCreatedAt();
    }
}
