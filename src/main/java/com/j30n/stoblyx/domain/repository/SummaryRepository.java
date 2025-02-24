package com.j30n.stoblyx.domain.repository;

import com.j30n.stoblyx.domain.model.Summary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SummaryRepository extends JpaRepository<Summary, Long> {
    Page<Summary> findByBookId(Long bookId, Pageable pageable);
    Page<Summary> findByBookIdAndDeletedFalse(Long bookId, Pageable pageable);
} 