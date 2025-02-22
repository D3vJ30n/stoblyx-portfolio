package com.j30n.stoblyx.domain.model.book.exception;

import com.j30n.stoblyx.common.exception.DomainException;
import com.j30n.stoblyx.common.exception.EntityAlreadyExistsException;
import com.j30n.stoblyx.common.exception.EntityNotFoundException;
import com.j30n.stoblyx.common.exception.UnauthorizedException;
import org.springframework.http.HttpStatus;

/**
 * 책 관련 예외의 기본 클래스
 */
public abstract class BookException extends DomainException {
    protected BookException(String message, HttpStatus status) {
        super(message, status);
    }

    protected BookException(String message, HttpStatus status, Throwable cause) {
        super(message, status, cause);
    }
}
