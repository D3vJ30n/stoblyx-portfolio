package com.j30n.stoblyx.domain.model.user.exception;

import org.springframework.http.HttpStatus;

/**
 * 사용자가 권한이 없을 때 발생하는 예외
 */
public class UserNotAuthorizedException extends UserException {
    public UserNotAuthorizedException(String operation) {
        super(String.format("해당 작업에 대한 권한이 없습니다: %s", operation), HttpStatus.FORBIDDEN);
    }

    public UserNotAuthorizedException(String message, Throwable cause) {
        super(message, HttpStatus.FORBIDDEN, cause);
    }
} 