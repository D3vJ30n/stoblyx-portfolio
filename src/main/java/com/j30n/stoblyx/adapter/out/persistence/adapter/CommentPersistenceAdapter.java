package com.j30n.stoblyx.adapter.out.persistence.adapter;

import com.j30n.stoblyx.adapter.out.persistence.mapper.CommentMapper;
import com.j30n.stoblyx.adapter.out.persistence.mapper.QuoteMapper;
import com.j30n.stoblyx.adapter.out.persistence.mapper.UserMapper;
import com.j30n.stoblyx.adapter.out.persistence.repository.CommentRepository;
import com.j30n.stoblyx.domain.model.comment.Comment;
import com.j30n.stoblyx.domain.model.comment.CommentId;
import com.j30n.stoblyx.domain.model.quote.Quote;
import com.j30n.stoblyx.domain.model.user.User;
import com.j30n.stoblyx.domain.port.out.comment.CommentPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 댓글 영속성 어댑터
 * 도메인 모델과 JPA 엔티티 간의 변환을 처리하고 데이터베이스 작업을 수행합니다.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentPersistenceAdapter implements CommentPort {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final QuoteMapper quoteMapper;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public Comment save(Comment comment) {
        if (comment == null) {
            log.error("댓글 정보가 null입니다");
            throw new IllegalArgumentException("댓글 정보는 null일 수 없습니다");
        }

        try {
            log.debug("댓글 저장 시도 - 작성자: {}", comment.getUser().getEmail());
            var savedEntity = commentRepository.save(
                commentMapper.toJpaEntity(comment)
                    .orElseThrow(() -> new IllegalArgumentException("댓글 변환 중 오류가 발생했습니다"))
            );
            var savedComment = commentMapper.toDomainEntity(savedEntity)
                .orElseThrow(() -> new IllegalArgumentException("저장된 댓글 변환 중 오류가 발생했습니다"));
            log.info("댓글 저장 완료 - ID: {}", savedComment.getId().getValue());
            return savedComment;
        } catch (Exception e) {
            log.error("댓글 저장 실패 - 작성자: {}, 오류: {}", comment.getUser().getEmail(), e.getMessage());
            throw e;
        }
    }

    @Override
    public List<Comment> findByUser(User user) {
        if (user == null) {
            log.error("사용자가 null인 댓글은 조회할 수 없습니다");
            return List.of();
        }

        log.debug("사용자의 댓글 목록 조회 시도 - 사용자 ID: {}", user.getId());
        var userEntity = userMapper.toJpaEntity(user);
        if (userEntity.isEmpty()) {
            log.error("사용자 변환 중 오류가 발생했습니다");
            return List.of();
        }

        return commentRepository.findByUserOrderByCreatedAtDesc(userEntity)
            .stream()
            .map(commentMapper::toDomainEntity)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());
    }

    @Override
    public Optional<Comment> findById(CommentId id) {
        if (id == null) {
            log.error("ID가 null인 댓글은 조회할 수 없습니다");
            return Optional.empty();
        }

        log.debug("댓글 조회 시도 - ID: {}", id.getValue());
        return commentRepository.findById(id.getValue())
            .flatMap(commentMapper::toDomainEntity)
            .map(comment -> {
                log.debug("댓글 조회 완료 - 작성자: {}", comment.getUser().getEmail());
                return comment;
            });
    }

    @Override
    public List<Comment> findByQuote(Quote quote) {
        if (quote == null) {
            log.error("인용구가 null인 댓글은 조회할 수 없습니다");
            return List.of();
        }

        log.debug("인용구의 댓글 목록 조회 시도 - 인용구 ID: {}", quote.getId().getValue());
        var quoteEntity = quoteMapper.toJpaEntity(quote)
            .orElseThrow(() -> new IllegalArgumentException("인용구 변환 중 오류가 발생했습니다"));

        return commentRepository.findByQuoteOrderByCreatedAtDesc(quoteEntity).stream()
            .map(commentMapper::toDomainEntity)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteById(CommentId id) {
        if (id == null) {
            log.error("ID가 null인 댓글은 삭제할 수 없습니다");
            return;
        }

        try {
            log.debug("댓글 삭제 시도 - ID: {}", id.getValue());
            findById(id).ifPresent(comment -> {
                comment.delete();
                save(comment);
                log.info("댓글 삭제 완료 - ID: {}", id.getValue());
            });
        } catch (Exception e) {
            log.error("댓글 삭제 실패 - ID: {}, 오류: {}", id.getValue(), e.getMessage());
            throw e;
        }
    }

    @Override
    public long countByQuote(Quote quote) {
        if (quote == null) {
            log.error("인용구가 null인 댓글은 집계할 수 없습니다");
            return 0;
        }

        log.debug("인용구의 댓글 수 집계 시도 - 인용구 ID: {}", quote.getId().getValue());
        return quoteMapper.toJpaEntity(quote)
            .map(commentRepository::countByQuoteAndIsDeletedFalse)
            .orElse(0L);
    }
}