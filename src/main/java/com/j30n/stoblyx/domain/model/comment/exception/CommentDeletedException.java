package com.j30n.stoblyx.domain.model.comment.exception;

import org.springframework.http.HttpStatus;

/**
 * 삭제된 댓글에 대한 작업을 시도할 때 발생하는 예외
 */
public class CommentDeletedException extends CommentException {
    public CommentDeletedException(Long id) {
        super(
            String.format("이미 삭제된 댓글입니다 (ID: %d)", id),
            HttpStatus.BAD_REQUEST
        );
    }

    public CommentDeletedException(String message, Throwable cause) {
        super(message, HttpStatus.BAD_REQUEST, cause);
    }
} 