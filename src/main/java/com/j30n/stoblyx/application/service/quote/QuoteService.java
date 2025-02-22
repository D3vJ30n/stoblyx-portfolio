package com.j30n.stoblyx.application.service.quote;

import com.j30n.stoblyx.domain.model.book.BookId;
import com.j30n.stoblyx.domain.model.quote.Content;
import com.j30n.stoblyx.domain.model.quote.Page;
import com.j30n.stoblyx.domain.model.quote.Quote;
import com.j30n.stoblyx.domain.model.quote.QuoteId;
import com.j30n.stoblyx.domain.model.user.User;
import com.j30n.stoblyx.domain.port.in.quote.CreateQuoteUseCase;
import com.j30n.stoblyx.domain.port.in.quote.DeleteQuoteUseCase;
import com.j30n.stoblyx.domain.port.in.quote.FindQuoteUseCase;
import com.j30n.stoblyx.domain.port.in.quote.UpdateQuoteUseCase;
import com.j30n.stoblyx.domain.port.out.quote.QuotePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 인용구 서비스
 * 인용구 관련 유스케이스를 구현합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuoteService implements CreateQuoteUseCase, UpdateQuoteUseCase,
    DeleteQuoteUseCase, FindQuoteUseCase {

    private final QuotePort quotePort;

    @Override
    @Transactional
    public Quote createQuote(Content content, Page page, BookId bookId, User user) {
        try {
            log.debug("인용구 생성 시도 - 작성자: {}, 책: {}", user.getEmail(), bookId.value());
            Quote quote = Quote.builder()
                .content(content)
                .page(page)
                .bookId(bookId)
                .user(user)
                .build();
            Quote savedQuote = quotePort.save(quote);
            log.info("인용구 생성 완료 - ID: {}", savedQuote.getId().value());
            return savedQuote;
        } catch (Exception e) {
            log.error("인용구 생성 실패 - 작성자: {}, 오류: {}", user.getEmail(), e.getMessage());
            throw new IllegalStateException("인용구 생성 중 오류가 발생했습니다", e);
        }
    }

    @Override
    @Transactional
    public Quote updateContent(QuoteId id, Content content, User user) {
        try {
            log.debug("인용구 내용 수정 시도 - ID: {}, 작성자: {}", id.value(), user.getEmail());
            Quote quote = quotePort.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("인용구를 찾을 수 없습니다"));

            if (!quote.isAuthor(user)) {
                throw new IllegalArgumentException("인용구 수정 권한이 없습니다");
            }

            quote.updateContent(content);
            Quote updatedQuote = quotePort.save(quote);
            log.info("인용구 내용 수정 완료 - ID: {}", updatedQuote.getId().value());
            return updatedQuote;
        } catch (Exception e) {
            log.error("인용구 내용 수정 실패 - ID: {}, 오류: {}", id.value(), e.getMessage());
            throw e;
        }
    }

    @Override
    @Transactional
    public Quote updatePage(QuoteId id, Page page, User user) {
        try {
            log.debug("인용구 페이지 수정 시도 - ID: {}, 작성자: {}", id.value(), user.getEmail());
            Quote quote = quotePort.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("인용구를 찾을 수 없습니다"));

            if (!quote.isAuthor(user)) {
                throw new IllegalArgumentException("인용구 수정 권한이 없습니다");
            }

            quote.updatePage(page);
            Quote updatedQuote = quotePort.save(quote);
            log.info("인용구 페이지 수정 완료 - ID: {}", updatedQuote.getId().value());
            return updatedQuote;
        } catch (Exception e) {
            log.error("인용구 페이지 수정 실패 - ID: {}, 오류: {}", id.value(), e.getMessage());
            throw e;
        }
    }

    @Override
    @Transactional
    public void deleteQuote(QuoteId id, User user) {
        try {
            log.debug("인용구 삭제 시도 - ID: {}, 작성자: {}", id.value(), user.getEmail());
            Quote quote = quotePort.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("인용구를 찾을 수 없습니다"));

            if (!quote.isAuthor(user)) {
                throw new IllegalArgumentException("인용구 삭제 권한이 없습니다");
            }

            quotePort.deleteById(id);
            log.info("인용구 삭제 완료 - ID: {}", id.value());
        } catch (Exception e) {
            log.error("인용구 삭제 실패 - ID: {}, 오류: {}", id.value(), e.getMessage());
            throw e;
        }
    }

    @Override
    public Optional<Quote> findById(QuoteId id) {
        try {
            log.debug("인용구 조회 시도 - ID: {}", id.value());
            return quotePort.findById(id)
                .map(quote -> {
                    log.debug("인용구 조회 완료 - ID: {}", quote.getId().value());
                    return quote;
                });
        } catch (Exception e) {
            log.error("인용구 조회 실패 - ID: {}, 오류: {}", id.value(), e.getMessage());
            throw e;
        }
    }

    @Override
    public List<Quote> findByBookId(BookId bookId) {
        try {
            log.debug("책의 인용구 목록 조회 시도 - 책 ID: {}", bookId.value());
            List<Quote> quotes = quotePort.findByBookId(bookId);
            log.debug("책의 인용구 목록 조회 완료 - 인용구 수: {}", quotes.size());
            return quotes;
        } catch (Exception e) {
            log.error("책의 인용구 목록 조회 실패 - 책 ID: {}, 오류: {}", bookId.value(), e.getMessage());
            throw e;
        }
    }

    @Override
    public List<Quote> findByUser(User user) {
        try {
            log.debug("사용자의 인용구 목록 조회 시도 - 사용자 ID: {}", user.getId());
            List<Quote> quotes = quotePort.findByUser(user);
            log.debug("사용자의 인용구 목록 조회 완료 - 인용구 수: {}", quotes.size());
            return quotes;
        } catch (Exception e) {
            log.error("사용자의 인용구 목록 조회 실패 - 사용자 ID: {}, 오류: {}", user.getId(), e.getMessage());
            throw e;
        }
    }

    @Override
    public long countByBookId(BookId bookId) {
        try {
            log.debug("책의 인용구 수 집계 시도 - 책 ID: {}", bookId.value());
            long count = quotePort.countByBookId(bookId);
            log.debug("책의 인용구 수 집계 완료 - 인용구 수: {}", count);
            return count;
        } catch (Exception e) {
            log.error("책의 인용구 수 집계 실패 - 책 ID: {}, 오류: {}", bookId.value(), e.getMessage());
            throw e;
        }
    }
} 