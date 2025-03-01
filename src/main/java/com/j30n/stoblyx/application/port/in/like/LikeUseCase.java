package com.j30n.stoblyx.application.port.in.like;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface LikeUseCase {
    void likeQuote(Long quoteId, Long userId);
    void unlikeQuote(Long quoteId, Long userId);
    boolean isLiked(Long quoteId, Long userId);
    long getLikeCount(Long quoteId);
    List<Long> getLikedQuoteIds(Long userId);
    Page<Long> getLikedQuoteIds(Long userId, Pageable pageable);
}
