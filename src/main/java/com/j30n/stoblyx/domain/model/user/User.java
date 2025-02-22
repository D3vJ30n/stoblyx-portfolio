package com.j30n.stoblyx.domain.model.user;

import com.j30n.stoblyx.domain.base.BaseEntity;
import com.j30n.stoblyx.domain.model.comment.Comment;
import com.j30n.stoblyx.domain.model.like.Like;
import com.j30n.stoblyx.domain.model.quote.Quote;
import com.j30n.stoblyx.domain.model.savedquote.SavedQuote;
import com.j30n.stoblyx.domain.model.userinterest.UserInterest;
import com.j30n.stoblyx.domain.model.userreward.UserReward;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static lombok.AccessLevel.PROTECTED;

/**
 * 사용자 도메인 엔티티
 * 불변 객체로 구현되어 있으며, 모든 필드는 final로 선언됩니다.
 */
@Entity
@Table(
    name = "users",
    indexes = {
        @Index(name = "idx_user_email", columnList = "email", unique = true)
    }
)
@Getter
@NoArgsConstructor(access = PROTECTED)
public class User extends BaseEntity {

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private final List<Quote> quotes = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private final List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private final List<Like> likes = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private final List<SavedQuote> savedQuotes = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private final List<UserReward> rewards = new ArrayList<>();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @org.hibernate.annotations.Comment("사용자 고유 식별자")
    private Long id;

    @NotBlank
    @Email
    @Size(max = 100)
    @Column(nullable = false, unique = true)
    @org.hibernate.annotations.Comment("사용자 이메일")
    private String email;

    @NotBlank
    @Size(min = 8, max = 100)
    @Column(nullable = false)
    @org.hibernate.annotations.Comment("암호화된 비밀번호")
    private String password;

    @NotBlank
    @Size(max = 50)
    @Column(nullable = false)
    @org.hibernate.annotations.Comment("사용자 이름")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @org.hibernate.annotations.Comment("사용자 권한")
    private Role role;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private UserInterest userInterest;

    // Builder pattern for immutable object creation
    @Builder
    private User(Long id, String email, String password, String name, Role role) {
        this.id = id;  // id 필드 추가
        this.email = Objects.requireNonNull(email, "이메일은 null일 수 없습니다");
        this.password = Objects.requireNonNull(password, "비밀번호는 null일 수 없습니다");
        this.name = Objects.requireNonNull(name, "이름은 null일 수 없습니다");
        this.role = Objects.requireNonNull(role, "역할은 null일 수 없습니다");
    }

    /**
     * 일반 사용자 생성을 위한 정적 팩토리 메서드
     */
    public static User createUser(String email, String password, String name) {
        return User.builder()
            .email(email)
            .password(password)
            .name(name)
            .role(Role.USER)
            .build();
    }

    /**
     * 관리자 생성을 위한 정적 팩토리 메서드
     */
    public static User createAdmin(String email, String password, String name) {
        return User.builder()
            .email(email)
            .password(password)
            .name(name)
            .role(Role.ADMIN)
            .build();
    }

    public static User withId(Long userId) {
        return null;
    }

    // Business methods

    /**
     * 비밀번호 업데이트
     */
    void updatePassword(String newPassword) {
        this.password = Objects.requireNonNull(newPassword, "비밀번호는 null일 수 없습니다");
    }

    /**
     * 이름 업데이트
     */
    void updateName(String newName) {
        this.name = Objects.requireNonNull(newName, "이름은 null일 수 없습니다");
    }

    /**
     * 사용자 역할을 정의하는 열거형
     */
    void setRole(Role role) {
        this.role = Objects.requireNonNull(role, "역할은 null일 수 없습니다");
    }

    /**
     * 연관관계 편의 메서드 - 인용구 추가
     */
    public void addQuote(Quote quote) {
        Objects.requireNonNull(quote, "인용구는 null일 수 없습니다");
        this.quotes.add(quote);
        quote.setUser(this);
    }

    /**
     * 연관관계 편의 메서드 - 댓글 추가
     */
    public void addComment(Comment comment) {
        Objects.requireNonNull(comment, "댓글은 null일 수 없습니다");
        this.comments.add(comment);
        comment.setUser(this);
    }

    /**
     * 연관관계 편의 메서드 - 좋아요 추가
     */
    public void addLike(Like like) {
        Objects.requireNonNull(like, "좋아요는 null일 수 없습니다");
        this.likes.add(like);
        like.setUser(this);
    }

    /**
     * 연관관계 편의 메서드 - 저장된 인용구 추가
     */
    public void saveQuote(SavedQuote savedQuote) {
        Objects.requireNonNull(savedQuote, "저장된 인용구는 null일 수 없습니다");
        this.savedQuotes.add(savedQuote);
        savedQuote.setUser(this);
    }

    /**
     * 사용자 역할을 정의하는 열거형
     */
    public enum Role {
        USER,    // 일반 사용자
        ADMIN    // 관리자
    }
} 