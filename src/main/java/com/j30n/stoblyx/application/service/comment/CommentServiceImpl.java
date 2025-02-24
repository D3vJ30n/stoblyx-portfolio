package com.j30n.stoblyx.application.service.comment;

import com.j30n.stoblyx.adapter.in.web.dto.comment.CommentCreateRequest;
import com.j30n.stoblyx.adapter.in.web.dto.comment.CommentResponse;
import com.j30n.stoblyx.adapter.in.web.dto.comment.CommentUpdateRequest;
import com.j30n.stoblyx.domain.model.Comment;
import com.j30n.stoblyx.domain.model.Quote;
import com.j30n.stoblyx.domain.model.User;
import com.j30n.stoblyx.domain.repository.CommentRepository;
import com.j30n.stoblyx.domain.repository.QuoteRepository;
import com.j30n.stoblyx.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final QuoteRepository quoteRepository;

    @Override
    @Transactional
    public CommentResponse createComment(Long userId, Long quoteId, CommentCreateRequest request) {
        User user = findUserById(userId);
        Quote quote = findQuoteById(quoteId);

        Comment comment = Comment.builder()
            .content(request.content())
            .user(user)
            .quote(quote)
            .build();

        return CommentResponse.from(commentRepository.save(comment));
    }

    @Override
    @Transactional(readOnly = true)
    public CommentResponse getComment(Long id) {
        return CommentResponse.from(findCommentById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CommentResponse> getCommentsByQuote(Long quoteId, Pageable pageable) {
        return commentRepository.findByQuoteId(quoteId, pageable)
            .map(CommentResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CommentResponse> getCommentsByUser(Long userId, Pageable pageable) {
        return commentRepository.findByUserId(userId, pageable)
            .map(CommentResponse::from);
    }

    @Override
    @Transactional
    public CommentResponse updateComment(Long userId, Long commentId, CommentUpdateRequest request) {
        Comment comment = findCommentById(commentId);
        validateCommentOwner(comment, userId);

        comment.update(request.content());
        return CommentResponse.from(comment);
    }

    @Override
    @Transactional
    public void deleteComment(Long userId, Long commentId) {
        Comment comment = findCommentById(commentId);
        validateCommentOwner(comment, userId);

        comment.delete();
    }

    private Comment findCommentById(Long id) {
        return commentRepository.findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다. id: " + id));
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다. id: " + id));
    }

    private Quote findQuoteById(Long id) {
        return quoteRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 문구입니다. id: " + id));
    }

    private void validateCommentOwner(Comment comment, Long userId) {
        if (!comment.isOwner(userId)) {
            throw new IllegalArgumentException("댓글의 소유자가 아닙니다.");
        }
    }
} 