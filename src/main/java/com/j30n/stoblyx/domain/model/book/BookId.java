package com.j30n.stoblyx.domain.model.book;

import java.util.Objects;

// domain/model/book/BookId.java
public record BookId(Long value) {
    public BookId {
        Objects.requireNonNull(value, "Book ID는 null일 수 없습니다");
    }
}