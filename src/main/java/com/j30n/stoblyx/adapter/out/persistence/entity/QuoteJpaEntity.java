package com.j30n.stoblyx.adapter.out.persistence.entity;

import com.j30n.stoblyx.domain.base.BaseTimeEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Comment;

/**
 * 문구 정보를 저장하는 JPA 엔티티
 * quotes 테이블과 매핑됩니다.
 */
@Entity
@Table(
    name = "quotes",
    indexes = {
        @Index(name = "idx_quote_book_id", columnList = "book_id"),
        @Index(name = "idx_quote_user_id", columnList = "user_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
public class QuoteJpaEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("문구 고유 식별자")
    private Long id;

    @NotBlank
    @Column(columnDefinition = "TEXT", nullable = false)
    @Comment("문구 내용")
    private String content;

    @Column(length = 20)
    @Comment("페이지 번호")
    private String page;

    @Column(length = 50)
    @Comment("챕터 정보")
    private String chapter;

    @Column(nullable = false)
    @Comment("좋아요 수")
    private int likeCount = 0;

    @Column(nullable = false)
    @Comment("저장 횟수")
    private int saveCount = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    @Comment("문구가 속한 책")
    private BookJpaEntity book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @Comment("문구를 등록한 사용자")
    private UserJpaEntity user;
} 