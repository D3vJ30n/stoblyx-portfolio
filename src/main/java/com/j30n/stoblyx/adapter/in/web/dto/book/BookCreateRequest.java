package com.j30n.stoblyx.adapter.in.web.dto.book;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public record BookCreateRequest(
    @NotBlank(message = "제목은 필수입니다")
    @Size(min = 1, max = 255, message = "제목은 1자 이상 255자 이하여야 합니다")
    String title,

    @NotBlank(message = "저자는 필수입니다")
    @Size(min = 1, max = 255, message = "저자는 1자 이상 255자 이하여야 합니다")
    String author,

    @Pattern(regexp = "^(?:(?:\\d{3}[-])?\\d{1,5}[-]\\d{1,7}[-]\\d{1,7}[-]\\d{1,7}|\\d{13})$", 
            message = "올바른 ISBN 형식이 아닙니다 (예: 979-11-92001-11-5 또는 9791192001115)")
    String isbn,

    @Size(max = 2000, message = "설명은 2000자를 초과할 수 없습니다")
    String description,

    @Size(max = 255, message = "출판사는 255자를 초과할 수 없습니다")
    String publisher,

    @NotNull(message = "출판일은 필수입니다")
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate publishDate,

    @Size(max = 255, message = "썸네일 URL은 255자를 초과할 수 없습니다")
    String thumbnailUrl,

    List<@Size(max = 50, message = "장르는 50자를 초과할 수 없습니다") String> genres
) {
    @JsonCreator
    public BookCreateRequest(
        @JsonProperty("title") String title,
        @JsonProperty("author") String author,
        @JsonProperty("isbn") String isbn,
        @JsonProperty("description") String description,
        @JsonProperty("publisher") String publisher,
        @JsonProperty("publishDate") LocalDate publishDate,
        @JsonProperty("thumbnailUrl") String thumbnailUrl,
        @JsonProperty("genres") List<String> genres
    ) {
        this.title = title != null ? title.trim() : null;
        this.author = author != null ? author.trim() : null;
        this.isbn = isbn != null ? isbn.trim().replaceAll("\\s+", "") : null;
        this.description = description != null ? description.trim() : null;
        this.publisher = publisher != null ? publisher.trim() : null;
        this.publishDate = publishDate;
        this.thumbnailUrl = thumbnailUrl != null ? thumbnailUrl.trim() : null;
        this.genres = genres != null ? genres.stream()
            .filter(genre -> genre != null && !genre.trim().isEmpty())
            .map(String::trim)
            .toList() : List.of();
    }
}