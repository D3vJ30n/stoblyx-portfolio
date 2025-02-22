package com.j30n.stoblyx.domain.model.book.exception;

import com.j30n.stoblyx.common.exception.DomainException;
import com.j30n.stoblyx.common.exception.EntityAlreadyExistsException;
import com.j30n.stoblyx.common.exception.EntityNotFoundException;
import com.j30n.stoblyx.common.exception.UnauthorizedException;
import org.springframework.http.HttpStatus;
import com.j30n.stoblyx.domain.model.book.BookId;

/**
 * 책을 찾을 수 없을 때 발생하는 예외
 */
public class BookNotFoundException extends EntityNotFoundException {
    public BookNotFoundException(Long id) {
        super("책", id);
    }

    public BookNotFoundException(String message) {
        super(message);
    }

    public BookNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
