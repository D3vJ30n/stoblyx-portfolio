package com.j30n.stoblyx.domain.model.comment.exception;

import com.j30n.stoblyx.common.exception.DomainException;
import org.springframework.http.HttpStatus;

/**
 * 댓글 관련 예외의 기본 클래스
 */
public abstract class CommentException extends DomainException {
    protected CommentException(String message, HttpStatus status) {
        super(message, status);
    }

    protected CommentException(String message, HttpStatus status, Throwable cause) {
        super(message, status, cause);
    }
} 