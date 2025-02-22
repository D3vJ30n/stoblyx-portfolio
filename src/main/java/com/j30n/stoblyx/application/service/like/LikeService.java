package com.j30n.stoblyx.application.service.like;

import com.j30n.stoblyx.domain.model.like.Like;
import com.j30n.stoblyx.domain.model.like.LikeId;
import com.j30n.stoblyx.domain.model.quote.Quote;
import com.j30n.stoblyx.domain.model.user.User;
import com.j30n.stoblyx.domain.port.in.like.FindLikeUseCase;
import com.j30n.stoblyx.domain.port.in.like.ToggleLikeUseCase;
import com.j30n.stoblyx.domain.port.out.like.LikePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 좋아요 서비스
 * 좋아요 관련 유스케이스를 구현합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikeService implements ToggleLikeUseCase, FindLikeUseCase {

    private final LikePort likePort;

    @Override
    @Transactional
    public Like toggleLike(Quote quote, User user) {
        try {
            log.debug("좋아요 토글 시도 - 인용구: {}, 사용자: {}", quote.getId().value(), user.getEmail());
            
            // 기존 좋아요 조회
            Optional<Like> existingLike = likePort.findByQuoteAndUser(quote, user);

            Like like;
            if (existingLike.isPresent()) {
                // 기존 좋아요가 있으면 활성화/비활성화 전환
                like = existingLike.get();
                if (like.isActive()) {
                    like.cancel();
                } else {
                    like.activate();
                }
            } else {
                // 기존 좋아요가 없으면 새로 생성
                like = Like.create(user, quote);
            }

            Like savedLike = likePort.save(like);
            log.info("좋아요 토글 완료 - ID: {}, 활성화: {}", savedLike.getId().value(), savedLike.isActive());
            return savedLike;
        } catch (Exception e) {
            log.error("좋아요 토글 실패 - 인용구: {}, 사용자: {}, 오류: {}", 
                quote.getId().value(), user.getEmail(), e.getMessage());
            throw e;
        }
    }

    @Override
    public Optional<Like> findById(LikeId id) {
        try {
            log.debug("좋아요 조회 시도 - ID: {}", id.value());
            return likePort.findById(id)
                .map(like -> {
                    log.debug("좋아요 조회 완료 - ID: {}", like.getId().value());
                    return like;
                });
        } catch (Exception e) {
            log.error("좋아요 조회 실패 - ID: {}, 오류: {}", id.value(), e.getMessage());
            throw e;
        }
    }

    @Override
    public List<Like> findByQuote(Quote quote) {
        try {
            log.debug("인용구의 좋아요 목록 조회 시도 - 인용구 ID: {}", quote.getId().value());
            List<Like> likes = likePort.findByQuote(quote);
            log.debug("인용구의 좋아요 목록 조회 완료 - 좋아요 수: {}", likes.size());
            return likes;
        } catch (Exception e) {
            log.error("인용구의 좋아요 목록 조회 실패 - 인용구 ID: {}, 오류: {}", 
                quote.getId().value(), e.getMessage());
            throw e;
        }
    }

    @Override
    public List<Like> findByUser(User user) {
        try {
            log.debug("사용자의 좋아요 목록 조회 시도 - 사용자 ID: {}", user.getId());
            List<Like> likes = likePort.findByUser(user);
            log.debug("사용자의 좋아요 목록 조회 완료 - 좋아요 수: {}", likes.size());
            return likes;
        } catch (Exception e) {
            log.error("사용자의 좋아요 목록 조회 실패 - 사용자 ID: {}, 오류: {}", user.getId(), e.getMessage());
            throw e;
        }
    }

    @Override
    public Optional<Like> findByQuoteAndUser(Quote quote, User user) {
        try {
            log.debug("사용자의 인용구 좋아요 조회 시도 - 인용구 ID: {}, 사용자 ID: {}", 
                quote.getId().value(), user.getId());
            return likePort.findByQuoteAndUser(quote, user)
                .map(like -> {
                    log.debug("사용자의 인용구 좋아요 조회 완료 - ID: {}", like.getId().value());
                    return like;
                });
        } catch (Exception e) {
            log.error("사용자의 인용구 좋아요 조회 실패 - 인용구 ID: {}, 사용자 ID: {}, 오류: {}", 
                quote.getId().value(), user.getId(), e.getMessage());
            throw e;
        }
    }

    @Override
    public long countActiveByQuote(Quote quote) {
        try {
            log.debug("인용구의 활성화된 좋아요 수 집계 시도 - 인용구 ID: {}", quote.getId().value());
            long count = likePort.countActiveByQuote(quote);
            log.debug("인용구의 활성화된 좋아요 수 집계 완료 - 좋아요 수: {}", count);
            return count;
        } catch (Exception e) {
            log.error("인용구의 활성화된 좋아요 수 집계 실패 - 인용구 ID: {}, 오류: {}", 
                quote.getId().value(), e.getMessage());
            throw e;
        }
    }
} 