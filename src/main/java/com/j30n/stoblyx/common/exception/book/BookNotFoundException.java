package com.j30n.stoblyx.common.exception.book;

import com.j30n.stoblyx.common.exception.domain.DomainException;

public class BookNotFoundException extends DomainException {
    public BookNotFoundException(Long id) {
        super(String.format("Book not found with id: %d", id));
    }

    public BookNotFoundException(String isbn) {
        super(String.format("Book not found with ISBN: %s", isbn));
    }
}