package com.j30n.stoblyx.domain.model.book;

import java.util.Objects;

// domain/model/book/Title.java
public record Title(String value) {
    public Title {
        Objects.requireNonNull(value, "제목은 null일 수 없습니다");
        if (value.isBlank()) {
            throw new IllegalArgumentException("제목은 비어있을 수 없습니다");
        }
        if (value.length() > 255) {
            throw new IllegalArgumentException("제목은 255자를 초과할 수 없습니다");
        }
    }
}