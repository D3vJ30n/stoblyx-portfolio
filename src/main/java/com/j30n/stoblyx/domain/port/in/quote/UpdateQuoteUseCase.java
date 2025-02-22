package com.j30n.stoblyx.domain.port.in.quote;

import com.j30n.stoblyx.domain.model.quote.Content;
import com.j30n.stoblyx.domain.model.quote.Page;
import com.j30n.stoblyx.domain.model.quote.Quote;
import com.j30n.stoblyx.domain.model.quote.QuoteId;
import com.j30n.stoblyx.domain.model.user.User;

/**
 * 인용구 수정을 위한 입력 포트
 */
public interface UpdateQuoteUseCase {
    /**
     * 인용구 내용을 수정합니다.
     *
     * @param id 수정할 인용구의 ID
     * @param content 새로운 내용
     * @param user 수정 요청자
     * @return 수정된 인용구
     * @throws IllegalArgumentException 인용구가 존재하지 않거나 수정 권한이 없는 경우
     * @throws IllegalStateException 이미 삭제된 인용구인 경우
     */
    Quote updateContent(QuoteId id, Content content, User user);

    /**
     * 인용구의 페이지 정보를 수정합니다.
     *
     * @param id 수정할 인용구의 ID
     * @param page 새로운 페이지 정보
     * @param user 수정 요청자
     * @return 수정된 인용구
     * @throws IllegalArgumentException 인용구가 존재하지 않거나 수정 권한이 없는 경우
     * @throws IllegalStateException 이미 삭제된 인용구인 경우
     */
    Quote updatePage(QuoteId id, Page page, User user);
} 