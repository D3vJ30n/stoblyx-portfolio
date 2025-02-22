package com.j30n.stoblyx.domain.model.user.exception;

import com.j30n.stoblyx.common.exception.DomainException;
import org.springframework.http.HttpStatus;

/**
 * 사용자 관련 예외의 기본 클래스
 */
public abstract class UserException extends DomainException {
    protected UserException(String message, HttpStatus status) {
        super(message, status);
    }

    protected UserException(String message, HttpStatus status, Throwable cause) {
        super(message, status, cause);
    }
} 