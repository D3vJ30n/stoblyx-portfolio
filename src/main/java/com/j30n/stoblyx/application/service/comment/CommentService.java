package com.j30n.stoblyx.application.service.comment;

import com.j30n.stoblyx.application.dto.comment.CommentDto;
import com.j30n.stoblyx.domain.model.book.BookId;
import com.j30n.stoblyx.domain.model.comment.Comment;
import com.j30n.stoblyx.domain.model.comment.CommentId;
import com.j30n.stoblyx.domain.model.comment.Content;
import com.j30n.stoblyx.domain.model.quote.Quote;
import com.j30n.stoblyx.domain.model.user.User;
import com.j30n.stoblyx.domain.port.in.comment.CreateCommentUseCase;
import com.j30n.stoblyx.domain.port.in.comment.DeleteCommentUseCase;
import com.j30n.stoblyx.domain.port.in.comment.FindCommentUseCase;
import com.j30n.stoblyx.domain.port.in.comment.UpdateCommentUseCase;
import com.j30n.stoblyx.domain.port.out.comment.CommentPort;
import com.j30n.stoblyx.domain.port.in.quote.FindQuoteUseCase;
import com.j30n.stoblyx.domain.port.in.user.FindUserUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 댓글 서비스
 * 댓글 관련 유스케이스를 구현합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService implements CreateCommentUseCase, UpdateCommentUseCase,
    DeleteCommentUseCase, FindCommentUseCase {

    private final CommentPort commentPort;
    private final FindQuoteUseCase findQuoteUseCase;
    private final FindUserUseCase findUserUseCase;

    @Override
    @Transactional
    public CommentDto.Responses.CommentDetail createComment(CommentDto.Commands.Create command) {
        // 인용구 조회
        var quote = findQuoteUseCase.findById(new QuoteId(command.quoteId()))
            .orElseThrow(() -> new IllegalArgumentException("인용구를 찾을 수 없습니다: " + command.quoteId()));

        // 사용자 조회
        var user = findUserUseCase.findById(command.userId())
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + command.userId()));

        // 댓글 생성
        var comment = Comment.createComment(
            new Content(command.content()),
            quote,
            user,
            new BookId(command.bookId())
        );

        // 저장 및 응답 반환
        var savedComment = commentPort.save(comment);
        return CommentDto.Responses.CommentDetail.from(savedComment);
    }

    @Override
    @Transactional
    public CommentDto.Responses.CommentDetail createReply(CommentDto.Commands.CreateReply command) {
        // 부모 댓글 조회
        var parentComment = commentPort.findById(new CommentId(command.parentId()))
            .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다: " + command.parentId()));

        // 사용자 조회
        var user = findUserUseCase.findById(command.userId())
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + command.userId()));

        // 답글 생성
        var reply = parentComment.createReply(new Content(command.content()), user);

        // 저장 및 응답 반환
        var savedReply = commentPort.save(reply);
        return CommentDto.Responses.CommentDetail.from(savedReply);
    }

    @Override
    @Transactional
    public CommentDto.Responses.CommentDetail updateContent(CommentDto.Commands.Update command) {
        // 댓글 조회
        var comment = commentPort.findById(new CommentId(command.commentId()))
            .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다: " + command.commentId()));

        // 권한 확인
        if (!comment.isAuthor(User.withId(command.userId()))) {
            throw new IllegalArgumentException("댓글을 수정할 권한이 없습니다");
        }

        // 내용 수정
        comment.updateContent(new Content(command.content()));

        // 저장 및 응답 반환
        var updatedComment = commentPort.save(comment);
        return CommentDto.Responses.CommentDetail.from(updatedComment);
    }

    @Override
    @Transactional
    public void deleteComment(CommentDto.Commands.Delete command) {
        // 댓글 조회
        var comment = commentPort.findById(new CommentId(command.commentId()))
            .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다: " + command.commentId()));

        // 권한 확인
        if (!comment.isAuthor(User.withId(command.userId()))) {
            throw new IllegalArgumentException("댓글을 삭제할 권한이 없습니다");
        }

        // 삭제 처리
        comment.delete();
        commentPort.save(comment);
    }

    @Override
    public CommentDto.Responses.CommentDetail findById(CommentDto.Queries.FindById query) {
        return commentPort.findById(new CommentId(query.commentId()))
            .map(CommentDto.Responses.CommentDetail::from)
            .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다: " + query.commentId()));
    }

    @Override
    public List<CommentDto.Responses.CommentDetail> findByQuote(CommentDto.Queries.FindByQuote query) {
        // 인용구 조회
        var quote = findQuoteUseCase.findById(new QuoteId(query.quoteId()))
            .orElseThrow(() -> new IllegalArgumentException("인용구를 찾을 수 없습니다: " + query.quoteId()));

        // 댓글 목록 조회 및 변환
        return commentPort.findByQuote(quote).stream()
            .map(CommentDto.Responses.CommentDetail::from)
            .toList();
    }

    @Override
    public List<CommentDto.Responses.CommentDetail> findByUser(CommentDto.Queries.FindByUser query) {
        // 사용자 조회
        var user = findUserUseCase.findById(query.userId())
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + query.userId()));

        // 댓글 목록 조회 및 변환
        return commentPort.findByUser(user).stream()
            .map(CommentDto.Responses.CommentDetail::from)
            .toList();
    }
} 