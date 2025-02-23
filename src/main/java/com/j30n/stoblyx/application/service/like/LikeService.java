package com.j30n.stoblyx.application.service.like;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface LikeService {
    void likeQuote(Long userId, Long quoteId);
    void unlikeQuote(Long userId, Long quoteId);
    boolean hasLiked(Long userId, Long quoteId);
    long getLikeCount(Long quoteId);
    List<Long> getLikedQuoteIds(Long userId);
    Page<Long> getLikedQuoteIds(Long userId, Pageable pageable);
} 