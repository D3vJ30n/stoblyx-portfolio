package com.j30n.stoblyx.domain.model;

import com.j30n.stoblyx.domain.model.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

/**
 * 사용자가 저장한 인용구 정보를 저장하는 엔티티
 */
@Entity
@Table(name = "saved_quotes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(SavedQuote.SavedQuoteId.class)
public class SavedQuote extends BaseEntity {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Id
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

    /**
     * SavedQuote 엔티티의 복합키 클래스
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SavedQuoteId implements Serializable {
        private Long user;
        private Long quote;
    }
} 