package com.example.namoldak.dto.ResponseDto;

import lombok.Getter;

@Getter
public class ResponseDto {
    private int statuscode;
    private String message;

    public ResponseDto(int statuscode, String message) {
        this.statuscode = statuscode;
        this.message = message;
    }
}