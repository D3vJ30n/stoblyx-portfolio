package com.j30n.stoblyx.domain.repository;

import com.j30n.stoblyx.domain.model.ContentInteraction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContentInteractionRepository extends JpaRepository<ContentInteraction, Long> {

    @Query("SELECT ci FROM ContentInteraction ci " +
           "WHERE ci.user.id = :userId " +
           "AND ci.content.id = :contentId")
    Optional<ContentInteraction> findByUserIdAndContentId(
        @Param("userId") Long userId,
        @Param("contentId") Long contentId
    );

    boolean existsByUserIdAndContentId(Long userId, Long contentId);
} 