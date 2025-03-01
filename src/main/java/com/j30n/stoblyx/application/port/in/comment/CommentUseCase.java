package com.j30n.stoblyx.application.port.in.comment;

import com.j30n.stoblyx.adapter.in.web.dto.comment.CommentCreateRequest;
import com.j30n.stoblyx.adapter.in.web.dto.comment.CommentResponse;
import com.j30n.stoblyx.adapter.in.web.dto.comment.CommentUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 댓글 관련 유스케이스 인터페이스
 */
public interface CommentUseCase {

    /**
     * 댓글 생성
     *
     * @param quoteId 명언 ID
     * @param request 댓글 생성 요청 DTO
     * @param userId 작성자 ID
     * @return 생성된 댓글 응답 DTO
     */
    CommentResponse createComment(Long quoteId, CommentCreateRequest request, Long userId);

    /**
     * 댓글 수정
     *
     * @param commentId 댓글 ID
     * @param request 댓글 수정 요청 DTO
     * @param userId 수정자 ID
     * @return 수정된 댓글 응답 DTO
     */
    CommentResponse updateComment(Long commentId, CommentUpdateRequest request, Long userId);

    /**
     * 댓글 삭제
     *
     * @param commentId 댓글 ID
     * @param userId 삭제자 ID
     */
    void deleteComment(Long commentId, Long userId);

    /**
     * 명언별 댓글 목록 조회
     *
     * @param quoteId 명언 ID
     * @param pageable 페이지네이션 정보
     * @return 댓글 응답 DTO 페이지
     */
    Page<CommentResponse> getCommentsByQuote(Long quoteId, Pageable pageable);

    /**
     * 사용자별 댓글 목록 조회
     *
     * @param userId 사용자 ID
     * @param pageable 페이지네이션 정보
     * @return 댓글 응답 DTO 페이지
     */
    Page<CommentResponse> getCommentsByUser(Long userId, Pageable pageable);
}
