package com.j30n.stoblyx.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.j30n.stoblyx.domain.model.common.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private final List<Search> searches = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private final List<Quote> quotes = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private final List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private final List<Like> likes = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private final List<SavedQuote> savedQuotes = new ArrayList<>();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    @Size(max = 50)
    @Column(unique = true)
    private String username;

    @JsonIgnore
    @NotEmpty
    @Size(max = 100)
    private String password;

    @NotEmpty
    @Size(max = 50)
    private String nickname;

    @Email
    @Size(max = 100)
    @Column(unique = true)
    private String email;

    @Size(max = 255)
    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    private UserRole role = UserRole.USER;

    @Column(length = 20)
    private String accountStatus = "ACTIVE";
    private LocalDateTime lastLoginAt;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Auth auth;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private UserInterest userInterest;

    @Builder
    public User(String username, String password, String nickname, String email, String profileImageUrl, UserRole role) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.email = email;
        this.profileImageUrl = profileImageUrl;
        this.role = (role != null) ? role : UserRole.USER;
        this.lastLoginAt = LocalDateTime.now();
    }

    /**
     * 사용자 프로필을 업데이트합니다.
     */
    public void updateProfile(String nickname, String profileImageUrl) {
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
    }

    /**
     * 이메일을 업데이트합니다.
     */
    public void updateEmail(String email) {
        this.email = email;
    }

    /**
     * 비밀번호를 업데이트합니다.
     */
    public void updatePassword(String password) {
        this.password = password;
    }

    /**
     * 역할을 업데이트합니다.
     */
    public void updateRole(UserRole role) {
        this.role = role;
    }

    /**
     * 계정 상태를 업데이트합니다.
     */
    public void updateStatus(String accountStatus) {
        this.accountStatus = accountStatus;
    }

    /**
     * 마지막 로그인 시간을 업데이트합니다.
     */
    public void updateLastLoginAt() {
        this.lastLoginAt = LocalDateTime.now();
    }

    public boolean isOwner(Long userId) {
        return this.id.equals(userId);
    }

    public String getProfileImage() {
        return getProfileImageUrl();
    }

    /**
     * 사용자 프로필 이미지를 업데이트합니다.
     *
     * @param profileImageUrl 새 프로필 이미지 URL
     */
    public void updateProfileImage(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
} 