package com.j30n.stoblyx.adapter.in.web.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // 회원 관련 오류
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다"),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 사용중인 이메일입니다"),

    // 문구 관련 오류
    QUOTE_NOT_FOUND(HttpStatus.NOT_FOUND, "문구를 찾을 수 없습니다"),
    INVALID_QUOTE_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 문구 요청입니다"),

    // 댓글 관련 오류
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다"),
    UNAUTHORIZED_COMMENT_ACCESS(HttpStatus.FORBIDDEN, "댓글에 대한 권한이 없습니다");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}