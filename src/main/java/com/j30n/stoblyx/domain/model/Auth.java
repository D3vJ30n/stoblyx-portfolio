package com.j30n.stoblyx.domain.model;

import com.j30n.stoblyx.domain.model.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 사용자 인증 정보를 저장하는 엔티티
 */
@Entity
@Table(name = "auth")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Auth extends BaseEntity {

    @Column(length = 20, nullable = false)
    private final String tokenType = "Bearer";
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 255)
    private String refreshToken;
    @Column(nullable = false)
    private LocalDateTime expiryDate;

    private LocalDateTime lastUsedAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder
    public Auth(String refreshToken, LocalDateTime expiryDate, User user) {
        this.refreshToken = refreshToken;
        this.expiryDate = expiryDate;
        this.user = user;
        this.lastUsedAt = LocalDateTime.now();
    }

    /**
     * 리프레시 토큰을 업데이트합니다.
     */
    public void updateRefreshToken(String refreshToken, LocalDateTime expiryDate) {
        this.refreshToken = refreshToken;
        this.expiryDate = expiryDate;
        this.lastUsedAt = LocalDateTime.now();
    }

    /**
     * 마지막 사용 시간을 업데이트합니다.
     */
    public void updateLastUsedAt() {
        this.lastUsedAt = LocalDateTime.now();
    }

    /**
     * 리프레시 토큰이 만료되었는지 확인합니다.
     */
    public boolean isExpired() {
        return expiryDate.isBefore(LocalDateTime.now());
    }
} 