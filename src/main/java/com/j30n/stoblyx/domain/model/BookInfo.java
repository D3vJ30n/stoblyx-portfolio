package com.j30n.stoblyx.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

/**
 * 책의 기본 정보를 담는 값 객체
 */
@Getter
@Builder
public class BookInfo {
    private final String title;
    private final String author;
    private final String isbn;
    private final String description;
    private final String publisher;
    private final LocalDate publishDate;
    private final String thumbnailUrl;
    private final List<String> genres;
} 