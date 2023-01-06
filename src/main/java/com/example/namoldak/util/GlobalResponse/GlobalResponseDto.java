package com.example.namoldak.util.GlobalResponse;

import com.example.namoldak.util.GlobalResponse.code.StatusCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

// 기능 : 응답으로 메세지가 필요할 경우를 위한 Dto
@Getter
public class GlobalResponseDto {
    private HttpStatus httpStatus;
    private String statusCode;
    private String statusMsg;

    public GlobalResponseDto(StatusCode statusCode){
        this.httpStatus = statusCode.getHttpStatus();
        this.statusCode = statusCode.getStatusCode();
        this.statusMsg = statusCode.getStatusMsg();
    }
}
