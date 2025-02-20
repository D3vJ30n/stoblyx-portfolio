package com.j30n.stoblyx.adapter.out.persistence.adapter;

import com.j30n.stoblyx.adapter.out.persistence.mapper.BookMapper;
import com.j30n.stoblyx.adapter.out.persistence.repository.BookRepository;
import com.j30n.stoblyx.domain.model.book.Book;
import com.j30n.stoblyx.domain.model.book.BookId;
import com.j30n.stoblyx.domain.port.out.book.BookPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BookPersistenceAdapter implements BookPort {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    @Override
    public Book save(Book book) {
        return bookMapper.toJpaEntity(book)
            .map(bookRepository::save)
            .flatMap(bookMapper::toDomainEntityWithRelations)
            .orElseThrow(() -> new IllegalArgumentException("Book cannot be null"));
    }

    @Override
    public Optional<Book> findById(BookId id) {
        return bookRepository.findById(id.value())
            .flatMap(bookMapper::toDomainEntityWithRelations);
    }

    @Override
    public Optional<Book> findByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn)
            .flatMap(bookMapper::toDomainEntity);
    }

    @Override
    public List<Book> findAll() {
        return bookRepository.findAll().stream()
            .map(bookMapper::toDomainEntityWithRelations)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());
    }

    @Override
    public boolean existsById(BookId id) {
        return bookRepository.existsById(id.value());
    }

    @Override
    public void deleteById(BookId id) {
        bookRepository.deleteById(id.value());
    }
}