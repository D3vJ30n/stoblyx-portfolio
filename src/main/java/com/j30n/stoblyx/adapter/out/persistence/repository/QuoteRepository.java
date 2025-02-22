package com.j30n.stoblyx.adapter.out.persistence.repository;

import com.j30n.stoblyx.adapter.out.persistence.entity.QuoteJpaEntity;
import com.j30n.stoblyx.adapter.out.persistence.entity.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 인용구 JPA 리포지토리
 */
@Repository
public interface QuoteRepository extends JpaRepository<QuoteJpaEntity, Long> {
    /**
     * 책에 속한 인용구 목록을 조회합니다.
     */
    List<QuoteJpaEntity> findByBookIdOrderByCreatedAtDesc(Long bookId);

    /**
     * 사용자가 작성한 인용구 목록을 조회합니다.
     */
    List<QuoteJpaEntity> findByUserOrderByCreatedAtDesc(UserJpaEntity user);

    /**
     * 책의 인용구 수를 조회합니다.
     */
    @Query("SELECT COUNT(q) FROM QuoteJpaEntity q WHERE q.bookId = :bookId AND q.isDeleted = false")
    long countByBookIdAndIsDeletedFalse(Long bookId);
} 