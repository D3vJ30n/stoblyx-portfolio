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
     * 검색 타입별 검색 기록을 찾습니다.
     */
    Page<Search> findBySearchType(String searchType, Pageable pageable);
    
    /**
     * 특정 기간의 검색 기록을 찾습니다.
     */
    List<Search> findByLastSearchedAtBetween(LocalDateTime start, LocalDateTime end);
    
    /**
     * 특정 검색어를 포함하는 검색 기록을 찾습니다.
     */
    Page<Search> findBySearchTermContaining(String searchTerm, Pageable pageable);
    
    /**
     * 사용자 ID로 검색 기록을 삭제합니다.
     */
    void deleteByUserId(Long userId);
    
    /**
     * 사용자의 최근 검색 기록을 찾습니다.
     */
    List<Search> findTop10ByUserOrderByLastSearchedAtDesc(User user);
} 