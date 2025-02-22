package com.j30n.stoblyx.domain.model.comment;

import com.j30n.stoblyx.domain.model.book.BookId;
import com.j30n.stoblyx.domain.model.quote.Quote;
import com.j30n.stoblyx.domain.model.user.User;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 댓글 도메인 모델
 * 책에 대한 사용자의 댓글을 나타냅니다.
 */
@Getter
public class Comment {
    private final List<Comment> replies = new ArrayList<>();
    private final User user;
    private final Quote quote;
    private final Comment parent;
    private final BookId bookId;
    private CommentId id;
    private Content content;
    private boolean isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    /**
     * 댓글 객체 생성을 위한 생성자
     * 생성 후 불변성을 보장하기 위해 private으로 선언
     */
    private Comment(User user, Quote quote, Content content, Comment parent, BookId bookId) {
        this.user = Objects.requireNonNull(user, "사용자는 null일 수 없습니다");
        this.quote = Objects.requireNonNull(quote, "인용구는 null일 수 없습니다");
        this.content = Objects.requireNonNull(content, "댓글 내용은 null일 수 없습니다");
        this.parent = parent;
        this.bookId = Objects.requireNonNull(bookId, "책 ID는 null일 수 없습니다");
        this.isDeleted = false;
        this.createdAt = LocalDateTime.now();
        this.modifiedAt = LocalDateTime.now();
    }

    /**
     * 인용구에 댓글을 생성하는 정적 팩토리 메서드
     */
    public static Comment createComment(Content content, Quote quote, User user, BookId bookId) {
        Comment comment = new Comment(user, quote, content, null, bookId);
        quote.getComments().add(comment);
        return comment;
    }

    /**
     * ID 설정을 위한 메서드
     */
    public void setId(CommentId id) {
        this.id = id;
    }

    /**
     * 시간 정보 설정을 위한 메서드
     */
    public void setTimeInfo(LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.createdAt = Objects.requireNonNull(createdAt, "생성 시간은 null일 수 없습니다");
        this.modifiedAt = Objects.requireNonNull(modifiedAt, "수정 시간은 null일 수 없습니다");
    }

    /**
     * 댓글에 답글을 생성하는 정적 팩토리 메서드
     */
    public Comment createReply(Content content, User user) {
        Comment reply = new Comment(user, this.quote, content, this, this.bookId);
        this.replies.add(reply);
        return reply;
    }

    /**
     * 답글 목록 조회
     */
    public List<Comment> getReplies() {
        return Collections.unmodifiableList(replies);
    }

    /**
     * 댓글 내용 수정
     */
    public void updateContent(Content content) {
        validateNotDeleted();
        this.content = Objects.requireNonNull(content, "댓글 내용은 null일 수 없습니다");
        this.modifiedAt = LocalDateTime.now();
    }

    /**
     * 댓글 삭제
     */
    public void delete() {
        this.isDeleted = true;
        this.modifiedAt = LocalDateTime.now();
    }

    /**
     * 작성자 확인
     */
    public boolean isAuthor(User user) {
        return this.user.equals(user);
    }

    /**
     * 삭제 여부 확인
     */
    public boolean isDeleted() {
        return this.isDeleted;
    }

    /**
     * 삭제된 댓글 검증
     */
    private void validateNotDeleted() {
        if (isDeleted) {
            throw new IllegalStateException("삭제된 댓글은 수정할 수 없습니다");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Comment comment)) return false;
        return Objects.equals(id, comment.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public void setUser(User user) {
    }
}