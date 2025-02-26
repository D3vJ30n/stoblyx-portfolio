package com.j30n.stoblyx.domain.model;

import com.j30n.stoblyx.domain.model.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user_interests")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserInterest extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ElementCollection
    @CollectionTable(
        name = "user_interest_genres",
        joinColumns = @JoinColumn(name = "user_interest_id")
    )
    @Column(name = "genre")
    private List<String> genres = new ArrayList<>();

    @ElementCollection
    @CollectionTable(
        name = "user_interest_authors",
        joinColumns = @JoinColumn(name = "user_interest_id")
    )
    @Column(name = "author")
    private List<String> authors = new ArrayList<>();

    @ElementCollection
    @CollectionTable(
        name = "user_interest_keywords",
        joinColumns = @JoinColumn(name = "user_interest_id")
    )
    @Column(name = "keyword")
    private List<String> keywords = new ArrayList<>();

    @Builder
    public UserInterest(User user, List<String> genres, List<String> authors, List<String> keywords) {
        this.user = user;
        this.genres = genres != null ? genres : new ArrayList<>();
        this.authors = authors != null ? authors : new ArrayList<>();
        this.keywords = keywords != null ? keywords : new ArrayList<>();
    }

    /**
     * 빈 관심사 정보를 생성합니다.
     *
     * @param user 사용자
     * @return 빈 관심사 정보
     */
    public static UserInterest createEmpty(User user) {
        return UserInterest.builder()
                .user(user)
                .genres(new ArrayList<>())
                .authors(new ArrayList<>())
                .keywords(new ArrayList<>())
                .build();
    }

    /**
     * 관심사 정보를 업데이트합니다.
     *
     * @param genres 장르 목록
     * @param authors 작가 목록
     * @param keywords 키워드 목록
     */
    public void updateInterests(List<String> genres, List<String> authors, List<String> keywords) {
        this.genres = genres != null ? genres : new ArrayList<>();
        this.authors = authors != null ? authors : new ArrayList<>();
        this.keywords = keywords != null ? keywords : new ArrayList<>();
    }
}