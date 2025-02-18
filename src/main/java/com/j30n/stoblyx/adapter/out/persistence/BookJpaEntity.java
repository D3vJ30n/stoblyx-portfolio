package com.stoblyx.adapter.out.persistence;

@Entity
@Table(name = "books")
@Getter
@Setter
@NoArgsConstructor
public class BookJpaEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("책 고유 식별자")
    private Long id;

    @Column(nullable = false, length = 255)
    @Comment("책 제목")
    private String title;

    @Column(nullable = false, length = 100)
    @Comment("저자")
    private String author;

    @Column(length = 100)
    @Comment("책 장르")
    private String genre;

    @Column
    @Comment("출판일")
    private LocalDate publishedAt;

    @OneToMany(mappedBy = "book")
    private List<QuoteJpaEntity> quotes = new ArrayList<>();
}