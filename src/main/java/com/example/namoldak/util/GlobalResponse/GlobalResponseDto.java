package com.example.namoldak.util.GlobalResponse;

import com.example.namoldak.util.GlobalResponse.code.ErrorCode;
import com.example.namoldak.util.GlobalResponse.code.SuccessCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

// 1. 기능 : 응답으로 메세지가 필요할 경우를 위한 Dto
// 2. 작성자 : 조소영
@Getter
public class GlobalResponseDto {
    private HttpStatus httpStatus;
    private String statusCode;
    private String statusMsg;

    public GlobalResponseDto(SuccessCode successCode){
        this.httpStatus = successCode.getHttpStatus();
        this.statusCode = successCode.getStatusCode();
        this.statusMsg = successCode.getStatusMsg();
    }

    public GlobalResponseDto(ErrorCode errorCode){
        this.httpStatus = errorCode.getHttpStatus();
        this.statusCode = errorCode.getStatusCode();
        this.statusMsg = errorCode.getStatusMsg();
    }
}
