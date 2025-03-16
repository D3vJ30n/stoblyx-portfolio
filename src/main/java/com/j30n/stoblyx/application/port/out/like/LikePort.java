package com.j30n.stoblyx.application.port.out.like;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface LikePort {
    void save(Long userId, Long quoteId);

    void delete(Long userId, Long quoteId);

    boolean exists(Long userId, Long quoteId);

    long countByQuoteId(Long quoteId);

    List<Long> findQuoteIdsByUserId(Long userId);

    Page<Long> findQuoteIdsByUserId(Long userId, Pageable pageable);
}
