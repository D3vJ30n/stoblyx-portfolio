package com.j30n.stoblyx.domain.port.in.like;

import com.j30n.stoblyx.domain.model.like.Like;
import com.j30n.stoblyx.domain.model.quote.Quote;
import com.j30n.stoblyx.domain.model.user.User;

/**
 * 좋아요 토글을 위한 입력 포트
 */
public interface ToggleLikeUseCase {
    /**
     * 인용구에 대한 좋아요를 토글합니다.
     * 좋아요가 없으면 생성하고, 있으면 활성화/비활성화를 전환합니다.
     *
     * @param quote 인용구
     * @param user 사용자
     * @return 토글된 좋아요
     */
    Like toggleLike(Quote quote, User user);
} 