package com.j30n.stoblyx.application.port.out.book;

import com.j30n.stoblyx.domain.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
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
    
    /**
     * 검색 기록 기반 추천 책 목록을 조회합니다.
     *
     * @param pageable 페이징 정보
     * @return 추천 책 목록
     */
    Page<Book> getHistoryBasedRecommendations(Pageable pageable);
    
    /**
     * 관심사 기반 추천 책 목록을 조회합니다.
     *
     * @param pageable 페이징 정보
     * @return 추천 책 목록
     */
    Page<Book> getInterestBasedRecommendations(Pageable pageable);
    
    /**
     * 기본 추천 책 목록을 조회합니다.
     *
     * @param pageable 페이징 정보
     * @return 추천 책 목록
     */
    Page<Book> getDefaultRecommendations(Pageable pageable);
    
    /**
     * 인기 있는 책 목록을 조회합니다.
     *
     * @param pageable 페이징 정보
     * @return 인기 책 목록
     */
    Page<Book> findPopularBooks(Pageable pageable);
    
    /**
     * 책 ID로 책을 조회합니다.
     *
     * @param bookId 책 ID
     * @return 책 Optional
     */
    Optional<Book> findBookById(Long bookId);
    
    /**
     * 모든 책 목록을 조회합니다.
     *
     * @return 모든 책 목록
     */
    List<Book> findAllBooks();
}
