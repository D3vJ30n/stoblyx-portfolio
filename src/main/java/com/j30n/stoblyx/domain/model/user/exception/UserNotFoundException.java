package com.j30n.stoblyx.domain.model.user.exception;

import org.springframework.http.HttpStatus;

/**
 * 사용자를 찾을 수 없을 때 발생하는 예외
 */
public class UserNotFoundException extends UserException {
    public UserNotFoundException(Long id) {
        super(String.format("사용자를 찾을 수 없습니다. (ID: %d)", id), HttpStatus.NOT_FOUND);
    }

    public UserNotFoundException(String email) {
        super(String.format("사용자를 찾을 수 없습니다. (이메일: %s)", email), HttpStatus.NOT_FOUND);
    }

    public UserNotFoundException(String message, Throwable cause) {
        super(message, HttpStatus.NOT_FOUND, cause);
    }
} 