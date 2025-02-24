package com.j30n.stoblyx.domain.repository;

import com.j30n.stoblyx.domain.model.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {
    Optional<Video> findByQuoteId(Long quoteId);
    boolean existsByQuoteId(Long quoteId);
} 