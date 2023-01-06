package com.example.namoldak.util.GlobalResponse;

import com.example.namoldak.util.GlobalResponse.code.StatusCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

// 기능 : 최종 응답이 리턴
public class ResponseUtil {

    // 메세지 응답 (No Data)
    public static ResponseEntity<?> response(StatusCode statusCode) {

        if (statusCode.getStatusCode().equals("200")) {
            // 성공응답
            return new ResponseEntity<>(new GlobalResponseDto(statusCode), HttpStatus.OK);
        }else{
            // 예외응답
            return new ResponseEntity<>(new GlobalResponseDto(statusCode), statusCode.getHttpStatus());
        }
    }

    // 성공 응답 (Data) - 메세지 없이 오로지 결과값만 반환
    public static <T> ResponseEntity<?> response(T Data) {
        return new ResponseEntity<>(Data, HttpStatus.OK);
    }

}
