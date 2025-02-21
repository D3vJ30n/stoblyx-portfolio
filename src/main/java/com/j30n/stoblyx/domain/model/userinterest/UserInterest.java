package com.j30n.stoblyx.domain.model.userinterest;

import com.j30n.stoblyx.domain.base.BaseEntity;
import com.j30n.stoblyx.domain.model.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Table(
    name = "user_interests",
    indexes = {
        @Index(name = "idx_user_interest_user_id", columnList = "user_id")
    }
)
@Getter
@NoArgsConstructor(access = PROTECTED)
public class UserInterest extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("사용자 관심사 고유 식별자")
    private Long id;

    @Column(columnDefinition = "TEXT")
    @Comment("최근 검색어 목록 (JSON 형식)")
    private String recentSearches;

    @Column(columnDefinition = "TEXT")
    @Comment("선호 장르 (JSON 형식)")
    private String favoriteGenres;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @Comment("관심사를 가진 사용자")
    private User user;

    // Builder pattern for immutable object creation
    @Builder
    private UserInterest(String recentSearches, String favoriteGenres, User user) {
        this.recentSearches = recentSearches;
        this.favoriteGenres = favoriteGenres;
        this.user = user;
    }

    // Business methods
    public void setUser(User user) {
        this.user = user;
    }

    public void updateRecentSearches(String recentSearches) {
        this.recentSearches = recentSearches;
    }

    public void updateFavoriteGenres(String favoriteGenres) {
        this.favoriteGenres = favoriteGenres;
    }
} 