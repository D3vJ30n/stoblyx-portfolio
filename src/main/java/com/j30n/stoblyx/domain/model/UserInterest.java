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
        name = "user_interest_topics",
        joinColumns = @JoinColumn(name = "user_interest_id")
    )
    @Column(name = "topic")
    private List<String> topics = new ArrayList<>();

    @Column(length = 500)
    private String bio;

    @Builder
    public UserInterest(User user, List<String> genres, List<String> topics, String bio) {
        this.user = user;
        this.genres = genres != null ? genres : new ArrayList<>();
        this.topics = topics != null ? topics : new ArrayList<>();
        this.bio = bio;
    }

    public void update(List<String> genres, List<String> topics, String bio) {
        this.genres = genres != null ? genres : this.genres;
        this.topics = topics != null ? topics : this.topics;
        this.bio = bio != null ? bio : this.bio;
    }
} 