package com.j30n.stoblyx.adapter.out.persistence.mapper;

import com.j30n.stoblyx.adapter.out.persistence.entity.BookJpaEntity;
import com.j30n.stoblyx.domain.model.book.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class BookMapper {

    private final QuoteMapper quoteMapper;
    private final SummaryMapper summaryMapper;

    public Optional<BookJpaEntity> toJpaEntity(Book book) {
        return Optional.ofNullable(book)
            .map(b -> {
                BookJpaEntity entity = new BookJpaEntity();
                entity.setId(b.getId().value());
                entity.setTitle(b.getTitle().value());
                entity.setAuthor(b.getAuthor().value());
                entity.setGenre(b.getGenre() != null ? b.getGenre().value() : null);
                entity.setPublishedAt(b.getPublishedAt().value());

                // 연관 엔티티 매핑
                if (b.getQuotes() != null) {
                    b.getQuotes().stream()
                        .map(quoteMapper::toJpaEntity)
                        .forEach(entity::addQuote);
                }

                if (b.getSummary() != null) {
                    summaryMapper.toJpaEntity(b.getSummary())
                        .ifPresent(entity::setSummary);
                }

                return entity;
            });
    }

    public Optional<Book> toDomainEntity(BookJpaEntity jpaEntity) {
        return Optional.ofNullable(jpaEntity)
            .map(e -> Book.withId(
                new BookId(e.getId()),
                new Title(e.getTitle()),
                new Author(e.getAuthor()),
                e.getGenre() != null ? new Genre(e.getGenre()) : null,
                new PublishedDate(e.getPublishedAt())
            ));
    }

    public Optional<Book> toDomainEntityWithRelations(BookJpaEntity jpaEntity) {
        return toDomainEntity(jpaEntity)
            .map(book -> {
                // 연관 엔티티 매핑
                if (jpaEntity.getQuotes() != null) {
                    jpaEntity.getQuotes().stream()
                        .map(quoteMapper::toDomainEntity)
                        .forEach(book::addQuote);
                }

                if (jpaEntity.getSummary() != null) {
                    summaryMapper.toDomainEntity(jpaEntity.getSummary())
                        .ifPresent(book::setSummary);
                }

                return book;
            });
    }
}