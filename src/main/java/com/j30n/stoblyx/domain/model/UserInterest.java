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
    @CollectionTable(name = "user_favorite_genres", joinColumns = @JoinColumn(name = "user_interest_id"))
    @Column(name = "genre")
    private List<String> favoriteGenres = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "user_favorite_authors", joinColumns = @JoinColumn(name = "user_interest_id"))
    @Column(name = "author")
    private List<String> favoriteAuthors = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "user_favorite_topics", joinColumns = @JoinColumn(name = "user_interest_id"))
    @Column(name = "topic")
    private List<String> favoriteTopics = new ArrayList<>();

    @Builder
    public UserInterest(User user) {
        this.user = user;
    }

    public void updateFavoriteGenres(List<String> genres) {
        this.favoriteGenres.clear();
        this.favoriteGenres.addAll(genres);
    }

    public void updateFavoriteAuthors(List<String> authors) {
        this.favoriteAuthors.clear();
        this.favoriteAuthors.addAll(authors);
    }

    public void updateFavoriteTopics(List<String> topics) {
        this.favoriteTopics.clear();
        this.favoriteTopics.addAll(topics);
    }
} 