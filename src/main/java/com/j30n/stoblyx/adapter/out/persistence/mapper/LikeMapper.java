package com.j30n.stoblyx.adapter.out.persistence.mapper;

import com.j30n.stoblyx.adapter.out.persistence.entity.LikeJpaEntity;
import com.j30n.stoblyx.domain.model.like.Like;
import com.j30n.stoblyx.domain.model.like.LikeId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 좋아요 도메인 모델과 JPA 엔티티 간의 변환을 처리하는 매퍼
 */
@Component
@RequiredArgsConstructor
public class LikeMapper {
    private final UserMapper userMapper;
    private final QuoteMapper quoteMapper;

    /**
     * 도메인 엔티티를 JPA 엔티티로 변환합니다.
     *
     * @param like 도메인 엔티티
     * @return JPA 엔티티 (Optional)
     */
    public Optional<LikeJpaEntity> toJpaEntity(Like like) {
        return Optional.ofNullable(like)
            .map(l -> {
                LikeJpaEntity entity = new LikeJpaEntity();
                if (l.getId() != null) {
                    entity.setId(l.getId().value());
                }
                entity.setActive(l.isActive());
                entity.setCreatedAt(l.getCreatedAt());
                entity.setModifiedAt(l.getModifiedAt());

                // 연관 엔티티 매핑
                userMapper.toJpaEntity(l.getUser())
                    .ifPresent(entity::setUser);
                quoteMapper.toJpaEntity(l.getQuote())
                    .ifPresent(entity::setQuote);

                return entity;
            });
    }

    /**
     * JPA 엔티티를 도메인 엔티티로 변환합니다.
     *
     * @param entity JPA 엔티티
     * @return 도메인 엔티티 (Optional)
     */
    public Optional<Like> toDomainEntity(LikeJpaEntity entity) {
        return Optional.ofNullable(entity)
            .flatMap(e -> {
                // 필수 연관 엔티티 변환
                return userMapper.toDomainEntity(e.getUser())
                    .flatMap(user -> quoteMapper.toDomainEntity(e.getQuote())
                        .map(quote -> {
                            Like like = Like.create(user, quote);
                            if (e.getId() != null) {
                                like.setId(new LikeId(e.getId()));
                            }
                            if (!e.isActive()) {
                                like.cancel();
                            }
                            like.setTimeInfo(e.getCreatedAt(), e.getModifiedAt());
                            return like;
                        }));
            });
    }
} 