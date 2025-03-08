package com.j30n.stoblyx.domain.repository;

import com.j30n.stoblyx.domain.model.Quote;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * 명언 JPA 리포지토리
 */
public interface QuoteRepository extends JpaRepository<Quote, Long> {
    Page<Quote> findByUserId(Long userId, Pageable pageable);

    @Query("SELECT CASE WHEN COUNT(q) > 0 THEN true ELSE false END FROM Quote q WHERE q.id = :id AND q.user.id = :userId")
    boolean existsByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);

    @Query("SELECT q FROM Quote q JOIN q.book b WHERE " +
           "(:keyword IS NULL OR LOWER(q.content) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:category IS NULL OR :category MEMBER OF b.genres)")
    Page<Quote> findByKeywordAndCategory(
        @Param("keyword") String keyword,
        @Param("category") String category,
        Pageable pageable
    );
    
    /**
     * N+1 문제 해결을 위해 EntityGraph 사용
     * 인용구 조회 시 사용자와 책 정보를 함께 로딩
     */
    @EntityGraph(attributePaths = {"user", "book"})
    @Override
    Optional<Quote> findById(Long id);
    
    /**
     * N+1 문제 해결을 위해 EntityGraph 사용
     * 사용자 ID로 인용구 목록 조회 시 책 정보를 함께 로딩
     */
    @EntityGraph(attributePaths = {"book"})
    Page<Quote> findByUserIdAndIsDeletedFalse(Long userId, Pageable pageable);
    
    /**
     * N+1 문제 해결을 위해 Fetch Join 사용
     * 키워드와 카테고리로 인용구 검색 시 사용자와 책 정보를 함께 로딩
     */
    @Query("SELECT DISTINCT q FROM Quote q " +
           "JOIN FETCH q.user " +
           "JOIN FETCH q.book b " +
           "WHERE (:keyword IS NULL OR LOWER(q.content) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND (:category IS NULL OR :category MEMBER OF b.genres) " +
           "AND q.isDeleted = false")
    List<Quote> findByKeywordAndCategoryWithUserAndBook(
        @Param("keyword") String keyword,
        @Param("category") String category
    );
    
    /**
     * 페이징 처리가 필요한 경우 카운트 쿼리 최적화
     */
    @Query(value = "SELECT q FROM Quote q " +
                  "JOIN q.user u " +
                  "JOIN q.book b " +
                  "WHERE (:keyword IS NULL OR LOWER(q.content) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
                  "AND (:category IS NULL OR :category MEMBER OF b.genres) " +
                  "AND q.isDeleted = false",
           countQuery = "SELECT COUNT(q) FROM Quote q " +
                       "JOIN q.book b " +
                       "WHERE (:keyword IS NULL OR LOWER(q.content) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
                       "AND (:category IS NULL OR :category MEMBER OF b.genres) " +
                       "AND q.isDeleted = false")
    Page<Quote> findByKeywordAndCategoryOptimized(
        @Param("keyword") String keyword,
        @Param("category") String category,
        Pageable pageable
    );
}