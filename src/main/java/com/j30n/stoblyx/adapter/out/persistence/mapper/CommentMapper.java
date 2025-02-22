package com.j30n.stoblyx.adapter.out.persistence.mapper;

import com.j30n.stoblyx.adapter.out.persistence.entity.CommentJpaEntity;
import com.j30n.stoblyx.adapter.out.persistence.entity.QuoteJpaEntity;
import com.j30n.stoblyx.adapter.out.persistence.entity.UserJpaEntity;
import com.j30n.stoblyx.domain.model.book.BookId;
import com.j30n.stoblyx.domain.model.comment.Comment;
import com.j30n.stoblyx.domain.model.comment.CommentId;
import com.j30n.stoblyx.domain.model.comment.Content;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 댓글 도메인 모델과 JPA 엔티티 간의 변환을 처리하는 매퍼
 */
@Component
@RequiredArgsConstructor
public class CommentMapper {
    private final UserMapper userMapper;
    private final QuoteMapper quoteMapper;

    /**
     * 도메인 엔티티를 JPA 엔티티로 변환합니다.
     *
     * @param comment 도메인 엔티티
     * @return JPA 엔티티 (Optional)
     */
    public Optional<CommentJpaEntity> toJpaEntity(Comment comment) {
        return Optional.ofNullable(comment)
            .map(c -> {
                CommentJpaEntity entity = new CommentJpaEntity();
                if (c.getId() != null) {
                    entity.setId(c.getId().value());
                }
                entity.setContent(c.getContent().value());
                entity.setBookId(c.getBookId().value());
                entity.setDeleted(c.isDeleted());

                // 연관 엔티티 매핑
                userMapper.toJpaEntity(c.getUser())
                    .ifPresent(entity::setUser);
                quoteMapper.toJpaEntity(c.getQuote())
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
    public Optional<Comment> toDomainEntity(CommentJpaEntity entity) {
        return Optional.ofNullable(entity)
            .flatMap(e -> {
                // 필수 연관 엔티티 변환
                return userMapper.toDomainEntity(e.getUser())
                    .flatMap(user -> quoteMapper.toDomainEntity(e.getQuote())
                        .map(quote -> Comment.builder()
                            .user(user)
                            .quote(quote)
                            .content(new Content(e.getContent()))
                            .bookId(new BookId(e.getBookId()))
                            .build()));
            })
            .map(comment -> {
                // ID 설정
                if (entity.getId() != null) {
                    comment.setId(new CommentId(entity.getId()));
                }
                // 삭제 상태 설정
                if (entity.isDeleted()) {
                    comment.delete();
                }
                return comment;
            });
    }
} 