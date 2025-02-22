package com.j30n.stoblyx.domain.port.in.comment;

import com.j30n.stoblyx.application.dto.comment.CommentDto;

/**
 * 댓글 생성을 위한 입력 포트
 */
public interface CreateCommentUseCase {
    /**
     * 인용구에 새로운 댓글을 생성합니다.
     *
     * @param command 댓글 생성 정보
     * @return 생성된 댓글의 상세 정보
     * @throws IllegalArgumentException 유효하지 않은 입력값인 경우
     */
    CommentDto.Responses.CommentDetail createComment(CommentDto.Commands.Create command);

    /**
     * 댓글에 답글을 생성합니다.
     *
     * @param command 답글 생성 정보
     * @return 생성된 답글의 상세 정보
     * @throws IllegalArgumentException 유효하지 않은 입력값이거나 부모 댓글이 존재하지 않는 경우
     */
    CommentDto.Responses.CommentDetail createReply(CommentDto.Commands.CreateReply command);
} 