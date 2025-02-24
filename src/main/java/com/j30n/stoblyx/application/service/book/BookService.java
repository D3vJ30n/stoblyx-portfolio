package com.j30n.stoblyx.application.service.book;

import com.j30n.stoblyx.adapter.in.web.dto.book.BookCreateRequest;
import com.j30n.stoblyx.adapter.in.web.dto.book.BookResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookService {
    BookResponse createBook(BookCreateRequest request);

    BookResponse getBook(Long id);

    Page<BookResponse> getBooks(String searchKeyword, Pageable pageable);

    BookResponse updateBook(Long id, BookCreateRequest request);

    void deleteBook(Long id);
} 