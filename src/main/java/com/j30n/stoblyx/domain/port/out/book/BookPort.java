package com.j30n.stoblyx.domain.port.out.book;

import com.j30n.stoblyx.domain.model.book.Book;
import com.j30n.stoblyx.domain.model.book.BookId;

import java.util.List;
import java.util.Optional;

public interface BookPort {
    Optional<Book> findById(BookId id);

    Optional<Book> findByIsbn(String isbn);  // 추가

    List<Book> findAll();

    Book save(Book book);

    boolean existsById(BookId id);

    void deleteById(BookId id);
}