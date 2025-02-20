package com.j30n.stoblyx.domain.model.book;

import com.j30n.stoblyx.domain.model.quote.Quote;
import com.j30n.stoblyx.domain.model.summary.Summary;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Getter
public class Book {
    private final BookId id;
    private final List<Quote> quotes = new ArrayList<>();
    private Title title;
    private Author author;
    private Genre genre;
    private PublishedDate publishedAt;
    private Summary summary;

    private Book(BookId id, Title title, Author author, Genre genre, PublishedDate publishedAt) {
        this.id = id;
        this.title = Objects.requireNonNull(title, "제목은 null일 수 없습니다");
        this.author = Objects.requireNonNull(author, "저자는 null일 수 없습니다");
        this.genre = genre;
        this.publishedAt = Objects.requireNonNull(publishedAt, "출판일은 null일 수 없습니다");
    }

    public static Book create(Title title, Author author, Genre genre, PublishedDate publishedAt) {
        return new Book(null, title, author, genre, publishedAt);
    }

    public static Book withId(BookId id, Title title, Author author, Genre genre, PublishedDate publishedAt) {
        return new Book(id, title, author, genre, publishedAt);
    }

    public void addQuote(Quote quote) {
        Objects.requireNonNull(quote, "인용구는 null일 수 없습니다");
        quotes.add(quote);
    }

    public void setSummary(Summary summary) {
        this.summary = Objects.requireNonNull(summary, "요약은 null일 수 없습니다");
    }

    public List<Quote> getQuotes() {
        return Collections.unmodifiableList(quotes);
    }

    public void updateTitle(Title title) {
        this.title = Objects.requireNonNull(title, "제목은 null일 수 없습니다");
    }

    public void updateAuthor(Author author) {
        this.author = Objects.requireNonNull(author, "저자는 null일 수 없습니다");
    }

    public void updateGenre(Genre genre) {
        this.genre = genre;
    }

    public void updatePublishedAt(PublishedDate publishedAt) {
        this.publishedAt = Objects.requireNonNull(publishedAt, "출판일은 null일 수 없습니다");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Book book)) return false;
        return Objects.equals(id, book.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}