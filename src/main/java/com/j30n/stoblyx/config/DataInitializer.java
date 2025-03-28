package com.j30n.stoblyx.config;

import com.j30n.stoblyx.domain.enums.ContentStatus;
import com.j30n.stoblyx.domain.enums.ContentType;
import com.j30n.stoblyx.domain.model.*;
import com.j30n.stoblyx.domain.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * 애플리케이션 초기 데이터 설정
 * 개발 및 시연용 데이터를 생성합니다.
 */
@Configuration
@Slf4j
@RequiredArgsConstructor
public class DataInitializer {

    private final ObjectProvider<DataInitializer> selfProvider;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final QuoteRepository quoteRepository;
    private final ShortFormContentRepository contentRepository;
    private final ContentInteractionRepository interactionRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final ContentCommentRepository contentCommentRepository;
    private final PopularSearchTermRepository popularSearchTermRepository;
    private final SearchRepository searchRepository;
    private final PasswordEncoder passwordEncoder;
    private final Random random = new Random();

    // 랜덤 비밀번호 생성 메서드 추가
    private String generateRandomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            int index = random.nextInt(chars.length());
            sb.append(chars.charAt(index));
        }
        return sb.toString();
    }

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            if (userRepository.count() > 0) {
                log.info("데이터가 이미 초기화되어 있습니다. 초기화 작업을 건너뜁니다.");
                return;
            }

            log.info("샘플 데이터 초기화를 시작합니다...");

            try {
                DataInitializer self = selfProvider.getObject();
                self.initUsers();
                self.initBooks();
                self.initQuotes();
                self.initShortFormContents();
                self.initInteractions();
                self.initPopularSearchTerms();
                self.initSearchHistory();

                log.info("샘플 데이터 초기화가 완료되었습니다.");
            } catch (Exception e) {
                log.error("샘플 데이터 초기화 중 오류가 발생했습니다.", e);
            }
        };
    }

    @Transactional
    public void initUsers() {
        List<User> users = Arrays.asList(
            User.builder()
                .username("user1")
                .password(passwordEncoder.encode(generateRandomPassword()))
                .email("user1@example.com")
                .nickname("일반사용자")
                .role(UserRole.USER)
                .build(),
            User.builder()
                .username("user2")
                .password(passwordEncoder.encode(generateRandomPassword()))
                .email("user2@example.com")
                .nickname("독서광")
                .role(UserRole.USER)
                .build(),
            User.builder()
                .username("admin")
                .password(passwordEncoder.encode(generateRandomPassword()))
                .email("admin@example.com")
                .nickname("관리자")
                .role(UserRole.ADMIN)
                .build()
        );

        userRepository.saveAll(users);
        log.info("사용자 {} 명이 생성되었습니다.", users.size());
    }

    @Transactional
    public void initBooks() {
        // 기존 책 샘플이 있는 경우 생성하지 않음
        if (bookRepository.count() > 0) {
            log.info("책 데이터가 이미 존재합니다.");
            return;
        }

        // 테스트용 책 데이터 생성
        List<Book> books = new ArrayList<>();

        // ID가 1인 테스트용 책 (명시적으로 테스트용으로 표시)
        BookInfo testBookInfo = BookInfo.builder()
            .title("테스트용 책")
            .author("테스트 작가")
            .isbn("979-11-00000-00-1")
            .description("K6 테스트를 위한 테스트용 책입니다.")
            .publisher("테스트 출판사")
            .publishDate(LocalDate.of(2023, 1, 1))
            .thumbnailUrl("https://example.com/test-book.jpg")
            .genres(Arrays.asList("테스트", "자기계발"))
            .build();

        Book testBook = Book.builder()
            .bookInfo(testBookInfo)
            .build();
        books.add(testBook);

        // 추가 책 데이터 생성
        String[] titles = {
            "철학의 위안", "사피엔스", "소크라테스의 변명", "당신 인생의 이야기",
            "아몬드", "곰팡이의 정원", "체리새우: 비밀글입니다", "빛의 과거",
            "말의 품격", "지적 대화를 위한 넓고 얇은 지식"
        };

        String[] authors = {
            "알랭 드 보통", "유발 하라리", "플라톤", "테드 창",
            "손원평", "이불", "황영미", "은희경",
            "이기주", "채사장"
        };

        String[] publishers = {
            "철학연구원", "사피엔스출판사", "고전출판", "과학문학사",
            "창작사", "만화출판", "청소년문학사", "한국문학출판",
            "국어연구원", "지식출판사"
        };

        String[][] genresList = {
            {"철학", "에세이"}, {"역사", "인문"}, {"고전", "철학"}, {"SF", "소설"},
            {"소설", "한국문학"}, {"만화", "그래픽노블"}, {"청소년", "소설"}, {"소설", "한국문학"},
            {"에세이", "언어"}, {"인문", "교양"}
        };

        String[] descriptions = {
            "철학적 사고를 통해 현대인의 불안과 고통을 위로하는 책",
            "인류의 역사를 거시적 관점에서 바라본 문명사",
            "소크라테스가 재판에서 자신을 변호하는 내용을 담은 플라톤의 저서",
            "외계 생명체와의 접촉을 통해 인간 언어와 사고의 본질을 탐구하는 SF소설",
            "감정을 느끼지 못하는 소년이 타인의 감정을 이해하게 되는 과정을 그린 소설",
            "독특한 세계관을 가진 판타지 만화",
            "비밀일기를 통해 펼쳐지는 10대들의 우정과 성장 이야기",
            "한 여성의 삶을 통해 한국 현대사를 조명하는 소설",
            "말과 언어의 중요성에 대해 생각해보게 하는 에세이",
            "다양한 분야의 기초 지식을 소개하는 교양서"
        };

        for (int i = 0; i < titles.length; i++) {
            BookInfo bookInfo = BookInfo.builder()
                .title(titles[i])
                .author(authors[i])
                .isbn("979-11-" + (10000 + i) + "-" + (10 + i) + "-" + (1 + i % 9))
                .description(descriptions[i])
                .publisher(publishers[i])
                .publishDate(LocalDate.of(2020 + i % 5, (i % 12) + 1, (i % 28) + 1))
                .thumbnailUrl("https://example.com/book" + (i + 1) + ".jpg")
                .genres(Arrays.asList(genresList[i]))
                .build();

            Book book = Book.builder()
                .bookInfo(bookInfo)
                .build();
            books.add(book);
        }

        bookRepository.saveAll(books);
        log.info("책 {} 권이 생성되었습니다.", books.size());
    }

    @Transactional
    public void initQuotes() {
        List<User> users = userRepository.findAll();
        List<Book> books = bookRepository.findAll();

        if (users.isEmpty() || books.isEmpty()) {
            log.warn("사용자 또는 도서 데이터가 없어 인용구를 생성할 수 없습니다.");
            return;
        }

        List<Quote> quotes = Arrays.asList(
            Quote.builder()
                .user(users.get(0))
                .book(books.get(0))
                .content("행복은 문제를 해결하는 것이 아니라, 문제를 이해하는 데 있다.")
                .memo("철학의 위안에서 가장 인상 깊었던 구절")
                .page(42)
                .build(),

            Quote.builder()
                .user(users.get(1))
                .book(books.get(0))
                .content("우리가 불행한 것은 불행해서가 아니라, 불행하다고 생각해서이다.")
                .memo("불행에 대한 생각의 전환")
                .page(78)
                .build(),

            Quote.builder()
                .user(users.get(0))
                .book(books.get(1))
                .content("인간은 이야기를 만들어내는 유일한 종이다. 우리는 허구적 이야기로 협력하고 세상을 바꾼다.")
                .memo("사피엔스의 핵심 주장")
                .page(150)
                .build(),

            Quote.builder()
                .user(users.get(1))
                .book(books.get(2))
                .content("나는 내가 아는 것이 없다는 것을 안다.")
                .memo("소크라테스의 유명한 명언")
                .page(25)
                .build(),

            Quote.builder()
                .user(users.get(0))
                .book(books.get(3))
                .content("새는 알에서 나오기 위해 투쟁한다. 알은 세계이다. 태어나려는 자는 한 세계를 깨뜨려야 한다.")
                .memo("자아 성장에 대한 비유")
                .page(110)
                .build(),

            Quote.builder()
                .user(users.get(1))
                .book(books.get(4))
                .content("빅브라더가 당신을 지켜보고 있다.")
                .memo("감시 사회에 대한 경고")
                .page(5)
                .build()
        );

        quoteRepository.saveAll(quotes);

        // 좋아요 추가
        for (Quote quote : quotes) {
            for (User user : users) {
                // 50% 확률로 좋아요 추가
                if (random.nextBoolean()) {
                    Like like = Like.builder()
                        .user(user)
                        .quote(quote)
                        .build();
                    likeRepository.save(like);

                    // 좋아요 수 업데이트
                    quote.updateLikeCount(1);
                }
            }

            // 댓글 추가
            for (int i = 0; i < random.nextInt(3) + 1; i++) {
                User randomUser = users.get(random.nextInt(users.size()));
                Comment comment = Comment.builder()
                    .user(randomUser)
                    .quote(quote)
                    .content("이 문구에 대한 댓글 " + (i + 1) + ": 정말 인상적인 문장입니다.")
                    .build();
                commentRepository.save(comment);
            }
        }

        quoteRepository.saveAll(quotes); // 좋아요 수 업데이트 저장
        log.info("인용구 {} 개와 관련 좋아요, 댓글이 생성되었습니다.", quotes.size());
    }

    @Transactional
    public void initShortFormContents() {
        List<Book> books = bookRepository.findAll();
        List<Quote> quotes = quoteRepository.findAll();

        if (quotes.isEmpty()) {
            log.warn("인용구 데이터가 없어 콘텐츠를 생성할 수 없습니다.");
            return;
        }

        List<ShortFormContent> contents = Arrays.asList(
            ShortFormContent.builder()
                .book(books.get(0))
                .quote(quotes.get(0))
                .title("철학이 주는 일상의 위안")
                .description("알랭 드 보통의 철학적 사상을 바탕으로 일상에서 겪는 문제들을 어떻게 해결할 수 있는지 알아봅니다.")
                .status(ContentStatus.PUBLISHED)
                .build(),

            ShortFormContent.builder()
                .book(books.get(1))
                .quote(quotes.get(2))
                .title("사피엔스: 인류의 역사")
                .description("유발 하라리가 설명하는 인류의 역사와 문명의 발전 과정을 짧게 요약했습니다.")
                .status(ContentStatus.PUBLISHED)
                .build(),

            ShortFormContent.builder()
                .book(books.get(2))
                .quote(quotes.get(3))
                .title("소크라테스의 지혜")
                .description("소크라테스의 철학과 그의 가르침이 현대 사회에 주는 의미를 살펴봅니다.")
                .status(ContentStatus.PUBLISHED)
                .build(),

            ShortFormContent.builder()
                .book(books.get(3))
                .quote(quotes.get(4))
                .title("데미안: 자아의 발견")
                .description("헤르만 헤세의 소설 '데미안'에서 다루는 자아 발견과 성장에 대한 이야기입니다.")
                .status(ContentStatus.PUBLISHED)
                .build()
        );

        // 콘텐츠 정보 추가 설정
        for (ShortFormContent content : contents) {
            // setter 메서드를 사용하여 콘텐츠 속성 설정
            content.setVideoUrl("https://example.com/videos/sample-" + random.nextInt(100) + ".mp4");
            content.setThumbnailUrl("https://example.com/thumbnails/sample-" + random.nextInt(100) + ".jpg");
            content.setAudioUrl("https://example.com/audio/sample-" + random.nextInt(100) + ".mp3");
            content.setDuration(random.nextInt(180) + 30);
            content.setContentType(ContentType.VIDEO);
            content.setSubtitles("자막 예시: " + content.getTitle());

            // 조회수, 좋아요, 공유 수 랜덤 설정
            for (int i = 0; i < random.nextInt(50) + 10; i++) {
                content.incrementViewCount();
            }

            for (int i = 0; i < random.nextInt(20) + 5; i++) {
                content.updateLikeCount(1);
            }

            for (int i = 0; i < random.nextInt(10) + 1; i++) {
                content.incrementShareCount();
            }
        }

        contentRepository.saveAll(contents);
        log.info("숏폼 콘텐츠 {} 개가 생성되었습니다.", contents.size());
    }

    @Transactional
    public void initInteractions() {
        List<User> users = userRepository.findAll();
        List<ShortFormContent> contents = contentRepository.findAll();

        if (users.isEmpty() || contents.isEmpty()) {
            log.warn("사용자 또는 콘텐츠 데이터가 없어 상호작용을 생성할 수 없습니다.");
            return;
        }

        for (User user : users) {
            createUserInteractions(user, contents);
        }

        contentRepository.saveAll(contents); // 댓글 수 업데이트 저장
        log.info("사용자-콘텐츠 상호작용이 생성되었습니다.");
    }

    private void createUserInteractions(User user, List<ShortFormContent> contents) {
        for (ShortFormContent content : contents) {
            // 50% 확률로 상호작용 생성
            if (random.nextBoolean()) {
                createSingleInteraction(user, content);
            }
        }
    }

    private void createSingleInteraction(User user, ShortFormContent content) {
        // 이미 해당 사용자와 콘텐츠에 대한 상호작용이 존재하는지 확인
        boolean exists = interactionRepository.existsByUserIdAndContentId(user.getId(), content.getId());
        if (exists) {
            log.info("이미 존재하는 상호작용입니다. User ID: {}, Content ID: {}", user.getId(), content.getId());
            return;
        }

        ContentInteraction interaction = ContentInteraction.builder()
            .user(user)
            .content(content)
            .build();

        applyRandomInteractionState(interaction);
        interactionRepository.save(interaction);

        // 댓글 생성 (30% 확률)
        createCommentIfNeeded(user, content);
    }

    private void applyRandomInteractionState(ContentInteraction interaction) {
        // 좋아요, 북마크 상태 랜덤 설정
        boolean liked = random.nextBoolean();
        boolean bookmarked = random.nextBoolean();

        if (liked) {
            interaction.toggleLike(); // 좋아요 상태로 변경
        }

        if (bookmarked) {
            interaction.toggleBookmark(); // 북마크 상태로 변경
        }

        // 조회 시간 설정
        interaction.setViewedAt(LocalDateTime.now().minusDays(random.nextInt(30)));
    }

    private void createCommentIfNeeded(User user, ShortFormContent content) {
        if (random.nextInt(10) < 3) {
            ContentComment comment = ContentComment.builder()
                .user(user)
                .shortFormContent(content)
                .content("이 콘텐츠에 대한 댓글: " + (random.nextInt(100) + 1))
                .build();

            contentCommentRepository.save(comment);
            content.incrementCommentCount();
        }
    }

    @Transactional
    public void initPopularSearchTerms() {
        List<String> searchTerms = Arrays.asList(
            "철학", "소설", "자기계발", "역사", "과학",
            "에세이", "심리학", "정치", "경제", "문화",
            "예술", "인문학", "소크라테스", "플라톤", "아리스토텔레스",
            "헤르만 헤세", "조지 오웰", "유발 하라리", "알랭 드 보통"
        );

        for (String term : searchTerms) {
            // 필드 값 설정
            int searchCount = random.nextInt(1000) + 100;
            double popularityScore = searchCount / 100.0;

            // 빌더 패턴 사용
            PopularSearchTerm popularTerm = PopularSearchTerm.builder()
                .searchTerm(term)
                .searchCount(searchCount)
                .popularityScore(popularityScore)
                .build();

            popularSearchTermRepository.save(popularTerm);
        }

        log.info("인기 검색어 {} 개가 생성되었습니다.", searchTerms.size());
    }

    @Transactional
    public void initSearchHistory() {
        List<User> users = userRepository.findAll();
        List<String> searchKeywords = List.of("인공지능", "자기계발", "경영", "소설", "심리학", "철학", "역사");
        List<String> searchCategories = List.of("BOOK", "SHORTFORM", "USER");

        for (int i = 0; i < 20; i++) {
            User randomUser = users.get(random.nextInt(users.size()));
            String searchTerm = searchKeywords.get(random.nextInt(searchKeywords.size()));
            String searchType = searchCategories.get(random.nextInt(searchCategories.size()));

            Search search = Search.builder()
                .searchTerm(searchTerm)
                .searchCount(random.nextInt(10) + 1)
                .searchType(searchType)
                .user(randomUser)
                .build();

            searchRepository.save(search);
        }

        log.info("사용자별 검색 기록이 생성되었습니다.");
    }
} 