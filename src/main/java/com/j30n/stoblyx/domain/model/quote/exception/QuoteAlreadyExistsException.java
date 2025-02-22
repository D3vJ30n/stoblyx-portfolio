package com.j30n.stoblyx.domain.model.quote.exception;

import com.j30n.stoblyx.common.exception.EntityAlreadyExistsException;

/**
 * 이미 존재하는 인용구를 생성하려고 할 때 발생하는 예외
 */
public class QuoteAlreadyExistsException extends EntityAlreadyExistsException {
    public QuoteAlreadyExistsException(Long bookId, String content) {
        super(String.format("이미 동일한 내용의 인용구가 존재합니다 (책 ID: %d)", bookId));
    }

    public QuoteAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
} 