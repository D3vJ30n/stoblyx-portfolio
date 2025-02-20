package com.j30n.stoblyx.application.dto.book;

import java.time.LocalDate;
import java.util.Objects;

public record RegisterBookCommand(
    String title,
    String author,
    String genre,
    LocalDate publishedAt
) {
    public RegisterBookCommand {
        Objects.requireNonNull(title, "제목은 필수입니다");
        Objects.requireNonNull(author, "저자는 필수입니다");
        Objects.requireNonNull(publishedAt, "출판일은 필수입니다");
    }
}