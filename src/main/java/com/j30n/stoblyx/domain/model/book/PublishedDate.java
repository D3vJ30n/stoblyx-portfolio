package com.j30n.stoblyx.domain.model.book;

import java.time.LocalDate;
import java.util.Objects;

// domain/model/book/PublishedDate.java
public record PublishedDate(LocalDate value) {
    public PublishedDate {
        Objects.requireNonNull(value, "출판일은 null일 수 없습니다");
        if (value.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("출판일은 미래일 수 없습니다");
        }
    }
}