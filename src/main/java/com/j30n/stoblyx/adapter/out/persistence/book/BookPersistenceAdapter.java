package com.j30n.stoblyx.adapter.out.persistence.book;

import com.j30n.stoblyx.application.port.out.book.BookPort;
import com.j30n.stoblyx.domain.model.Book;
import com.j30n.stoblyx.domain.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 책 영속성 어댑터
 * 외부 데이터베이스에서 책 정보를 관리합니다.
 */
@Component
@RequiredArgsConstructor
public class BookPersistenceAdapter implements BookPort {

    private final BookRepository bookRepository;

    @Override
    public Book save(Book book) {
        return bookRepository.save(book);
    }

    @Override
    public Optional<Book> findById(Long id) {
        return bookRepository.findByIdAndIsDeletedFalse(id);
    }

    @Override
    public Optional<Book> findByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn);
    }

    @Override
    public Page<Book> findAll(Pageable pageable) {
        return bookRepository.findByIsDeletedFalse(pageable);
    }

    @Override
    public Page<Book> findByKeywordAndCategory(String keyword, String category, Pageable pageable) {
        return bookRepository.findByKeywordAndCategory(keyword, category, pageable);
    }

    @Override
    public void delete(Book book) {
        book.delete();
        bookRepository.save(book);
    }

    @Override
    public boolean existsByIsbn(String isbn) {
        return bookRepository.existsByIsbn(isbn);
    }

    @Override
    public Page<Book> findByGenre(String genre, Pageable pageable) {
        return bookRepository.findByGenresContainingAndIsDeletedFalse(genre, pageable);
    }
}
