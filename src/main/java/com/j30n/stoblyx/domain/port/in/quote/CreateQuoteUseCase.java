package com.j30n.stoblyx.domain.port.in.quote;

import com.j30n.stoblyx.domain.model.book.BookId;
import com.j30n.stoblyx.domain.model.quote.Content;
import com.j30n.stoblyx.domain.model.quote.Page;
import com.j30n.stoblyx.domain.model.quote.Quote;
import com.j30n.stoblyx.domain.model.user.User;

/**
 * 인용구 생성을 위한 입력 포트
 */
public interface CreateQuoteUseCase {
    /**
     * 새로운 인용구를 생성합니다.
     *
     * @param content 인용구 내용
     * @param page 페이지 정보
     * @param bookId 책 ID
     * @param user 작성자
     * @return 생성된 인용구
     */
    Quote createQuote(Content content, Page page, BookId bookId, User user);
} 