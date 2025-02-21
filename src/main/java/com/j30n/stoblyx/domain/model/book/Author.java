package com.j30n.stoblyx.domain.model.book;

import java.util.Objects;

// domain/model/book/Author.java
public record Author(String value) {
    public Author {
        Objects.requireNonNull(value, "저자는 null일 수 없습니다");
        if (value.isBlank()) {
            throw new IllegalArgumentException("저자는 비어있을 수 없습니다");
        }
        if (value.length() > 255) {
            throw new IllegalArgumentException("저자는 255자를 초과할 수 없습니다");
        }
    }
}