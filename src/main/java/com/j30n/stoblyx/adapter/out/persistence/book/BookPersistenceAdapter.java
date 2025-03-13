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

    @Override
    public Page<Book> getHistoryBasedRecommendations(Pageable pageable) {
        // 실제 구현에서는 사용자의 검색 기록을 분석하여 추천 책을 제공
        // 테스트 목적으로 임시 구현
        return bookRepository.findByIsDeletedFalse(pageable);
    }

    @Override
    public Page<Book> getInterestBasedRecommendations(Pageable pageable) {
        // 실제 구현에서는 사용자의 관심사를 분석하여 추천 책을 제공
        // 테스트 목적으로 임시 구현
        return bookRepository.findByIsDeletedFalse(pageable);
    }

    @Override
    public Page<Book> getDefaultRecommendations(Pageable pageable) {
        // 실제 구현에서는 인기 있는 책이나 최신 책을 추천
        // 테스트 목적으로 임시 구현
        return bookRepository.findByIsDeletedFalse(pageable);
    }
}
