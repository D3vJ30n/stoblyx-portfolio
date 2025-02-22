package com.j30n.stoblyx.domain.port.out.comment;

import com.j30n.stoblyx.domain.model.comment.Comment;
import com.j30n.stoblyx.domain.model.comment.CommentId;
import com.j30n.stoblyx.domain.model.quote.Quote;
import com.j30n.stoblyx.domain.model.user.User;

import java.util.List;
import java.util.Optional;

/**
 * 댓글 영속성을 위한 출력 포트
 * 도메인 모델과 영속성 계층 사이의 인터페이스를 정의합니다.
 */
public interface CommentPort {
    /**
     * 댓글을 저장합니다.
     *
     * @param comment 저장할 댓글
     * @return 저장된 댓글
     */
    Comment save(Comment comment);

    /**
     * ID로 댓글을 조회합니다.
     *
     * @param id 댓글 ID
     * @return 조회된 댓글 (Optional)
     */
    Optional<Comment> findById(CommentId id);

    /**
     * 인용구에 달린 댓글 목록을 조회합니다.
     *
     * @param quote 인용구
     * @return 댓글 목록
     */
    List<Comment> findByQuote(Quote quote);

    /**
     * 사용자가 작성한 댓글 목록을 조회합니다.
     *
     * @param user 사용자
     * @return 댓글 목록
     */
    List<Comment> findByUser(User user);

    /**
     * 댓글을 삭제합니다.
     *
     * @param id 삭제할 댓글의 ID
     */
    void deleteById(CommentId id);

    /**
     * 인용구에 달린 댓글 수를 조회합니다.
     *
     * @param quote 인용구
     * @return 댓글 수
     */
    long countByQuote(Quote quote);
} 