package com.j30n.stoblyx.domain.repository;

import com.j30n.stoblyx.domain.model.Quote;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface QuoteRepository extends JpaRepository<Quote, Long> {

    @Query("SELECT q FROM Quote q WHERE q.user.id = :userId AND q.isDeleted = false")
    Page<Quote> findByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT q FROM Quote q WHERE q.book.id = :bookId AND q.isDeleted = false")
    Page<Quote> findByBookId(@Param("bookId") Long bookId, Pageable pageable);

    @Query("SELECT q FROM Quote q WHERE q.user.id = :userId AND q.book.id = :bookId AND q.isDeleted = false")
    Page<Quote> findByUserIdAndBookId(@Param("userId") Long userId, @Param("bookId") Long bookId, Pageable pageable);

    @Query("SELECT q FROM Quote q WHERE q.isDeleted = false")
    Page<Quote> findAll(Pageable pageable);
} 