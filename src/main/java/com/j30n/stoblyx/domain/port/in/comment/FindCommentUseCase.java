package com.j30n.stoblyx.domain.port.in.comment;

import com.j30n.stoblyx.application.dto.comment.CommentDto;

import java.util.List;

/**
 * 댓글 조회를 위한 입력 포트
 */
public interface FindCommentUseCase {
    /**
     * ID로 댓글을 조회합니다.
     *
     * @param query 댓글 조회 정보
     * @return 조회된 댓글의 상세 정보
     * @throws IllegalArgumentException 유효하지 않은 입력값이거나 댓글이 존재하지 않는 경우
     */
    CommentDto.Responses.CommentDetail findById(CommentDto.Queries.FindById query);

    /**
     * 인용구에 달린 댓글 목록을 조회합니다.
     *
     * @param query 인용구별 댓글 조회 정보
     * @return 조회된 댓글 목록
     * @throws IllegalArgumentException 유효하지 않은 입력값이거나 인용구가 존재하지 않는 경우
     */
    List<CommentDto.Responses.CommentDetail> findByQuote(CommentDto.Queries.FindByQuote query);

    /**
     * 사용자가 작성한 댓글 목록을 조회합니다.
     *
     * @param query 사용자별 댓글 조회 정보
     * @return 조회된 댓글 목록
     * @throws IllegalArgumentException 유효하지 않은 입력값이거나 사용자가 존재하지 않는 경우
     */
    List<CommentDto.Responses.CommentDetail> findByUser(CommentDto.Queries.FindByUser query);
} 