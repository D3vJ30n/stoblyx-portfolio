package com.j30n.stoblyx.adapter.out.persistence.book;

import com.j30n.stoblyx.application.port.out.book.BookPort;
import com.j30n.stoblyx.domain.model.Book;
import com.j30n.stoblyx.domain.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
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
        List<Book> books = bookRepository.findAllWithGenres();
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), books.size());
        
        List<Book> pageContent = start < end ? books.subList(start, end) : new ArrayList<>();
        return new org.springframework.data.domain.PageImpl<>(pageContent, pageable, books.size());
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

    @Override
    public Page<Book> getHistoryBasedRecommendations(Pageable pageable) {
        // 실제 구현은 사용자의 검색 기록을 분석해야 함
        // 임시로 인기있는 책 반환
        return findPopularBooks(pageable);
    }

    @Override
    public Page<Book> getInterestBasedRecommendations(Pageable pageable) {
        // 실제 구현은 사용자의 관심사를 분석해야 함
        // 임시로 최신 책 반환
        return bookRepository.findByIsDeletedFalseOrderByCreatedAtDesc(pageable);
    }

    @Override
    public Page<Book> getDefaultRecommendations(Pageable pageable) {
        // 기본 추천은 인기도가 높은 책
        return findPopularBooks(pageable);
    }

    @Override
    public Page<Book> findPopularBooks(Pageable pageable) {
        return bookRepository.findByIsDeletedFalseOrderByPopularityDesc(pageable);
    }

    @Override
    public Optional<Book> findBookById(Long bookId) {
        return bookRepository.findByIdAndIsDeletedFalse(bookId);
    }

    @Override
    public List<Book> findAllBooks() {
        return bookRepository.findAllWithGenres();
    }

    /**
     * 디버깅 및 테스트 목적으로 BookRepository 객체에 접근할 수 있는 메서드
     * @return BookRepository 객체
     */
    public BookRepository getBookRepository() {
        return bookRepository;
    }
}
