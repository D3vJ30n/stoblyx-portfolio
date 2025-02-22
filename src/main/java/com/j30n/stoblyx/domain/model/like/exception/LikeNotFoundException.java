package com.j30n.stoblyx.domain.model.like.exception;

import com.j30n.stoblyx.common.exception.DomainException;
import com.j30n.stoblyx.common.exception.EntityNotFoundException;
import com.j30n.stoblyx.common.exception.UnauthorizedException;
import org.springframework.http.HttpStatus;
import com.j30n.stoblyx.domain.model.like.LikeId;

/**
 * 좋아요를 찾을 수 없을 때 발생하는 예외
 */
public class LikeNotFoundException extends EntityNotFoundException {
    public LikeNotFoundException(LikeId likeId) {
        super("좋아요를 찾을 수 없습니다: " + likeId.getValue());
    }

    public LikeNotFoundException(String message) {
        super(message);
    }

    public LikeNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}