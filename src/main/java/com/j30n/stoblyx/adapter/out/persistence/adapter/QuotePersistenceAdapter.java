package com.j30n.stoblyx.adapter.out.persistence.adapter;

import com.j30n.stoblyx.adapter.out.persistence.mapper.QuoteMapper;
import com.j30n.stoblyx.adapter.out.persistence.mapper.UserMapper;
import com.j30n.stoblyx.adapter.out.persistence.repository.QuoteRepository;
import com.j30n.stoblyx.domain.model.book.BookId;
import com.j30n.stoblyx.domain.model.quote.Quote;
import com.j30n.stoblyx.domain.model.quote.QuoteId;
import com.j30n.stoblyx.domain.model.user.User;
import com.j30n.stoblyx.domain.port.out.quote.QuotePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 인용구 영속성 어댑터
 * 도메인 모델과 JPA 엔티티 간의 변환을 처리하고 데이터베이스 작업을 수행합니다.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuotePersistenceAdapter implements QuotePort {
    private final QuoteRepository quoteRepository;
    private final QuoteMapper quoteMapper;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public Quote save(Quote quote) {
        if (quote == null) {
            log.error("인용구 정보가 null입니다");
            throw new IllegalArgumentException("인용구 정보는 null일 수 없습니다");
        }

        try {
            log.debug("인용구 저장 시도 - 작성자: {}", quote.getUser().getEmail());
            var savedEntity = quoteRepository.save(
                quoteMapper.toJpaEntity(quote)
                    .orElseThrow(() -> new IllegalArgumentException("인용구 변환 중 오류가 발생했습니다"))
            );
            var savedQuote = quoteMapper.toDomainEntity(savedEntity)
                .orElseThrow(() -> new IllegalArgumentException("저장된 인용구 변환 중 오류가 발생했습니다"));
            log.info("인용구 저장 완료 - ID: {}", savedQuote.getId().getValue());
            return savedQuote;
        } catch (Exception e) {
            log.error("인용구 저장 실패 - 작성자: {}, 오류: {}", quote.getUser().getEmail(), e.getMessage());
            throw e;
        }
    }

    @Override
    public Optional<Quote> findById(QuoteId id) {
        if (id == null) {
            log.error("ID가 null인 인용구는 조회할 수 없습니다");
            return Optional.empty();
        }

        log.debug("인용구 조회 시도 - ID: {}", id.getValue());
        return quoteRepository.findById(id.getValue())
            .flatMap(quoteMapper::toDomainEntity)
            .map(quote -> {
                log.debug("인용구 조회 완료 - 작성자: {}", quote.getUser().getEmail());
                return quote;
            });
    }

    @Override
    public List<Quote> findByBookId(BookId bookId) {
        if (bookId == null) {
            log.error("책 ID가 null인 인용구는 조회할 수 없습니다");
            return List.of();
        }

        log.debug("책의 인용구 목록 조회 시도 - 책 ID: {}", bookId.value());
        return quoteRepository.findByBookIdOrderByCreatedAtDesc(bookId.value()).stream()
            .map(quoteMapper::toDomainEntity)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());
    }

    @Override
    public List<Quote> findByUser(User user) {
        if (user == null) {
            log.error("사용자가 null인 인용구는 조회할 수 없습니다");
            return List.of();
        }

        log.debug("사용자의 인용구 목록 조회 시도 - 사용자 ID: {}", user.getId());
        var userJpaEntity = userMapper.toJpaEntity(user);

        if (userJpaEntity == null) {
            log.error("사용자 변환 중 오류가 발생했습니다");
            return List.of();
        }

        return quoteRepository.findByUserOrderByCreatedAtDesc(userJpaEntity).stream()
            .map(quoteMapper::toDomainEntity)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteById(QuoteId id) {
        if (id == null) {
            log.error("ID가 null인 인용구는 삭제할 수 없습니다");
            return;
        }

        try {
            log.debug("인용구 삭제 시도 - ID: {}", id.getValue());
            findById(id).ifPresent(quote -> {
                quote.delete();
                save(quote);
                log.info("인용구 삭제 완료 - ID: {}", id.getValue());
            });
        } catch (Exception e) {
            log.error("인용구 삭제 실패 - ID: {}, 오류: {}", id.getValue(), e.getMessage());
            throw e;
        }
    }

    @Override
    public long countByBookId(BookId bookId) {
        if (bookId == null) {
            log.error("책 ID가 null인 인용구는 집계할 수 없습니다");
            return 0;
        }

        log.debug("책의 인용구 수 집계 시도 - 책 ID: {}", bookId.value());
        return quoteRepository.countByBookIdAndIsDeletedFalse(bookId.value());
    }
} 