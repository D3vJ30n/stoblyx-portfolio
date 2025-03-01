package com.j30n.stoblyx.application.service.content;

import com.j30n.stoblyx.adapter.in.web.dto.content.ContentCommentCreateRequest;
import com.j30n.stoblyx.adapter.in.web.dto.content.ContentCommentResponse;
import com.j30n.stoblyx.adapter.in.web.dto.content.ContentCommentUpdateRequest;
import com.j30n.stoblyx.application.port.in.content.ContentCommentUseCase;
import com.j30n.stoblyx.application.port.out.auth.AuthPort;
import com.j30n.stoblyx.application.port.out.content.ContentCommentPort;
import com.j30n.stoblyx.application.port.out.content.ContentPort;
import com.j30n.stoblyx.domain.model.ContentComment;
import com.j30n.stoblyx.domain.model.ShortFormContent;
import com.j30n.stoblyx.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 콘텐츠 댓글 서비스 구현체
 */
@Service
@RequiredArgsConstructor
public class ContentCommentService implements ContentCommentUseCase {

    private final ContentCommentPort contentCommentPort;
    private final ContentPort contentPort;
    private final AuthPort authPort;

    @Override
    @Transactional
    public ContentCommentResponse createComment(Long contentId, ContentCommentCreateRequest request, Long userId) {
        User user = findUserById(userId);
        ShortFormContent content = findContentById(contentId);
        
        ContentComment parentComment = null;
        if (request.parentId() != null) {
            parentComment = findCommentById(request.parentId());
        }

        ContentComment comment = ContentComment.builder()
            .user(user)
            .content(content)
            .commentText(request.commentText())
            .parent(parentComment)
            .build();

        return ContentCommentResponse.from(contentCommentPort.saveComment(comment));
    }

    @Override
    @Transactional
    public ContentCommentResponse updateComment(Long commentId, ContentCommentUpdateRequest request, Long userId) {
        ContentComment comment = findCommentById(commentId);
        validateCommentOwner(comment, userId);

        comment.updateContent(request.commentText());
        return ContentCommentResponse.from(contentCommentPort.saveComment(comment));
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        ContentComment comment = findCommentById(commentId);
        validateCommentOwner(comment, userId);

        contentCommentPort.deleteComment(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ContentCommentResponse> getTopLevelCommentsByContent(Long contentId, Pageable pageable) {
        return contentCommentPort.findTopLevelComments(contentId, pageable)
            .map(comment -> {
                List<ContentComment> replies = contentCommentPort.findRepliesByParentId(comment.getId());
                return ContentCommentResponse.fromWithReplies(comment, replies);
            });
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContentCommentResponse> getRepliesByParentId(Long parentId) {
        return contentCommentPort.findRepliesByParentId(parentId)
            .stream()
            .map(ContentCommentResponse::from)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ContentCommentResponse> getCommentsByUser(Long userId, Pageable pageable) {
        return contentCommentPort.findCommentsByUserId(userId, pageable)
            .map(ContentCommentResponse::from);
    }

    /**
     * 댓글 ID로 댓글 조회
     */
    private ContentComment findCommentById(Long id) {
        return contentCommentPort.findCommentById(id)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다. id: " + id));
    }

    /**
     * 사용자 ID로 사용자 조회
     */
    private User findUserById(Long id) {
        return authPort.findUserById(id)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다. id: " + id));
    }

    /**
     * 콘텐츠 ID로 콘텐츠 조회
     */
    private ShortFormContent findContentById(Long id) {
        return contentPort.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 콘텐츠입니다. id: " + id));
    }

    /**
     * 댓글 소유자 확인
     */
    private void validateCommentOwner(ContentComment comment, Long userId) {
        if (!comment.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("댓글의 소유자가 아닙니다.");
        }
    }
} 