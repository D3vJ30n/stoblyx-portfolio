package com.j30n.stoblyx.domain.model.quote.exception;

import com.j30n.stoblyx.common.exception.UnauthorizedException;

/**
 * 인용구에 대한 권한이 없을 때 발생하는 예외
 */
public class QuoteNotAuthorizedException extends UnauthorizedException {
    public QuoteNotAuthorizedException(String operation) {
        super(String.format("인용구에 대한 %s 권한이 없습니다", operation));
    }

    public QuoteNotAuthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
} 