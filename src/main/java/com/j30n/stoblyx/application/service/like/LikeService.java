package com.j30n.stoblyx.application.service.like;

import com.j30n.stoblyx.application.port.in.like.LikeUseCase;
import com.j30n.stoblyx.application.port.out.like.LikePort;
import com.j30n.stoblyx.domain.model.Quote;
import com.j30n.stoblyx.domain.model.User;
import com.j30n.stoblyx.domain.repository.QuoteRepository;
import com.j30n.stoblyx.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LikeService extends LikeUseCase {

    private final LikePort likePort;
    private final UserRepository userRepository;
    private final QuoteRepository quoteRepository;

    @Override
    @Transactional
    public void likeQuote(Long userId, Long quoteId) {
        User user = findUserById(userId);
        Quote quote = findQuoteById(quoteId);
        likePort.save(userId, quoteId);
    }

    @Override
    @Transactional
    public void unlikeQuote(Long userId, Long quoteId) {
        likePort.delete(userId, quoteId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasLiked(Long userId, Long quoteId) {
        return likePort.exists(userId, quoteId);
    }

    @Override
    @Transactional(readOnly = true)
    public long getLikeCount(Long quoteId) {
        return likePort.countByQuoteId(quoteId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> getLikedQuoteIds(Long userId) {
        return likePort.findQuoteIdsByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Long> getLikedQuoteIds(Long userId, Pageable pageable) {
        return likePort.findQuoteIdsByUserId(userId, pageable);
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다. id: " + id));
    }

    private Quote findQuoteById(Long id) {
        return quoteRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 인용구입니다. id: " + id));
    }
}