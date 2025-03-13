package com.j30n.stoblyx.domain.model;

import com.j30n.stoblyx.domain.model.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @Column(name = "interests", columnDefinition = "TEXT")
    private String interests;

    @Builder
    public UserInterest(User user, String interests) {
        this.user = user;
        this.interests = interests != null ? interests : "";
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
                .interests("")
                .build();
    }

    /**
     * 관심사 정보를 업데이트합니다.
     *
     * @param interests 사용자 관심사 정보
     */
    public void updateInterests(String interests) {
        this.interests = interests != null ? interests : "";
    }
}