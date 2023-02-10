package com.example.namoldak.util.GlobalResponse.code;

import lombok.Getter;
import org.springframework.http.HttpStatus;

// 기능 : 응답용 메세지 커스텀
@Getter
public enum StatusCode {

    //TODO ========================= 예외 응답 코드 ===============================
    // 400 BAD_REQUEST : 잘못된 요청
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "400", "요청이 올바르지 않습니다"),
    BAD_REQUEST_TOKEN(HttpStatus.UNAUTHORIZED, "401", "토큰이 유효하지 않습니다."),
    EXIST_EMAIL(HttpStatus.BAD_REQUEST, "400", "중복된 이메일이 존재합니다."),
    BAD_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "400", "refreshtoken 삭제 권한이 없습니다."),
    EXIST_NICKNAME(HttpStatus.BAD_REQUEST, "400", "중복된 닉네임이 존재합니다."),
    LOGIN_MATCH_FAIL(HttpStatus.BAD_REQUEST, "400", "회원을 찾을 수 없습니다."),
    INVALID_ID_PASSWORD(HttpStatus.BAD_REQUEST, "400", "아이디나 비밀번호의 구성이 알맞지 않습니다"),
    BAD_PASSWORD(HttpStatus.BAD_REQUEST, "400", "비밀번호가 일치하지 않습니다"),
    INVALID_TOKEN(HttpStatus.BAD_REQUEST, "403", "로그인 후 사용이 가능합니다"),
    NO_AUTH_MEMBER(HttpStatus.BAD_REQUEST, "403", "작성자 정보와 일치하지 않습니다."),
    NOT_FOUND_MEMBER(HttpStatus.NOT_FOUND, "110", "회원을 찾을 수 없습니다."),
    LOGIN_WRONG_SIGNATURE_JWT_TOKEN(HttpStatus.BAD_REQUEST, "112", "잘못된 JWT 서명입니다."),
    LOGIN_EXPIRED_JWT_TOKEN(HttpStatus.UNAUTHORIZED, "401", "만료된 JWT 토큰입니다."),
    LOGIN_NOT_SUPPORTED_JWT_TOKEN(HttpStatus.BAD_REQUEST, "114", "지원되지 않는 JWT 토큰입니다."),
    LOGIN_WRONG_FORM_JWT_TOKEN(HttpStatus.BAD_REQUEST, "115", "JWT 토큰이 잘못되었습니다."),
    CANT_ENTER(HttpStatus.BAD_REQUEST, "122", "정원이 다 차있닭!!"),
    MEMBER_DUPLICATED(HttpStatus.BAD_REQUEST, "123", "이미 입장해있닭!!"),
    UNAUTHORIZE(HttpStatus.UNAUTHORIZED, "124", "방장만이 게임 시작을 진행할 수 있습니다."),
    ALREADY_PLAYING(HttpStatus.BAD_REQUEST, "125", "게임이 시작해서 못 들어간닭!!"),
    NOT_ENOUGH_MEMBER(HttpStatus.BAD_REQUEST, "130", "게임 시작하기에 충분한 유저가 모이지 않았습니다."),
    NOT_EXIST_ROOMS(HttpStatus.BAD_REQUEST, "132", "조건에 맞는 방이 존재하지 않습니다."),
    SIGNATURE_EXCEPTION(HttpStatus.BAD_REQUEST, "134", "JWT 서명에 문제가 발생했습니다."),
    JWT_EXCEPTION(HttpStatus.BAD_REQUEST, "135", "JWT 예외 응답 처리에 오류가 발생했습니다."),
    JSON_PROCESS_FAILED(HttpStatus.BAD_REQUEST, "136", "JSON 처리에서 오류가 발생했습니다."),
    NOT_FOUND_REFRESHTOKEN(HttpStatus.BAD_REQUEST, "137", "Refresh Token을 찾을 수 없습니다."),
    NOT_FOUND_ATTENDEE(HttpStatus.NOT_FOUND, "404", "게임 참가자를 찾을 수 없습니다."),
    GAME_SET_NOT_FOUND(HttpStatus.NOT_FOUND, "404", "게임 스타트 셋을 찾을 수 없습니다."),
    SESSION_ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "404", "해당 게임룸 세션 정보가 없습니다."),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "404", "댓글이 존재하지 않습니다."),
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "404", "해당 게시글이 없습니다."),
    KEYWORD_NOT_FOUND(HttpStatus.NOT_FOUND, "404", "검색 결과가 없습니다."),
    FILE_DELETE_FAILED(HttpStatus.NOT_FOUND, "404", "파일 삭제 실패"),
    FILE_CONVERT_FAILED(HttpStatus.NOT_FOUND, "404", "파일 전환 실패"),

    //TODO ========================= 성공 응답 코드 ===============================

    OK(HttpStatus.OK, "200", "응답이 정상 처리 되었습니다."),
    LOGIN_OK(HttpStatus.OK, "200", "로그인 되셨습니다!"),
    LOGOUT_OK(HttpStatus.OK, "200", "로그아웃 되셨습니다!"),
    SIGNUP_OK(HttpStatus.OK, "200", "회원가입에 성공했습니다."),
    SIGNIN_OK(HttpStatus.OK, "200", "로그인에 성공했습니다."),
    GET_OK(HttpStatus.OK, "200", "조회 성공했습니다."),
    CREATE_OK(HttpStatus.OK, "200", "생성 성공했습니다."),
    CREATE_ROOM(HttpStatus.OK, "200", "게임방을 생성했습니닭!"),
    MODIFY_OK(HttpStatus.OK, "200", "수정 성공했습니다."),
    DELETE_OK(HttpStatus.OK, "200", "삭제 성공했습니다."),
    LIKE_CHECK(HttpStatus.OK, "200", "좋아요 성공했습니다."),
    AVAILABLE_EMAIL(HttpStatus.OK, "200", "사용 가능한 이메일 입니다."),
    AVAILABLE_NICKNAME(HttpStatus.OK, "200", "사용 가능한 닉네임 입니다."),
    SEND_EMAIL(HttpStatus.OK, "200", "인증 메일이 발송되었습니다."),
    REGISTER_OK(HttpStatus.OK, "200", "가입 완료 되었습니다."),
    EXIT_SUCCESS(HttpStatus.OK, "200", "방을 나가셨습니닭!"),
    ENTER_OK(HttpStatus.OK, "200", "방에 입장하셨습니닭!"),
    DELETE_MEMBER_OK(HttpStatus.OK, "200", "회원 탈퇴 성공했습니다."),
    GAME_START(HttpStatus.OK, "200", "게임 시작!"),
    ;
    private final HttpStatus httpStatus;
    private final String statusCode;
    private final String statusMsg;

    StatusCode(HttpStatus httpStatus, String statusCode, String statusMsg) {
        this.httpStatus = httpStatus;
        this.statusCode = statusCode;
        this.statusMsg = statusMsg;
    }
}