package com.j30n.stoblyx.application.port.out.comment;

import com.j30n.stoblyx.domain.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * 댓글 영속성 포트 인터페이스
 */
public interface CommentPort {

    /**
     * 댓글 저장
     *
     * @param comment 저장할 댓글
     * @return 저장된 댓글
     */
    Comment saveComment(Comment comment);

    /**
     * 댓글 조회
     *
     * @param commentId 댓글 ID
     * @return 조회된 댓글
     */
    Optional<Comment> findCommentById(Long commentId);

    /**
     * 명언별 댓글 목록 조회
     *
     * @param quoteId 명언 ID
     * @param pageable 페이지네이션 정보
     * @return 댓글 목록
     */
    Page<Comment> findCommentsByQuoteId(Long quoteId, Pageable pageable);

    /**
     * 사용자별 댓글 목록 조회
     *
     * @param userId 사용자 ID
     * @param pageable 페이지네이션 정보
     * @return 댓글 목록
     */
    Page<Comment> findCommentsByUserId(Long userId, Pageable pageable);

    /**
     * 댓글 삭제
     *
     * @param comment 삭제할 댓글
     */
    void deleteComment(Comment comment);

    /**
     * 댓글이 특정 사용자의 것인지 확인
     *
     * @param id 댓글 ID
     * @param userId 사용자 ID
     * @return 사용자의 댓글이면 true, 아니면 false
     */
    boolean existsByIdAndUserId(Long id, Long userId);
}
