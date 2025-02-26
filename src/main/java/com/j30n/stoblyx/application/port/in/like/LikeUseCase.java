package com.j30n.stoblyx.application.port.in.like;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public class LikeUseCase {
    public void likeQuote(Long userId, Long quoteId) {
    }
    public void unlikeQuote(Long userId, Long quoteId) {
    }
    public boolean hasLiked(Long userId, Long quoteId) {
        return false;
    }
    public long getLikeCount(Long quoteId) {
        return 0;
    }
    public List<Long> getLikedQuoteIds(Long userId) {
        return null;
    }
    public Page<Long> getLikedQuoteIds(Long userId, Pageable pageable) {
        return null;
    }
}
