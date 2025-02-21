package com.j30n.stoblyx.domain.model.book;

// domain/model/book/Genre.java
public record Genre(String value) {
    public Genre {
        if (value != null && value.length() > 100) {
            throw new IllegalArgumentException("장르는 100자를 초과할 수 없습니다");
        }
    }
}