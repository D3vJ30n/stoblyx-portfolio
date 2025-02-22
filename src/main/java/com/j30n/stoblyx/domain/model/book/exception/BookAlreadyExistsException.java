package com.j30n.stoblyx.domain.model.book.exception;

import com.j30n.stoblyx.common.exception.EntityAlreadyExistsException;

/**
 * 이미 존재하는 책을 생성하려고 할 때 발생하는 예외
 */
public class BookAlreadyExistsException extends EntityAlreadyExistsException {
    public BookAlreadyExistsException(String isbn) {
        super("이미 존재하는 ISBN입니다: " + isbn);
    }

    public BookAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
} 