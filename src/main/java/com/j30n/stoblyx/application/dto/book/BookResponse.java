package com.j30n.stoblyx.application.dto.book;

import java.time.LocalDate;
import java.util.Objects;

public record BookResponse(
    Long id,
    String title,
    String author,
    String genre,
    LocalDate publishedAt
) {
    public BookResponse {
        Objects.requireNonNull(id, "ID는 null일 수 없습니다");
        Objects.requireNonNull(title, "제목은 null일 수 없습니다");
        Objects.requireNonNull(author, "저자는 null일 수 없습니다");
        Objects.requireNonNull(publishedAt, "출판일은 null일 수 없습니다");
    }
}