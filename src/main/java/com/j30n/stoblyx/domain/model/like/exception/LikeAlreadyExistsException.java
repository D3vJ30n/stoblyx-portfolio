package com.j30n.stoblyx.domain.model.like.exception;

import com.j30n.stoblyx.common.exception.EntityAlreadyExistsException;
import com.j30n.stoblyx.domain.model.quote.Quote;
import com.j30n.stoblyx.domain.model.user.User;

/**
 * 이미 존재하는 좋아요를 생성하려고 할 때 발생하는 예외
 */
public class LikeAlreadyExistsException extends EntityAlreadyExistsException {
    public LikeAlreadyExistsException(Quote quote, User user) {
        super(String.format("이미 좋아요가 존재합니다 (인용구 ID: %d, 사용자 ID: %d)", 
            quote.getId().value(), user.getId()));
    }

    public LikeAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
} 