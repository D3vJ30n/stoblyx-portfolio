package com.j30n.stoblyx.domain.repository;

import com.j30n.stoblyx.domain.model.SearchTermProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SearchTermProfileRepository extends JpaRepository<SearchTermProfile, Long> {
    
    Optional<SearchTermProfile> findBySearchTerm(String searchTerm);
    
    List<SearchTermProfile> findBySearchTermContaining(String searchTerm);
    
    Page<SearchTermProfile> findBySearchTermContaining(String searchTerm, Pageable pageable);
    
    @Query("SELECT stp FROM SearchTermProfile stp ORDER BY stp.searchCount DESC")
    List<SearchTermProfile> findTopTerms(Pageable pageable);
    
    @Query("SELECT DISTINCT stp.searchTerm FROM SearchTermProfile stp GROUP BY stp.searchTerm ORDER BY COUNT(stp) DESC")
    List<String> findMostCommonTerms(Pageable pageable);
    
    @Query("SELECT stp FROM SearchTermProfile stp WHERE stp.searchTerm LIKE %:term%")
    List<SearchTermProfile> findByTermContaining(@Param("term") String term);
} 