package com.example.namoldak.dto.ResponseDto;

import com.example.namoldak.util.GlobalResponse.code.StatusCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 기능 : 데이터 및 상태 코드, 메세지 반환
@Getter
@Setter
@NoArgsConstructor
public class PrivateResponseBody<T> {
    private String statusCode;
    private String statusMsg;
    private T data;

    public PrivateResponseBody(StatusCode statusCode, T data){
        this.statusCode = statusCode.getStatusCode();
        this.statusMsg  = statusCode.getStatusMsg();
        this.data       = data;
    }
}
