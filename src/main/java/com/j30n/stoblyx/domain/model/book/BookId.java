package com.j30n.stoblyx.domain.model.book;

import lombok.Value;

@Value
public class BookId {
    Long value;

    public BookId(Long value) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("Book ID must be a positive number");
        }
        this.value = value;
    }

    public Long value() {
        return 0L;
    }
}