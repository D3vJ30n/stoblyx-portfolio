package com.j30n.stoblyx.domain.port.out.quote;

import com.j30n.stoblyx.domain.model.book.BookId;
import com.j30n.stoblyx.domain.model.quote.Quote;
import com.j30n.stoblyx.domain.model.quote.QuoteId;
import com.j30n.stoblyx.domain.model.user.User;

import java.util.List;
import java.util.Optional;

/**
 * 인용구 영속성을 위한 출력 포트
 * 도메인 모델과 영속성 계층 사이의 인터페이스를 정의합니다.
 */
public interface QuotePort {
    /**
     * 인용구를 저장합니다.
     *
     * @param quote 저장할 인용구
     * @return 저장된 인용구
     */
    Quote save(Quote quote);

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
     * 인용구를 삭제합니다.
     *
     * @param id 삭제할 인용구의 ID
     */
    void deleteById(QuoteId id);

    /**
     * 책의 인용구 수를 조회합니다.
     *
     * @param bookId 책 ID
     * @return 인용구 수
     */
    long countByBookId(BookId bookId);
} 