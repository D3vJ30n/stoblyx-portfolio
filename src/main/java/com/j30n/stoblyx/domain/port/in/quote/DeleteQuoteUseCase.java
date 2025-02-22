package com.j30n.stoblyx.domain.port.in.quote;

import com.j30n.stoblyx.domain.model.quote.QuoteId;
import com.j30n.stoblyx.domain.model.user.User;

/**
 * 인용구 삭제를 위한 입력 포트
 */
public interface DeleteQuoteUseCase {
    /**
     * 인용구를 삭제합니다.
     *
     * @param id 삭제할 인용구의 ID
     * @param user 삭제 요청자
     * @throws IllegalArgumentException 인용구가 존재하지 않거나 삭제 권한이 없는 경우
     * @throws IllegalStateException 이미 삭제된 인용구인 경우
     */
    void deleteQuote(QuoteId id, User user);
} 