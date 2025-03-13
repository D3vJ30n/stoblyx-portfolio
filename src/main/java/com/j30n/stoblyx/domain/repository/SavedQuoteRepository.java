package com.j30n.stoblyx.domain.repository;

import com.j30n.stoblyx.domain.model.Quote;
import com.j30n.stoblyx.domain.model.SavedQuote;
import com.j30n.stoblyx.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 저장된 인용구 리포지토리
 */
@Repository
public interface SavedQuoteRepository extends JpaRepository<SavedQuote, Long> {
    
    /**
     * 사용자와 인용구로 저장된 인용구를 찾습니다.
     */
    Optional<SavedQuote> findByUserAndQuote(User user, Quote quote);
    
    /**
     * 사용자 ID와 인용구 ID로 저장된 인용구를 찾습니다.
     */
    Optional<SavedQuote> findByUserIdAndQuoteId(Long userId, Long quoteId);
    
    /**
     * 사용자 ID로 저장된 인용구 목록을 찾습니다.
     */
    Page<SavedQuote> findByUserId(Long userId, Pageable pageable);
    
    /**
     * 인용구 ID로 저장된 인용구 목록을 찾습니다.
     */
    List<SavedQuote> findByQuoteId(Long quoteId);
    
    /**
     * 사용자 ID로 저장된 인용구 수를 계산합니다.
     */
    long countByUserId(Long userId);
    
    /**
     * 사용자 ID와 인용구 ID로 저장된 인용구를 삭제합니다.
     */
    void deleteByUserIdAndQuoteId(Long userId, Long quoteId);
    
    /**
     * 사용자 ID로 모든 저장된 인용구를 삭제합니다.
     */
    void deleteByUserId(Long userId);
} 