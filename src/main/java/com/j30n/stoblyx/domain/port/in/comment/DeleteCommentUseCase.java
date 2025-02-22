package com.j30n.stoblyx.domain.port.in.comment;

import com.j30n.stoblyx.application.dto.comment.CommentDto;

/**
 * 댓글 삭제를 위한 입력 포트
 */
public interface DeleteCommentUseCase {
    /**
     * 댓글을 삭제합니다.
     *
     * @param command 댓글 삭제 정보
     * @throws IllegalArgumentException 유효하지 않은 입력값이거나 댓글이 존재하지 않는 경우
     */
    void deleteComment(CommentDto.Commands.Delete command);
} 