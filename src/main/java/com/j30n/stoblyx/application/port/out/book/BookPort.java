package com.j30n.stoblyx.application.port.out.book;

import com.j30n.stoblyx.domain.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface BookPort {
    Book save(Book book);
    Optional<Book> findById(Long id);
    Optional<Book> findByIsbn(String isbn);
    Page<Book> findAll(Pageable pageable);
    Page<Book> findByKeywordAndCategory(String keyword, String category, Pageable pageable);
    void delete(Book book);
    boolean existsByIsbn(String isbn);
    Page<Book> findByGenre(String genre, Pageable pageable);
}
