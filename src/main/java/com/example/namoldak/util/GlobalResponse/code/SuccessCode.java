package com.example.namoldak.util.GlobalResponse.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum SuccessCode {
    OK(HttpStatus.OK, "200", "OK"),
    SIGNUP_OK(HttpStatus.OK, "200","회원가입에 성공했습니다."),
    SIGNIN_OK(HttpStatus.OK,"200", "로그인에 성공했습니다."),
    GET_OK(HttpStatus.OK,"200", "조회 성공했습니다."),
    CREATE_OK(HttpStatus.OK,"200", "생성 성공했습니다."),
    MODIFY_OK(HttpStatus.OK,"200", "수정 성공했습니다."),
    DELETE_OK(HttpStatus.OK,"200", "삭제 성공했습니다."),
    LIKE_CHECK(HttpStatus.OK,"200", "좋아요 성공했습니다."),
    AVAILABLE_EMAIL(HttpStatus.OK,"200", "사용 가능한 이메일 입니다."),
    AVAILABLE_NICKNAME(HttpStatus.OK,"200", "사용 가능한 닉네임 입니다."),
    SEND_EMAIL(HttpStatus.OK,"200", "인증 메일이 발송되었습니다."),
    REGISTER_OK(HttpStatus.OK,"200", "가입 완료 되었습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String statusCode;
    private final String statusMsg;

    SuccessCode(HttpStatus httpStatus, String statusCode, String statusMsg) {
        this.httpStatus = httpStatus;
        this.statusCode = statusCode;
        this.statusMsg = statusMsg;
    }
}
