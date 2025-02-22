package com.j30n.stoblyx.domain.model.quote.exception;

import org.springframework.http.HttpStatus;

/**
 * 삭제된 인용구에 대한 작업을 시도할 때 발생하는 예외
 */
public class QuoteDeletedException extends QuoteException {
    public QuoteDeletedException(Long id) {
        super(
            String.format("이미 삭제된 인용구입니다 (ID: %d)", id),
            HttpStatus.BAD_REQUEST
        );
    }

    public QuoteDeletedException(String message, Throwable cause) {
        super(message, HttpStatus.BAD_REQUEST, cause);
    }
} 