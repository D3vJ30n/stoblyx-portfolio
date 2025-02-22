package com.j30n.stoblyx.domain.model.like.exception;

import com.j30n.stoblyx.common.exception.UnauthorizedException;

/**
 * 좋아요에 대한 권한이 없을 때 발생하는 예외
 */
public class LikeNotAuthorizedException extends UnauthorizedException {
    public LikeNotAuthorizedException(String operation) {
        super(String.format("좋아요에 대한 %s 권한이 없습니다", operation));
    }

    public LikeNotAuthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
} 