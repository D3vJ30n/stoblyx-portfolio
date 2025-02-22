package com.j30n.stoblyx.domain.model.comment.exception;

import com.j30n.stoblyx.common.exception.EntityNotFoundException;
import com.j30n.stoblyx.domain.model.comment.CommentId;

/**
 * 댓글을 찾을 수 없을 때 발생하는 예외
 */
public class CommentNotFoundException extends EntityNotFoundException {
    public CommentNotFoundException(CommentId commentId) {
        super("댓글", commentId.getValue());
    }

    public CommentNotFoundException(String message) {
        super(message);
    }

    public CommentNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
} 