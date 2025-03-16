package com.j30n.stoblyx.domain.model;

import com.j30n.stoblyx.domain.model.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자가 저장한 인용구 정보를 저장하는 엔티티
 */
@Entity
@Table(
    name = "saved_quotes",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_saved_quotes", columnNames = {"user_id", "quote_id"})
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SavedQuote extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quote_id")
    private Quote quote;

    @Column(length = 255)
    private String note;

    @Builder
    public SavedQuote(User user, Quote quote, String note) {
        this.user = user;
        this.quote = quote;
        this.note = note;
    }

    /**
     * 노트를 업데이트합니다.
     */
    public void updateNote(String note) {
        this.note = note;
    }
} 