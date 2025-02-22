package com.j30n.stoblyx.domain.model.quote;

import com.j30n.stoblyx.domain.base.BaseEntity;
import com.j30n.stoblyx.domain.model.book.BookId;
import com.j30n.stoblyx.domain.model.comment.Comment;
import com.j30n.stoblyx.domain.model.like.Like;
import com.j30n.stoblyx.domain.model.user.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 인용구 도메인 모델
 * 책에서 발췌한 인용구를 나타냅니다.
 */
@Getter
public class Quote extends BaseEntity {
    private final List<Comment> comments = new ArrayList<>();
    private final List<Like> likes = new ArrayList<>();
    private final User user;
    private final BookId bookId;
    private QuoteId id;
    private Content content;
    private Page page;
    private boolean isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private Book book;

    /**
     * 인용구 객체 생성을 위한 빌더 패턴
     * 생성 후 불변성을 보장하기 위해 private으로 선언
     */
    @Builder
    private Quote(User user, BookId bookId, Content content, Page page) {
        this.user = Objects.requireNonNull(user, "사용자는 null일 수 없습니다");
        this.bookId = Objects.requireNonNull(bookId, "책 ID는 null일 수 없습니다");
        this.content = Objects.requireNonNull(content, "인용구 내용은 null일 수 없습니다");
        this.page = Objects.requireNonNull(page, "페이지 정보는 null일 수 없습니다");
        this.isDeleted = false;
        this.createdAt = LocalDateTime.now();
        this.modifiedAt = LocalDateTime.now();
    }

    /**
     * ID로 인용구를 생성하는 정적 팩토리 메서드
     */
    public static Quote withId(Long id) {
        Quote quote = Quote.builder()
            .user(User.withId(1L)) // 임시 사용자
            .bookId(new BookId(1L)) // 임시 책 ID
            .content(new Content("임시 내용"))
            .page(new Page(1))
            .build();
        quote.setId(new QuoteId(id));
        return quote;
    }

    /**
     * ID 설정을 위한 메서드
     */
    public void setId(QuoteId id) {
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
     * 인용구 내용 수정
     */
    public void updateContent(Content content) {
        validateNotDeleted();
        this.content = Objects.requireNonNull(content, "인용구 내용은 null일 수 없습니다");
        this.modifiedAt = LocalDateTime.now();
    }

    /**
     * 페이지 정보 수정
     */
    public void updatePage(Page page) {
        validateNotDeleted();
        this.page = Objects.requireNonNull(page, "페이지 정보는 null일 수 없습니다");
        this.modifiedAt = LocalDateTime.now();
    }

    /**
     * 인용구 삭제
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
     * 댓글 목록 조회
     */
    public List<Comment> getComments() {
        return Collections.unmodifiableList(comments);
    }

    /**
     * 좋아요 목록 조회
     */
    public List<Like> getLikes() {
        return Collections.unmodifiableList(likes);
    }

    /**
     * 삭제된 인용구 검증
     */
    private void validateNotDeleted() {
        if (isDeleted) {
            throw new IllegalStateException("삭제된 인용구는 수정할 수 없습니다");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Quote quote)) return false;
        return Objects.equals(id, quote.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public void setUser(User user) {
    }

    public void setBook(Book book) {
        this.book = Objects.requireNonNull(book, "책 정보는 null일 수 없습니다");
    }

    public Book getBook() {
        return Objects.requireNonNull(book, "책 정보가 설정되지 않았습니다");
    }
}