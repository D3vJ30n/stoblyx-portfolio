package com.j30n.stoblyx.domain.repository;

import com.j30n.stoblyx.domain.model.ContentComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContentCommentRepository extends JpaRepository<ContentComment, Long> {

    /**
     * 콘텐츠 ID로 삭제되지 않은 댓글 조회
     */
    Page<ContentComment> findByContent_IdAndIsDeletedFalse(Long contentId, Pageable pageable);

    /**
     * 사용자 ID로 삭제되지 않은 댓글 조회
     */
    Page<ContentComment> findByUser_IdAndIsDeletedFalse(Long userId, Pageable pageable);

    /**
     * 댓글 ID와 삭제되지 않음 조건으로 댓글 조회
     */
    Optional<ContentComment> findByIdAndIsDeletedFalse(Long id);

    /**
     * 부모 댓글 ID로 삭제되지 않은 대댓글 조회
     */
    List<ContentComment> findByParent_IdAndIsDeletedFalse(Long parentId);

    /**
     * 콘텐츠 ID와 사용자 ID로 댓글 존재 여부 확인
     */
    boolean existsByContent_IdAndUser_IdAndIsDeletedFalse(Long contentId, Long userId);

    /**
     * 댓글 ID와 사용자 ID로 댓글 존재 여부 확인
     */
    boolean existsByIdAndUser_IdAndIsDeletedFalse(Long id, Long userId);

    /**
     * 최상위 댓글(부모 댓글이 없는)만 조회
     */
    @Query("SELECT c FROM ContentComment c WHERE c.content.id = :contentId AND c.parent IS NULL AND c.isDeleted = false")
    Page<ContentComment> findTopLevelCommentsByContentId(@Param("contentId") Long contentId, Pageable pageable);

    /**
     * 콘텐츠의 댓글 수 조회
     */
    @Query("SELECT COUNT(c) FROM ContentComment c WHERE c.content.id = :contentId AND c.isDeleted = false")
    long countByContentId(@Param("contentId") Long contentId);
} 