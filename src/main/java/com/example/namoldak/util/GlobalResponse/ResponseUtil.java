package com.example.namoldak.util.GlobalResponse;

import com.example.namoldak.util.GlobalResponse.code.StatusCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

// 1. 기능 : 최종 응답이 리턴
// 2. 작성자 : 조소영
public class ResponseUtil {

    // 성공 응답 (No Data)
    public static ResponseEntity<?> response(StatusCode statusCode) {
        if (statusCode.getStatusCode().equals("200")) {
            return new ResponseEntity<>(new GlobalResponseDto(statusCode), HttpStatus.OK);
        }else{
            return new ResponseEntity<>(new GlobalResponseDto(statusCode), statusCode.getHttpStatus());
        }
    }

    // 성공 응답 (Data) - 오로지 객체값만 반환
    public static <T> ResponseEntity<?> response(T Data) {
        return new ResponseEntity<>(Data, HttpStatus.OK);
    }

}
