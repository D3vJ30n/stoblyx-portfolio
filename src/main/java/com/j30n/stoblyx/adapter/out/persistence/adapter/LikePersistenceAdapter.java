package com.j30n.stoblyx.adapter.out.persistence.adapter;

import com.j30n.stoblyx.adapter.out.persistence.mapper.LikeMapper;
import com.j30n.stoblyx.adapter.out.persistence.mapper.QuoteMapper;
import com.j30n.stoblyx.adapter.out.persistence.mapper.UserMapper;
import com.j30n.stoblyx.adapter.out.persistence.repository.LikeRepository;
import com.j30n.stoblyx.domain.model.like.Like;
import com.j30n.stoblyx.domain.model.like.LikeId;
import com.j30n.stoblyx.domain.model.quote.Quote;
import com.j30n.stoblyx.domain.model.user.User;
import com.j30n.stoblyx.domain.port.out.like.LikePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 좋아요 영속성 어댑터
 * 도메인 모델과 JPA 엔티티 간의 변환을 처리하고 데이터베이스 작업을 수행합니다.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikePersistenceAdapter implements LikePort {
    private final LikeRepository likeRepository;
    private final LikeMapper likeMapper;
    private final QuoteMapper quoteMapper;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public Like save(Like like) {
        if (like == null) {
            log.error("좋아요 정보가 null입니다");
            throw new IllegalArgumentException("좋아요 정보는 null일 수 없습니다");
        }

        try {
            log.debug("좋아요 저장 시도 - 작성자: {}", like.getUser().getEmail());
            var savedEntity = likeRepository.save(
                likeMapper.toJpaEntity(like)
                    .orElseThrow(() -> new IllegalArgumentException("좋아요 변환 중 오류가 발생했습니다"))
            );
            var savedLike = likeMapper.toDomainEntity(savedEntity)
                .orElseThrow(() -> new IllegalArgumentException("저장된 좋아요 변환 중 오류가 발생했습니다"));
            log.info("좋아요 저장 완료 - ID: {}", savedLike.getId().getClass());
            return savedLike;
        } catch (Exception e) {
            log.error("좋아요 저장 실패 - 작성자: {}, 오류: {}", like.getUser().getEmail(), e.getMessage());
            throw e;
        }
    }

    @Override
    public Optional<Like> findById(LikeId id) {
        if (id == null) {
            log.error("ID가 null인 좋아요는 조회할 수 없습니다");
            return Optional.empty();
        }

        log.debug("좋아요 조회 시도 - ID: {}", id.getClass());
        return likeRepository.findById(id.getValue())
            .flatMap(likeMapper::toDomainEntity)
            .map(like -> {
                log.debug("좋아요 조회 완료 - 작성자: {}", like.getUser().getEmail());
                return like;
            });
    }

    @Override
    public List<Like> findByQuote(Quote quote) {
        if (quote == null) {
            log.error("인용구가 null인 좋아요는 조회할 수 없습니다");
            return List.of();
        }

        log.debug("인용구의 좋아요 목록 조회 시도 - 인용구 ID: {}", quote.getId().getValue());
        return quoteMapper.toJpaEntity(quote)
            .map(quoteEntity -> likeRepository.findByQuoteOrderByCreatedAtDesc(quoteEntity).stream()
                .map(likeMapper::toDomainEntity)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList()))
            .orElse(List.of());
    }

    @Override
    public List<Like> findByUser(User user) {
        if (user == null) {
            log.error("사용자가 null인 좋아요는 조회할 수 없습니다");
            return List.of();
        }

        log.debug("사용자의 좋아요 목록 조회 시도 - 사용자 ID: {}", user.getId());
        var userEntity = userMapper.toJpaEntity(user);
        if (userEntity == null) {
            log.error("사용자 변환 중 오류가 발생했습니다");
            return List.of();
        }

        return likeRepository.findByUserOrderByCreatedAtDesc(userEntity).stream()
            .map(likeMapper::toDomainEntity)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());
    }

    @Override
    public Optional<Like> findByQuoteAndUser(Quote quote, User user) {
        if (quote == null || user == null) {
            log.error("인용구 또는 사용자가 null인 좋아요는 조회할 수 없습니다");
            return Optional.empty();
        }

        log.debug("사용자의 인용구 좋아요 조회 시도 - 인용구 ID: {}, 사용자 ID: {}",
            quote.getId().getClass(), user.getId());

        var quoteJpaEntity = quoteMapper.toJpaEntity(quote);
        var userJpaEntity = userMapper.toJpaEntity(user);

        if (quoteJpaEntity == null || userJpaEntity == null) {
            log.error("인용구 또는 사용자 변환 중 오류가 발생했습니다");
            return Optional.empty();
        }

        return likeRepository.findByQuoteAndUser(quoteJpaEntity, userJpaEntity)
            .flatMap(likeMapper::toDomainEntity);
    }

    @Override
    public long countActiveByQuote(Quote quote) {
        if (quote == null) {
            log.error("인용구가 null인 좋아요는 집계할 수 없습니다");
            return 0;
        }

        log.debug("인용구의 활성화된 좋아요 수 집계 시도 - 인용구 ID: {}", quote.getId().getValue());
        return quoteMapper.toJpaEntity(quote)
            .map(likeRepository::countByQuoteAndIsActiveTrue)
            .orElse(0L);
    }
} 