package com.j30n.stoblyx.domain.repository;

import com.j30n.stoblyx.domain.model.Quote;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * 명언 JPA 리포지토리
 */
public interface QuoteRepository extends JpaRepository<Quote, Long> {
    Page<Quote> findByUserId(Long userId, Pageable pageable);

    @Query("SELECT CASE WHEN COUNT(q) > 0 THEN true ELSE false END FROM Quote q WHERE q.id = :id AND q.user.id = :userId")
    boolean existsByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);

    @Query("SELECT q FROM Quote q WHERE " +
           "(:keyword IS NULL OR LOWER(q.content) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:category IS NULL OR q.book.category = :category)")
    Page<Quote> findByKeywordAndCategory(
        @Param("keyword") String keyword,
        @Param("category") String category,
        Pageable pageable
    );
}