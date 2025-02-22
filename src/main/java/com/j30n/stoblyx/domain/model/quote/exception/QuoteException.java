package com.j30n.stoblyx.domain.model.quote.exception;

import com.j30n.stoblyx.common.exception.DomainException;
import org.springframework.http.HttpStatus;

/**
 * 인용구 관련 예외의 기본 클래스
 */
public abstract class QuoteException extends DomainException {
    protected QuoteException(String message, HttpStatus status) {
        super(message, status);
    }

    protected QuoteException(String message, HttpStatus status, Throwable cause) {
        super(message, status, cause);
    }
} 