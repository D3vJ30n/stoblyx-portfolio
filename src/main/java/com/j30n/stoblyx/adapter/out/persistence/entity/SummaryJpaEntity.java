package com.j30n.stoblyx.adapter.out.persistence.entity;

import com.j30n.stoblyx.domain.base.BaseTimeEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Comment;

/**
 * 책 요약 정보를 저장하는 JPA 엔티티
 * summaries 테이블과 매핑됩니다.
 */
@Entity
@Table(name = "summaries")
@Getter
@Setter
@NoArgsConstructor
public class SummaryJpaEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("요약 고유 식별자")
    private Long id;

    @NotBlank
    @Column(columnDefinition = "TEXT", nullable = false)
    @Comment("요약 내용")
    private String summaryText;

    @Column(nullable = false)
    @Comment("원본 텍스트 길이")
    private int originalLength;

    @Column(nullable = false)
    @Comment("요약된 텍스트 길이")
    private int summaryLength;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    @Comment("요약이 속한 책")
    private BookJpaEntity book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @Comment("요약을 생성한 사용자")
    private UserJpaEntity user;
} 