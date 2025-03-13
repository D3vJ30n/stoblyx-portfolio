package com.j30n.stoblyx.adapter.out.persistence.content;

import com.j30n.stoblyx.application.port.out.content.ContentCommentPort;
import com.j30n.stoblyx.domain.model.ContentComment;
import com.j30n.stoblyx.domain.repository.ContentCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * 콘텐츠 댓글 영속성 어댑터
 */
@Component
@RequiredArgsConstructor
public class ContentCommentPersistenceAdapter implements ContentCommentPort {

    private final ContentCommentRepository contentCommentRepository;

    @Override
    public ContentComment saveComment(ContentComment comment) {
        return contentCommentRepository.save(comment);
    }

    @Override
    public Optional<ContentComment> findCommentById(Long commentId) {
        return contentCommentRepository.findByIdAndIsDeletedFalse(commentId);
    }

    @Override
    public Page<ContentComment> findCommentsByContentId(Long contentId, Pageable pageable) {
        return contentCommentRepository.findByShortFormContent_IdAndIsDeletedFalse(contentId, pageable);
    }

    @Override
    public Page<ContentComment> findCommentsByUserId(Long userId, Pageable pageable) {
        return contentCommentRepository.findByUser_IdAndIsDeletedFalse(userId, pageable);
    }

    @Override
    public void deleteComment(ContentComment comment) {
        comment.delete();
        contentCommentRepository.save(comment);
    }

    @Override
    public Page<ContentComment> findTopLevelComments(Long contentId, Pageable pageable) {
        return contentCommentRepository.findTopLevelCommentsByContentId(contentId, pageable);
    }

    @Override
    public List<ContentComment> findRepliesByParentId(Long parentId) {
        return List.of();
    }

    @Override
    public long countCommentsByContentId(Long contentId) {
        return contentCommentRepository.countByContentId(contentId);
    }

    @Override
    public boolean existsByIdAndUserId(Long id, Long userId) {
        return contentCommentRepository.existsByIdAndUser_IdAndIsDeletedFalse(id, userId);
    }
} 