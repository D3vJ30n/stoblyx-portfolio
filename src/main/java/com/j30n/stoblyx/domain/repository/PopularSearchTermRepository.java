package com.j30n.stoblyx.domain.repository;

import com.j30n.stoblyx.domain.model.PopularSearchTerm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PopularSearchTermRepository extends JpaRepository<PopularSearchTerm, Long> {
    
    Optional<PopularSearchTerm> findBySearchTerm(String searchTerm);
    
    @Query("SELECT pst FROM PopularSearchTerm pst ORDER BY pst.popularityScore DESC")
    Page<PopularSearchTerm> findPopularTerms(Pageable pageable);
    
    @Query("SELECT pst FROM PopularSearchTerm pst WHERE pst.lastUpdatedAt >= :since ORDER BY pst.popularityScore DESC")
    List<PopularSearchTerm> findRecentPopularTerms(@Param("since") LocalDateTime since, Pageable pageable);
    
    @Query("SELECT pst.searchTerm FROM PopularSearchTerm pst ORDER BY pst.searchCount DESC")
    List<String> findMostSearchedTerms(Pageable pageable);
} 