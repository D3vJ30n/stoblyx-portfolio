package com.j30n.stoblyx.application.service.book;

import com.j30n.stoblyx.adapter.in.web.dto.book.BookCreateRequest;
import com.j30n.stoblyx.adapter.in.web.dto.book.BookDetailResponse;
import com.j30n.stoblyx.adapter.in.web.dto.book.BookResponse;
import com.j30n.stoblyx.application.port.in.book.BookUseCase;
import com.j30n.stoblyx.application.port.out.book.BookPort;
import com.j30n.stoblyx.domain.model.Book;
import com.j30n.stoblyx.domain.model.BookInfo;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookService implements BookUseCase {

    private final BookPort bookPort;
    private static final String BOOK_NOT_FOUND_WITH_ID = "Book not found with id: ";

    @Override
    @Transactional
    public BookResponse createBook(BookCreateRequest request) {
        if (bookPort.existsByIsbn(request.isbn())) {
            throw new IllegalArgumentException("Book with ISBN " + request.isbn() + " already exists");
        }

        BookInfo bookInfo = BookInfo.builder()
            .title(request.title())
            .author(request.author())
            .isbn(request.isbn())
            .description(request.description())
            .publisher(request.publisher())
            .publishDate(request.publishDate())
            .thumbnailUrl(request.thumbnailUrl())
            .genres(request.genres())
            .build();

        Book book = Book.builder()
            .bookInfo(bookInfo)
            .build();

        Book savedBook = bookPort.save(book);
        return BookResponse.from(savedBook);
    }

    @Override
    @Transactional(readOnly = true)
    public BookResponse getBook(Long id) {
        Book book = bookPort.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(BOOK_NOT_FOUND_WITH_ID + id));
        return BookResponse.from(book);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookResponse> getAllBooks(Pageable pageable) {
        return bookPort.findAll(pageable)
            .map(BookResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookResponse> searchBooks(String keyword, String category, Pageable pageable) {
        return bookPort.findByKeywordAndCategory(keyword, category, pageable)
            .map(BookResponse::from);
    }

    @Override
    @Transactional
    public BookResponse updateBook(Long id, BookCreateRequest request) {
        Book book = bookPort.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(BOOK_NOT_FOUND_WITH_ID + id));

        if (!book.getIsbn().equals(request.isbn()) && bookPort.existsByIsbn(request.isbn())) {
            throw new IllegalArgumentException("Book with ISBN " + request.isbn() + " already exists");
        }

        BookInfo bookInfo = BookInfo.builder()
            .title(request.title())
            .author(request.author())
            .isbn(request.isbn())
            .description(request.description())
            .publisher(request.publisher())
            .publishDate(request.publishDate())
            .thumbnailUrl(request.thumbnailUrl())
            .genres(request.genres())
            .build();

        book.update(bookInfo);

        Book updatedBook = bookPort.save(book);
        return BookResponse.from(updatedBook);
    }

    @Override
    @Transactional
    public void deleteBook(Long id) {
        Book book = bookPort.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(BOOK_NOT_FOUND_WITH_ID + id));
        bookPort.delete(book);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByIsbn(String isbn) {
        return bookPort.existsByIsbn(isbn);
    }

    @Override
    @Transactional(readOnly = true)
    public BookResponse findByIsbn(String isbn) {
        Book book = bookPort.findByIsbn(isbn)
            .orElseThrow(() -> new EntityNotFoundException("Book not found with ISBN: " + isbn));
        return BookResponse.from(book);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookResponse> findByGenre(String genre, Pageable pageable) {
        return bookPort.findByGenre(genre, pageable)
            .map(BookResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public BookDetailResponse getBookDetail(Long id) {
        Book book = bookPort.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(BOOK_NOT_FOUND_WITH_ID + id));
        return BookDetailResponse.from(book);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookResponse> getBooks(Pageable pageable) {
        return bookPort.findAll(pageable).map(BookResponse::from);
    }
}