package com.mysite.sbb.aitrip.global.response;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // 인증 (A)
    INVALID_TOKEN("A001", HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN("A002", HttpStatus.UNAUTHORIZED, "만료된 토큰입니다."),
    UNSUPPORTED_TOKEN("A003", HttpStatus.UNAUTHORIZED, "지원하지 않는 토큰 형식입니다."),
    EMPTY_TOKEN("A004", HttpStatus.UNAUTHORIZED, "토큰이 비어있습니다."),
    UNAUTHORIZED("A005", HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
    BLACKLISTED_TOKEN("A006", HttpStatus.UNAUTHORIZED, "로그아웃된 토큰입니다."),
    INVALID_REFRESH_TOKEN("A007", HttpStatus.UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다."),
    INVALID_CREDENTIALS("A008", HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다."),

    // 사용자 (U)
    DUPLICATE_EMAIL("U001", HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다."),
    USER_NOT_FOUND("U002", HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."),
    INVALID_PASSWORD("U003", HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다."),

    // 여행 (T)
    NOT_FOUND_TRIP("T001", HttpStatus.NOT_FOUND, "존재하지 않는 여행입니다."),
    UNAUTHORIZED_TRIP_ACCESS("T002", HttpStatus.FORBIDDEN, "해당 여행에 대한 권한이 없습니다."),

    // 장소 (P)
    NOT_FOUND_PLACE("P001", HttpStatus.NOT_FOUND, "존재하지 않는 장소입니다."),

    // 장소 스타일 태그 (PS)
    NOT_FOUND_PLACE_STYLE_TAG("PS001", HttpStatus.NOT_FOUND, "존재하지 않는 장소 스타일 태그입니다."),

    // 장소 이동시간 (PM)
    NOT_FOUND_PLACE_MOVING_TIME("PM001", HttpStatus.NOT_FOUND, "존재하지 않는 이동시간 정보입니다."),

    // 장소 혼잡도 (PC)
    NOT_FOUND_PLACE_CROWD_DATA("PC001", HttpStatus.NOT_FOUND, "존재하지 않는 혼잡도 정보입니다."),

    // 여행 장소 (TP)
    NOT_FOUND_TRIP_PLACE("TP001", HttpStatus.NOT_FOUND, "존재하지 않는 여행 장소입니다."),
    DUPLICATE_TRIP_PLACE("TP002", HttpStatus.CONFLICT, "이미 여행에 추가된 장소입니다."),

    // 일정 (S)
    NOT_FOUND_SCHEDULE("S001", HttpStatus.NOT_FOUND, "존재하지 않는 일정입니다.");

    private final String code;
    private final HttpStatus status;
    private final String message;

    ErrorCode(String code, HttpStatus status, String message) {
        this.code = code;
        this.status = status;
        this.message = message;
    }
}
