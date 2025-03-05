package com.j30n.stoblyx.domain.repository;

import com.j30n.stoblyx.domain.model.SearchTermProfile;
import com.j30n.stoblyx.domain.model.User;
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
    
    Optional<SearchTermProfile> findByUserAndSearchTerm(User user, String searchTerm);
    
    List<SearchTermProfile> findByUser(User user);
    
    Page<SearchTermProfile> findByUser(User user, Pageable pageable);
    
    @Query("SELECT stp FROM SearchTermProfile stp WHERE stp.user.id = :userId ORDER BY stp.searchFrequency DESC")
    List<SearchTermProfile> findTopTermsByUserId(@Param("userId") Long userId, Pageable pageable);
    
    @Query("SELECT DISTINCT stp.searchTerm FROM SearchTermProfile stp GROUP BY stp.searchTerm ORDER BY COUNT(stp) DESC")
    List<String> findMostCommonTerms(Pageable pageable);
    
    @Query("SELECT stp FROM SearchTermProfile stp WHERE stp.user.id = :userId AND stp.searchTerm LIKE %:term%")
    List<SearchTermProfile> findByUserIdAndTermContaining(@Param("userId") Long userId, @Param("term") String term);
} 