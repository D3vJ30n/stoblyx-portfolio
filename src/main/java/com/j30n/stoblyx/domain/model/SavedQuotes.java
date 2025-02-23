package com.j30n.stoblyx.domain.model;

import com.j30n.stoblyx.domain.model.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "saved_quotes",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "quote_id"})
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SavedQuotes extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quote_id")
    private Quote quote;

    private String note;

    @Builder
    public SavedQuotes(User user, Quote quote, String note) {
        this.user = user;
        this.quote = quote;
        this.note = note;
    }

    public void updateNote(String note) {
        this.note = note;
    }
} 