package com.j30n.stoblyx.adapter.out.persistence.mapper;

import com.j30n.stoblyx.adapter.out.persistence.entity.SummaryJpaEntity;
import com.j30n.stoblyx.domain.model.summary.Summary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Summary 도메인 모델과 SummaryJpaEntity 간의 변환을 담당하는 매퍼
 */
@Component
@RequiredArgsConstructor
public class SummaryMapper {

    /**
     * Summary 도메인 모델을 JPA 엔티티로 변환합니다.
     *
     * @param summary 변환할 Summary 도메인 모델
     * @return 변환된 SummaryJpaEntity를 포함한 Optional, summary가 null인 경우 empty Optional 반환
     */
    public Optional<SummaryJpaEntity> toJpaEntity(Summary summary) {
        return Optional.ofNullable(summary)
            .map(s -> {
                SummaryJpaEntity entity = new SummaryJpaEntity();
                entity.setId(s.getId());
                entity.setSummaryText(s.getSummaryText());
                entity.setOriginalLength(s.getOriginalLength());
                entity.setSummaryLength(s.getSummaryLength());
                return entity;
            });
    }

    /**
     * JPA 엔티티를 Summary 도메인 모델로 변환합니다.
     *
     * @param jpaEntity 변환할 SummaryJpaEntity
     * @return 변환된 Summary 도메인 모델을 포함한 Optional, jpaEntity가 null인 경우 empty Optional 반환
     */
    public Optional<Summary> toDomainEntity(SummaryJpaEntity jpaEntity) {
        return Optional.ofNullable(jpaEntity)
            .map(e -> Summary.builder()
                .id(e.getId())
                .summaryText(e.getSummaryText())
                .originalLength(e.getOriginalLength())
                .summaryLength(e.getSummaryLength())
                .build());
    }
} 