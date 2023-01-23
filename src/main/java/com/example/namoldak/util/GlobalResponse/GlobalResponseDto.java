package com.example.namoldak.util.GlobalResponse;

import com.example.namoldak.util.GlobalResponse.code.StatusCode;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import org.springframework.http.HttpStatus;

// 기능 : 응답으로 메세지가 필요할 경우를 위한 Dto
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GlobalResponseDto<T> {
    private HttpStatus httpStatus;
    private String statusCode;
    private String statusMsg;
    private T data;

    public GlobalResponseDto(StatusCode statusCode) {
        this.httpStatus = statusCode.getHttpStatus();
        this.statusCode = statusCode.getStatusCode();
        this.statusMsg  = statusCode.getStatusMsg();
    }

    public GlobalResponseDto(StatusCode statusCode, T data){
        this.httpStatus = statusCode.getHttpStatus();
        this.statusCode = statusCode.getStatusCode();
        this.statusMsg  = statusCode.getStatusMsg();
        this.data       = data;
    }
}
