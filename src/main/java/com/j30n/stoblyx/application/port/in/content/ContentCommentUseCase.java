package com.j30n.stoblyx.application.port.in.content;

import com.j30n.stoblyx.adapter.in.web.dto.content.ContentCommentCreateRequest;
import com.j30n.stoblyx.adapter.in.web.dto.content.ContentCommentResponse;
import com.j30n.stoblyx.adapter.in.web.dto.content.ContentCommentUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 콘텐츠 댓글 관련 유스케이스 인터페이스
 */
public interface ContentCommentUseCase {

    /**
     * 콘텐츠 댓글 생성
     *
     * @param contentId 콘텐츠 ID
     * @param request 댓글 생성 요청 DTO
     * @param userId 작성자 ID
     * @return 생성된 댓글 응답 DTO
     */
    ContentCommentResponse createComment(Long contentId, ContentCommentCreateRequest request, Long userId);

    /**
     * 콘텐츠 댓글 수정
     *
     * @param commentId 댓글 ID
     * @param request 댓글 수정 요청 DTO
     * @param userId 수정자 ID
     * @return 수정된 댓글 응답 DTO
     */
    ContentCommentResponse updateComment(Long commentId, ContentCommentUpdateRequest request, Long userId);

    /**
     * 콘텐츠 댓글 삭제
     *
     * @param commentId 댓글 ID
     * @param userId 삭제자 ID
     */
    void deleteComment(Long commentId, Long userId);

    /**
     * 콘텐츠별 댓글 목록 조회 (최상위 댓글)
     *
     * @param contentId 콘텐츠 ID
     * @param pageable 페이지네이션 정보
     * @return 댓글 응답 DTO 페이지
     */
    Page<ContentCommentResponse> getTopLevelCommentsByContent(Long contentId, Pageable pageable);

    /**
     * 대댓글 목록 조회
     *
     * @param parentId 부모 댓글 ID
     * @return 대댓글 응답 DTO 목록
     */
    List<ContentCommentResponse> getRepliesByParentId(Long parentId);

    /**
     * 사용자별 댓글 목록 조회
     *
     * @param userId 사용자 ID
     * @param pageable 페이지네이션 정보
     * @return 댓글 응답 DTO 페이지
     */
    Page<ContentCommentResponse> getCommentsByUser(Long userId, Pageable pageable);
} 