package com.j30n.stoblyx.application.usecase.book;

import com.j30n.stoblyx.application.dto.book.BookResponse;
import com.j30n.stoblyx.application.dto.book.RegisterBookCommand;
import com.j30n.stoblyx.application.dto.book.UpdateBookCommand;
import com.j30n.stoblyx.domain.model.book.*;
import com.j30n.stoblyx.domain.port.in.book.DeleteBookUseCase;
import com.j30n.stoblyx.domain.port.in.book.FindBookUseCase;
import com.j30n.stoblyx.domain.port.in.book.RegisterBookUseCase;
import com.j30n.stoblyx.domain.port.in.book.UpdateBookUseCase;
import com.j30n.stoblyx.domain.port.out.book.BookPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService implements RegisterBookUseCase, FindBookUseCase, UpdateBookUseCase, DeleteBookUseCase {

    private final BookPort bookPort;

    @Override
    @Transactional
    public BookResponse registerBook(RegisterBookCommand command) {
        // 1. 값 객체 생성
        Title title = new Title(command.title());
        Author author = new Author(command.author());
        Genre genre = command.genre() != null ? new Genre(command.genre()) : null;
        PublishedDate publishedDate = new PublishedDate(command.publishedAt());

        // 2. 도메인 모델 생성
        Book book = Book.create(title, author, genre, publishedDate);

        // 3. 저장 및 응답 반환
        Book savedBook = bookPort.save(book);
        return mapToBookResponse(savedBook);
    }

    @Override
    @Transactional(readOnly = true)
    public BookResponse findById(BookId id) {
        Book book = bookPort.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("해당 ID의 책을 찾을 수 없습니다: " + id.value()));
        return mapToBookResponse(book);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookResponse> findAll() {
        return bookPort.findAll().stream()
            .map(this::mapToBookResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BookResponse findByIsbn(String isbn) {
        Book book = bookPort.findByIsbn(isbn)
            .orElseThrow(() -> new IllegalArgumentException("해당 ISBN의 책을 찾을 수 없습니다: " + isbn));
        return mapToBookResponse(book);
    }

    @Override
    @Transactional
    public void updateBook(UpdateBookCommand command) {
        // 1. 기존 책 조회
        Book book = bookPort.findById(new BookId(command.id()))
            .orElseThrow(() -> new IllegalArgumentException("해당 ID의 책을 찾을 수 없습니다: " + command.id()));

        // 2. 값 객체 생성
        Title newTitle = new Title(command.title());
        Author newAuthor = new Author(command.author());
        Genre newGenre = command.genre() != null ? new Genre(command.genre()) : null;
        PublishedDate newPublishedDate = new PublishedDate(command.publishedAt());

        // 3. 도메인 모델 업데이트
        book.updateTitle(newTitle);
        book.updateAuthor(newAuthor);
        book.updateGenre(newGenre);
        book.updatePublishedAt(newPublishedDate);

        // 4. 저장
        bookPort.save(book);
    }

    @Override
    @Transactional
    public void deleteBook(BookId id) {
        // 1. 책이 존재하는지 확인
        if (!bookPort.existsById(id)) {
            throw new IllegalArgumentException("해당 ID의 책을 찾을 수 없습니다: " + id.value());
        }

        // 2. 책 삭제
        bookPort.deleteById(id);
    }

    private BookResponse mapToBookResponse(Book book) {
        return new BookResponse(
            book.getId().value(),
            book.getTitle().value(),
            book.getAuthor().value(),
            book.getGenre() != null ? book.getGenre().value() : null,
            book.getPublishedAt().value()
        );
    }
}