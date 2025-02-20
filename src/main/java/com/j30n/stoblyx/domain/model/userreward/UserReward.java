package com.j30n.stoblyx.domain.model.userreward;

import com.j30n.stoblyx.domain.base.BaseEntity;
import com.j30n.stoblyx.domain.model.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Table(
    name = "user_rewards",
    indexes = {
        @Index(name = "idx_user_reward_user_id", columnList = "user_id")
    }
)
@Getter
@NoArgsConstructor(access = PROTECTED)
public class UserReward extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("보상 고유 식별자")
    private Long id;

    @Min(1)
    @Column(nullable = false)
    @Comment("사용자의 랭킹 위치")
    private Integer rank;

    @NotBlank
    @Size(max = 50)
    @Column(nullable = false)
    @Comment("지급된 보상 유형")
    private String rewardType;

    @Column(nullable = false)
    @Comment("보상 지급 여부")
    private boolean rewarded = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @Comment("보상을 받은 사용자")
    private User user;

    // Builder pattern for immutable object creation
    @Builder
    private UserReward(Integer rank, String rewardType, User user) {
        this.rank = rank;
        this.rewardType = rewardType;
        this.user = user;
        this.rewarded = false;
    }

    // Business methods
    public void setUser(User user) {
        this.user = user;
    }

    public void markAsRewarded() {
        this.rewarded = true;
    }

    public boolean isEligibleForReward() {
        return !this.rewarded && this.rank <= 50;
    }
} 