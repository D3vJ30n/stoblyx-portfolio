package com.j30n.stoblyx.domain.port.out.book;

import com.j30n.stoblyx.domain.model.book.Book;
import com.j30n.stoblyx.domain.model.book.BookId;

import java.util.List;
import java.util.Optional;

public interface BookPort {
    Book save(Book book);

    Optional<Book> findById(BookId id);

    List<Book> findAll();

    void deleteById(BookId id);

    boolean existsById(BookId id);
}