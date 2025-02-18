package com.j30n.stoblyx.domain.user;

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
import org.hibernate.annotations.Comment;

import java.util.ArrayList;
import java.util.List;

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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("사용자 고유 식별자")
    private Long id;

    @NotBlank
    @Email
    @Size(max = 100)
    @Column(nullable = false, unique = true)
    @Comment("사용자 이메일")
    private String email;

    @NotBlank
    @Size(min = 8, max = 100)
    @Column(nullable = false)
    @Comment("암호화된 비밀번호")
    private String password;

    @NotBlank
    @Size(max = 50)
    @Column(nullable = false)
    @Comment("사용자 이름")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Comment("사용자 권한")
    private Role role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private final List<Quote> quotes = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private final List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private final List<Like> likes = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private final List<SavedQuote> savedQuotes = new ArrayList<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private UserInterest userInterest;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private final List<UserReward> rewards = new ArrayList<>();

    // Builder pattern for immutable object creation
    @Builder
    private User(String email, String password, String name, Role role) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.role = role;
    }

    // Business methods
    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }

    public void updateName(String newName) {
        this.name = newName;
    }

    public void addQuote(Quote quote) {
        this.quotes.add(quote);
        quote.setUser(this);
    }

    public void addComment(Comment comment) {
        this.comments.add(comment);
        comment.setUser(this);
    }

    public void addLike(Like like) {
        this.likes.add(like);
        like.setUser(this);
    }

    public void saveQuote(SavedQuote savedQuote) {
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