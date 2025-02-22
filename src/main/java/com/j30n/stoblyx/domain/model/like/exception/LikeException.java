package com.j30n.stoblyx.domain.model.like.exception;

import com.j30n.stoblyx.common.exception.DomainException;
import org.springframework.http.HttpStatus;

/**
 * 좋아요 관련 예외의 기본 클래스
 */
public abstract class LikeException extends DomainException {
    protected LikeException(String message, HttpStatus status) {
        super(message, status);
    }

    protected LikeException(String message, HttpStatus status, Throwable cause) {
        super(message, status, cause);
    }
} 