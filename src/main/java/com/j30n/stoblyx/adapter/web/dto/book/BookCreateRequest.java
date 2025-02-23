package com.j30n.stoblyx.adapter.web.dto.book;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

public record BookCreateRequest(
    @NotBlank(message = "제목은 필수입니다")
    @Size(max = 255, message = "제목은 255자를 초과할 수 없습니다")
    String title,

    @NotBlank(message = "저자는 필수입니다")
    @Size(max = 255, message = "저자는 255자를 초과할 수 없습니다")
    String author,

    @Size(max = 13, message = "ISBN은 13자를 초과할 수 없습니다")
    String isbn,

    @Size(max = 2000, message = "설명은 2000자를 초과할 수 없습니다")
    String description,

    @Size(max = 255, message = "출판사는 255자를 초과할 수 없습니다")
    String publisher,

    LocalDate publishDate,

    List<String> genres
) {
    @Builder
    public BookCreateRequest {}
} 