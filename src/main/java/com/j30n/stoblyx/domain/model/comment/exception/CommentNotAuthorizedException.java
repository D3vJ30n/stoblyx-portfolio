package com.j30n.stoblyx.domain.model.comment.exception;

import com.j30n.stoblyx.common.exception.UnauthorizedException;

/**
 * 댓글에 대한 권한이 없을 때 발생하는 예외
 */
public class CommentNotAuthorizedException extends UnauthorizedException {
    public CommentNotAuthorizedException(String operation) {
        super("댓글", operation);
    }

    public CommentNotAuthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
} 