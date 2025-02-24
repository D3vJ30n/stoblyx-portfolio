package com.j30n.stoblyx.application.service.comment;

import com.j30n.stoblyx.adapter.in.web.dto.comment.CommentCreateRequest;
import com.j30n.stoblyx.adapter.in.web.dto.comment.CommentResponse;
import com.j30n.stoblyx.adapter.in.web.dto.comment.CommentUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentService {
    CommentResponse createComment(Long userId, Long quoteId, CommentCreateRequest request);

    CommentResponse getComment(Long id);

    Page<CommentResponse> getCommentsByQuote(Long quoteId, Pageable pageable);

    Page<CommentResponse> getCommentsByUser(Long userId, Pageable pageable);

    CommentResponse updateComment(Long userId, Long commentId, CommentUpdateRequest request);

    void deleteComment(Long userId, Long commentId);
} 