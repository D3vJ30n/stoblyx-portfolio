package com.j30n.stoblyx.domain.model.book.exception;

import com.j30n.stoblyx.common.exception.UnauthorizedException;

/**
 * 책에 대한 권한이 없을 때 발생하는 예외
 */
public class BookNotAuthorizedException extends UnauthorizedException {
    public BookNotAuthorizedException() {
        super("책에 대한 권한이 없습니다");
    }

    public BookNotAuthorizedException(String message) {
        super(message);
    }

    public BookNotAuthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
} 