package com.j30n.stoblyx.application.service.comment;

import com.j30n.stoblyx.adapter.in.web.dto.comment.CommentCreateRequest;
import com.j30n.stoblyx.adapter.in.web.dto.comment.CommentResponse;
import com.j30n.stoblyx.adapter.in.web.dto.comment.CommentUpdateRequest;
import com.j30n.stoblyx.application.port.in.comment.CommentUseCase;
import com.j30n.stoblyx.application.port.out.auth.AuthPort;
import com.j30n.stoblyx.application.port.out.comment.CommentPort;
import com.j30n.stoblyx.application.port.out.quote.QuotePort;
import com.j30n.stoblyx.domain.model.Comment;
import com.j30n.stoblyx.domain.model.Quote;
import com.j30n.stoblyx.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService implements CommentUseCase {

    private final CommentPort commentPort;
    private final AuthPort authPort;
    private final QuotePort quotePort;

    @Override
    @Transactional
    public CommentResponse createComment(Long quoteId, CommentCreateRequest request, Long userId) {
        User user = findUserById(userId);
        Quote quote = findQuoteById(quoteId);

        Comment comment = Comment.builder()
            .content(request.content())
            .user(user)
            .quote(quote)
            .build();

        return CommentResponse.from(commentPort.saveComment(comment));
    }

    @Transactional(readOnly = true)
    public CommentResponse getComment(Long id) {
        return CommentResponse.from(findCommentById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CommentResponse> getCommentsByQuote(Long quoteId, Pageable pageable) {
        return commentPort.findCommentsByQuoteId(quoteId, pageable)
            .map(CommentResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CommentResponse> getCommentsByUser(Long userId, Pageable pageable) {
        return commentPort.findCommentsByUserId(userId, pageable)
            .map(CommentResponse::from);
    }

    @Override
    @Transactional
    public CommentResponse updateComment(Long commentId, CommentUpdateRequest request, Long userId) {
        Comment comment = findCommentById(commentId);
        validateCommentOwner(comment, userId);

        comment.update(request.content());
        return CommentResponse.from(commentPort.saveComment(comment));
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = findCommentById(commentId);
        validateCommentOwner(comment, userId);

        commentPort.deleteComment(comment);
    }

    private Comment findCommentById(Long id) {
        return commentPort.findCommentById(id)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다. id: " + id));
    }

    private User findUserById(Long id) {
        return authPort.findUserById(id)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다. id: " + id));
    }

    private Quote findQuoteById(Long id) {
        return quotePort.findQuoteById(id)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 문구입니다. id: " + id));
    }

    private void validateCommentOwner(Comment comment, Long userId) {
        if (!comment.isOwner(userId)) {
            throw new IllegalArgumentException("댓글의 소유자가 아닙니다.");
        }
    }
}
