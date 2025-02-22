package com.j30n.stoblyx.domain.port.in.quote;

import com.j30n.stoblyx.domain.model.book.BookId;
import com.j30n.stoblyx.domain.model.quote.Quote;
import com.j30n.stoblyx.domain.model.quote.QuoteId;
import com.j30n.stoblyx.domain.model.user.User;

import java.util.List;
import java.util.Optional;

/**
 * 인용구 조회를 위한 입력 포트
 */
public interface FindQuoteUseCase {
    /**
     * ID로 인용구를 조회합니다.
     *
     * @param id 인용구 ID
     * @return 조회된 인용구 (Optional)
     */
    Optional<Quote> findById(QuoteId id);

    /**
     * 책에 속한 인용구 목록을 조회합니다.
     *
     * @param bookId 책 ID
     * @return 인용구 목록
     */
    List<Quote> findByBookId(BookId bookId);

    /**
     * 사용자가 작성한 인용구 목록을 조회합니다.
     *
     * @param user 사용자
     * @return 인용구 목록
     */
    List<Quote> findByUser(User user);

    /**
     * 책의 인용구 수를 조회합니다.
     *
     * @param bookId 책 ID
     * @return 인용구 수
     */
    long countByBookId(BookId bookId);
} 