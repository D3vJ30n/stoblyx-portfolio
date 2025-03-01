package com.j30n.stoblyx.application.port.in.book;

import com.j30n.stoblyx.adapter.in.web.dto.book.BookCreateRequest;
import com.j30n.stoblyx.adapter.in.web.dto.book.BookDetailResponse;
import com.j30n.stoblyx.adapter.in.web.dto.book.BookResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookUseCase {
    BookResponse createBook(BookCreateRequest request);
    BookResponse getBook(Long id);
    Page<BookResponse> getAllBooks(Pageable pageable);
    Page<BookResponse> searchBooks(String keyword, String category, Pageable pageable);
    BookResponse updateBook(Long id, BookCreateRequest request);
    void deleteBook(Long id);
    boolean existsByIsbn(String isbn);
    BookResponse findByIsbn(String isbn);
    Page<BookResponse> findByGenre(String genre, Pageable pageable);
    BookDetailResponse getBookDetail(Long bookId);
    Page<BookResponse> getBooks(Pageable pageable);
}
