package com.j30n.stoblyx.application.service.like;

import com.j30n.stoblyx.domain.model.Like;
import com.j30n.stoblyx.domain.model.Quote;
import com.j30n.stoblyx.domain.model.User;
import com.j30n.stoblyx.domain.repository.LikeRepository;
import com.j30n.stoblyx.domain.repository.QuoteRepository;
import com.j30n.stoblyx.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final QuoteRepository quoteRepository;

    @Override
    @Transactional
    public void likeQuote(Long userId, Long quoteId) {
        User user = findUserById(userId);
        Quote quote = findQuoteById(quoteId);

        likeRepository.findByUserIdAndQuoteId(userId, quoteId)
            .ifPresentOrElse(
                like -> {
                    if (like.isDeleted()) {
                        like.undelete();
                    }
                },
                () -> {
                    Like like = Like.builder()
                        .user(user)
                        .quote(quote)
                        .build();
                    likeRepository.save(like);
                }
            );
    }

    @Override
    @Transactional
    public void unlikeQuote(Long userId, Long quoteId) {
        likeRepository.findByUserIdAndQuoteId(userId, quoteId)
            .ifPresent(Like::delete);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasLiked(Long userId, Long quoteId) {
        return likeRepository.findByUserIdAndQuoteId(userId, quoteId)
            .map(like -> !like.isDeleted())
            .orElse(false);
    }

    @Override
    @Transactional(readOnly = true)
    public long getLikeCount(Long quoteId) {
        return likeRepository.countByQuoteId(quoteId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> getLikedQuoteIds(Long userId) {
        return likeRepository.findByUserId(userId)
            .stream()
            .map(like -> like.getQuote().getId())
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Long> getLikedQuoteIds(Long userId, Pageable pageable) {
        return likeRepository.findByUserId(userId, pageable)
            .map(like -> like.getQuote().getId());
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다. id: " + id));
    }

    private Quote findQuoteById(Long id) {
        return quoteRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 문구입니다. id: " + id));
    }
}