package com.j30n.stoblyx.domain.repository;

import com.j30n.stoblyx.domain.model.Search;
import com.j30n.stoblyx.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SearchRepository extends JpaRepository<Search, Long> {
    
    /**
     * 사용자의 검색 기록을 찾습니다.
     */
    Page<Search> findByUser(User user, Pageable pageable);
    
    /**
     * 사용자 ID로 검색 기록을 찾습니다.
     */
    Page<Search> findByUserId(Long userId, Pageable pageable);
    
    /**
     * 카테고리별 검색 기록을 찾습니다.
     */
    Page<Search> findByCategory(String category, Pageable pageable);
    
    /**
     * 특정 기간의 검색 기록을 찾습니다.
     */
    List<Search> findBySearchedAtBetween(LocalDateTime start, LocalDateTime end);
    
    /**
     * 특정 키워드를 포함하는 검색 기록을 찾습니다.
     */
    Page<Search> findByKeywordContaining(String keyword, Pageable pageable);
    
    /**
     * 사용자 ID로 검색 기록을 삭제합니다.
     */
    void deleteByUserId(Long userId);
} 