package com.j30n.stoblyx.adapter.out.persistence.comment;

import com.j30n.stoblyx.application.port.out.comment.CommentPort;
import com.j30n.stoblyx.domain.model.Comment;
import com.j30n.stoblyx.domain.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 댓글 영속성 어댑터
 */
@Component
@RequiredArgsConstructor
public class CommentAdapter implements CommentPort {

    private final CommentRepository commentRepository;

    @Override
    public Comment saveComment(Comment comment) {
        return commentRepository.save(comment);
    }

    @Override
    public Optional<Comment> findCommentById(Long commentId) {
        return commentRepository.findById(commentId);
    }

    @Override
    public void deleteComment(Comment comment) {
        comment.delete();
        commentRepository.save(comment);
    }

    @Override
    public Page<Comment> findCommentsByQuoteId(Long quoteId, Pageable pageable) {
        return commentRepository.findByQuoteId(quoteId, pageable);
    }

    @Override
    public Page<Comment> findCommentsByUserId(Long userId, Pageable pageable) {
        return commentRepository.findByUserId(userId, pageable);
    }

    @Override
    public boolean existsByIdAndUserId(Long id, Long userId) {
        return commentRepository.existsByIdAndUserId(id, userId);
    }
}
