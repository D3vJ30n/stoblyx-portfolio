package com.j30n.stoblyx.domain.repository;

import com.j30n.stoblyx.domain.model.User;
import com.j30n.stoblyx.domain.model.UserSimilarity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserSimilarityRepository extends JpaRepository<UserSimilarity, Long> {
    
    Optional<UserSimilarity> findBySourceUserAndTargetUser(User sourceUser, User targetUser);
    
    @Query("SELECT us FROM UserSimilarity us WHERE us.sourceUser.id = :userId AND us.isActive = true ORDER BY us.similarityScore DESC")
    Page<UserSimilarity> findActiveSimilaritiesForUser(@Param("userId") Long userId, Pageable pageable);
    
    @Query("SELECT us FROM UserSimilarity us WHERE us.sourceUser.id = :userId AND us.isActive = true AND us.similarityScore >= :minScore ORDER BY us.similarityScore DESC")
    List<UserSimilarity> findSimilaritiesWithMinScore(@Param("userId") Long userId, @Param("minScore") Double minScore);
    
    @Query("SELECT COUNT(us) FROM UserSimilarity us WHERE us.sourceUser.id = :userId AND us.isActive = true")
    Long countActiveSimilaritiesForUser(@Param("userId") Long userId);
    
    void deleteBySourceUserAndTargetUser(User sourceUser, User targetUser);
} 