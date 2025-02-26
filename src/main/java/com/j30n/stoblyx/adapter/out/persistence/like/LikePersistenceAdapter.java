package com.j30n.stoblyx.adapter.out.persistence.like;

import com.j30n.stoblyx.application.port.out.like.LikePort;
import com.j30n.stoblyx.domain.model.Like;
import com.j30n.stoblyx.domain.model.Quote;
import com.j30n.stoblyx.domain.model.User;
import com.j30n.stoblyx.domain.repository.LikeRepository;
import com.j30n.stoblyx.domain.repository.QuoteRepository;
import com.j30n.stoblyx.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class LikePersistenceAdapter implements LikePort {

    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final QuoteRepository quoteRepository;

    @Override
    public void save(Long userId, Long quoteId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다. id: " + userId));
        Quote quote = quoteRepository.findById(quoteId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 인용구입니다. id: " + quoteId));

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
    public void delete(Long userId, Long quoteId) {
        likeRepository.findByUserIdAndQuoteId(userId, quoteId)
            .ifPresent(Like::delete);
    }

    @Override
    public boolean exists(Long userId, Long quoteId) {
        return likeRepository.findByUserIdAndQuoteId(userId, quoteId)
            .map(like -> !like.isDeleted())
            .orElse(false);
    }

    @Override
    public long countByQuoteId(Long quoteId) {
        return likeRepository.countByQuoteId(quoteId);
    }

    @Override
    public List<Long> findQuoteIdsByUserId(Long userId) {
        return likeRepository.findByUserId(userId)
            .stream()
            .map(like -> like.getQuote().getId())
            .collect(Collectors.toList());
    }

    @Override
    public Page<Long> findQuoteIdsByUserId(Long userId, Pageable pageable) {
        return likeRepository.findByUserId(userId, pageable)
            .map(like -> like.getQuote().getId());
    }
}
