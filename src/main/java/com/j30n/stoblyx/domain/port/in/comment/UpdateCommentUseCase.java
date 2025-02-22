package com.j30n.stoblyx.domain.port.in.comment;

import com.j30n.stoblyx.application.dto.comment.CommentDto;

/**
 * 댓글 수정을 위한 입력 포트
 */
public interface UpdateCommentUseCase {
    /**
     * 댓글 내용을 수정합니다.
     *
     * @param command 댓글 수정 정보
     * @return 수정된 댓글의 상세 정보
     * @throws IllegalArgumentException 유효하지 않은 입력값이거나 댓글이 존재하지 않는 경우
     */
    CommentDto.Responses.CommentDetail updateContent(CommentDto.Commands.Update command);
} 