package com.j30n.stoblyx.adapter.out.persistence.mapper;

import com.j30n.stoblyx.adapter.out.persistence.entity.QuoteJpaEntity;
import com.j30n.stoblyx.domain.model.quote.Quote;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Quote 도메인 모델과 QuoteJpaEntity 간의 변환을 담당하는 매퍼
 */
@Component
@RequiredArgsConstructor
public class QuoteMapper {

    /**
     * Quote 도메인 모델을 JPA 엔티티로 변환합니다.
     *
     * @param quote 변환할 Quote 도메인 모델
     * @return 변환된 QuoteJpaEntity, quote가 null인 경우 null 반환
     */
    public QuoteJpaEntity toJpaEntity(Quote quote) {
        if (quote == null) {
            return null;
        }

        QuoteJpaEntity entity = new QuoteJpaEntity();
        entity.setId(quote.getId());
        entity.setContent(quote.getContent());
        entity.setPage(quote.getPage());
        entity.setChapter(quote.getChapter());
        entity.setLikeCount(quote.getLikeCount());
        entity.setSaveCount(quote.getSaveCount());
        return entity;
    }

    /**
     * JPA 엔티티를 Quote 도메인 모델로 변환합니다.
     *
     * @param jpaEntity 변환할 QuoteJpaEntity
     * @return 변환된 Quote 도메인 모델, jpaEntity가 null인 경우 null 반환
     */
    public Quote toDomainEntity(QuoteJpaEntity jpaEntity) {
        if (jpaEntity == null) {
            return null;
        }

        return Quote.builder()
            .id(jpaEntity.getId())
            .content(jpaEntity.getContent())
            .page(jpaEntity.getPage())
            .chapter(jpaEntity.getChapter())
            .likeCount(jpaEntity.getLikeCount())
            .saveCount(jpaEntity.getSaveCount())
            .build();
    }
} 