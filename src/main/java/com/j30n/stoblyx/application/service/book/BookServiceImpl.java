package com.j30n.stoblyx.application.service.book;

import com.j30n.stoblyx.adapter.web.dto.book.BookCreateRequest;
import com.j30n.stoblyx.adapter.web.dto.book.BookResponse;
import com.j30n.stoblyx.domain.model.Book;
import com.j30n.stoblyx.domain.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    @Override
    @Transactional
    public BookResponse createBook(BookCreateRequest request) {
        Book book = Book.builder()
                .title(request.title())
                .author(request.author())
                .isbn(request.isbn())
                .description(request.description())
                .publisher(request.publisher())
                .publishDate(request.publishDate())
                .genres(request.genres())
                .build();

        return BookResponse.from(bookRepository.save(book));
    }

    @Override
    @Transactional(readOnly = true)
    public BookResponse getBook(Long id) {
        return BookResponse.from(findBookById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookResponse> getBooks(String searchKeyword, Pageable pageable) {
        if (searchKeyword == null || searchKeyword.trim().isEmpty()) {
            return bookRepository.findByDeletedFalse(pageable)
                    .map(BookResponse::from);
        }
        return bookRepository.findByTitleOrAuthorContainingIgnoreCase(searchKeyword.trim(), pageable)
                .map(BookResponse::from);
    }

    @Override
    @Transactional
    public BookResponse updateBook(Long id, BookCreateRequest request) {
        Book book = findBookById(id);

        book.update(
                request.title(),
                request.author(),
                request.isbn(),
                request.description(),
                request.publisher(),
                request.publishDate(),
                request.genres()
        );

        return BookResponse.from(book);
    }

    @Override
    @Transactional
    public void deleteBook(Long id) {
        Book book = findBookById(id);
        book.delete();
    }

    private Book findBookById(Long id) {
        return bookRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 책입니다. id: " + id));
    }
} 