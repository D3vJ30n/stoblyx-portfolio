package com.j30n.stoblyx.application.port.out.content;

import com.j30n.stoblyx.domain.model.ContentComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * 콘텐츠 댓글 영속성 포트 인터페이스
 */
public interface ContentCommentPort {

    /**
     * 콘텐츠 댓글 저장
     *
     * @param comment 저장할 댓글
     * @return 저장된 댓글
     */
    ContentComment saveComment(ContentComment comment);

    /**
     * 콘텐츠 댓글 조회
     *
     * @param commentId 댓글 ID
     * @return 조회된 댓글
     */
    Optional<ContentComment> findCommentById(Long commentId);

    /**
     * 콘텐츠별 댓글 목록 조회
     *
     * @param contentId 콘텐츠 ID
     * @param pageable 페이지네이션 정보
     * @return 댓글 목록
     */
    Page<ContentComment> findCommentsByContentId(Long contentId, Pageable pageable);

    /**
     * 사용자별 댓글 목록 조회
     *
     * @param userId 사용자 ID
     * @param pageable 페이지네이션 정보
     * @return 댓글 목록
     */
    Page<ContentComment> findCommentsByUserId(Long userId, Pageable pageable);

    /**
     * 댓글 삭제
     *
     * @param comment 삭제할 댓글
     */
    void deleteComment(ContentComment comment);

    /**
     * 최상위 댓글 조회
     *
     * @param contentId 콘텐츠 ID
     * @param pageable 페이지네이션 정보
     * @return 최상위 댓글 목록
     */
    Page<ContentComment> findTopLevelComments(Long contentId, Pageable pageable);

    /**
     * 대댓글 조회
     *
     * @param parentId 부모 댓글 ID
     * @return 대댓글 목록
     */
    List<ContentComment> findRepliesByParentId(Long parentId);

    /**
     * 콘텐츠의 댓글 수 조회
     *
     * @param contentId 콘텐츠 ID
     * @return 댓글 수
     */
    long countCommentsByContentId(Long contentId);

    /**
     * 댓글이 특정 사용자의 것인지 확인
     *
     * @param id 댓글 ID
     * @param userId 사용자 ID
     * @return 사용자의 댓글이면 true, 아니면 false
     */
    boolean existsByIdAndUserId(Long id, Long userId);
} 