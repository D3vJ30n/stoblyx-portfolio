package com.j30n.stoblyx.adapter.out.persistence.mapper;

import com.j30n.stoblyx.adapter.out.persistence.entity.QuoteJpaEntity;
import com.j30n.stoblyx.domain.model.book.BookId;
import com.j30n.stoblyx.domain.model.quote.Content;
import com.j30n.stoblyx.domain.model.quote.Page;
import com.j30n.stoblyx.domain.model.quote.Quote;
import com.j30n.stoblyx.domain.model.quote.QuoteId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 인용구 도메인 모델과 JPA 엔티티 간의 변환을 처리하는 매퍼
 */
@Component
@RequiredArgsConstructor
public class QuoteMapper {
    private final UserMapper userMapper;

    /**
     * 도메인 엔티티를 JPA 엔티티로 변환합니다.
     *
     * @param quote 도메인 엔티티
     * @return JPA 엔티티 (Optional)
     */
    public Optional<QuoteJpaEntity> toJpaEntity(Quote quote) {
        return Optional.ofNullable(quote)
            .map(q -> {
                QuoteJpaEntity entity = new QuoteJpaEntity();
                if (q.getId() != null) {
                    entity.setId(q.getId().value());
                }
                entity.setContent(q.getContent().value());
                entity.setBookId(q.getBookId().value());
                entity.setPage(q.getPage().value());
                entity.setDeleted(q.isDeleted());
                entity.setCreatedAt(q.getCreatedAt());
                entity.setModifiedAt(q.getModifiedAt());

                // 연관 엔티티 매핑
                userMapper.toJpaEntity(q.getUser())
                    .ifPresent(entity::setUser);

                return entity;
            });
    }

    /**
     * JPA 엔티티를 도메인 엔티티로 변환합니다.
     *
     * @param entity JPA 엔티티
     * @return 도메인 엔티티 (Optional)
     */
    public Optional<Quote> toDomainEntity(QuoteJpaEntity entity) {
        return Optional.ofNullable(entity)
            .flatMap(e -> userMapper.toDomainEntity(e.getUser())
                .map(user -> {
                    Quote quote = Quote.builder()
                        .content(new Content(e.getContent()))
                        .bookId(new BookId(e.getBookId()))
                        .page(new Page(e.getPage()))
                        .user(user)
                        .build();

                    if (e.getId() != null) {
                        quote.setId(new QuoteId(e.getId()));
                    }
                    if (e.isDeleted()) {
                        quote.delete();
                    }
                    quote.setTimeInfo(e.getCreatedAt(), e.getModifiedAt());

                    return quote;
                }));
    }
} 