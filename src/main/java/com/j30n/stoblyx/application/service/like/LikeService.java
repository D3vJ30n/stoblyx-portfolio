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
public class LikeService implements LikeUseCase {

    private final LikePort likePort;
    private final UserRepository userRepository;
    private final QuoteRepository quoteRepository;

    @Override
    @Transactional
    public void likeQuote(Long userId, Long quoteId) {
        findUserById(userId);
        findQuoteById(quoteId);
        likePort.save(userId, quoteId);
    }

    @Override
    @Transactional
    public void unlikeQuote(Long userId, Long quoteId) {
        likePort.delete(userId, quoteId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isLiked(Long userId, Long quoteId) {
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

    /**
     * 사용자가 특정 문구에 좋아요를 했는지 확인합니다.
     *
     * @param userId 사용자 ID
     * @param quoteId 문구 ID
     * @return 좋아요 여부 (true: 좋아요 함, false: 좋아요 안함)
     */
    public boolean quoteLiked(Long userId, Long quoteId) {
        // 사용자와 문구 존재 여부 확인
        userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        quoteRepository.findById(quoteId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 문구입니다."));
        
        // 좋아요 여부 확인
        return likePort.exists(userId, quoteId);
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