package com.j30n.stoblyx.domain.model;

import com.j30n.stoblyx.domain.model.common.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 인용구의 AI 기반 요약 정보를 저장하는 엔티티
 */
@Entity
@Table(name = "quote_summaries")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QuoteSummary extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    @Column(length = 1000, nullable = false)
    private String content;

    @Column(length = 50)
    private String algorithm;

    @Column(nullable = false)
    private LocalDateTime generatedAt;

    @Column
    private Double quality = 0.0;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quote_id", nullable = false)
    private Quote quote;

    @Builder
    public QuoteSummary(String content, String algorithm, Quote quote, Double quality) {
        this.content = content;
        this.algorithm = algorithm;
        this.quote = quote;
        this.quality = quality != null ? quality : 0.0;
        this.generatedAt = LocalDateTime.now();
    }

    /**
     * 요약 품질 점수를 업데이트합니다.
     */
    public void updateQuality(Double quality) {
        this.quality = quality;
    }

    /**
     * 요약 내용을 업데이트합니다.
     */
    public void updateContent(String content, String algorithm) {
        this.content = content;
        this.algorithm = algorithm;
        this.generatedAt = LocalDateTime.now();
    }
} 