package com.j30n.stoblyx.adapter.out.persistence;

import org.springframework.stereotype.Component;
import com.j30n.stoblyx.domain.model.book.Book;

@Component
public class BookMapper {
    public BookJpaEntity toJpaEntity(Book book) {
        if (book == null) return null;
        
        BookJpaEntity entity = new BookJpaEntity();
        entity.setId(book.getId());
        entity.setTitle(book.getTitle());
        entity.setAuthor(book.getAuthor());
        entity.setGenre(book.getGenre());
        entity.setPublishedAt(book.getPublishedAt());
        return entity;
    }

    public Book toDomainEntity(BookJpaEntity jpaEntity) {
        if (jpaEntity == null) return null;
        
        return Book.builder()
                .id(jpaEntity.getId())
                .title(jpaEntity.getTitle())
                .author(jpaEntity.getAuthor())
                .genre(jpaEntity.getGenre())
                .publishedAt(jpaEntity.getPublishedAt())
                .build();
    }
}