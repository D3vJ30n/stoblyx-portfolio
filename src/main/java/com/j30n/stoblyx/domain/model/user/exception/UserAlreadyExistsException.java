package com.j30n.stoblyx.domain.model.user.exception;

import org.springframework.http.HttpStatus;

/**
 * 이미 존재하는 사용자를 생성하려 할 때 발생하는 예외
 */
public class UserAlreadyExistsException extends UserException {
    public UserAlreadyExistsException(String email) {
        super(String.format("이미 존재하는 이메일입니다: %s", email), HttpStatus.CONFLICT);
    }

    public UserAlreadyExistsException(String message, Throwable cause) {
        super(message, HttpStatus.CONFLICT, cause);
    }
} 