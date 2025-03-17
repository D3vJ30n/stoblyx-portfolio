package com.j30n.stoblyx.application.service.book;

import com.j30n.stoblyx.adapter.in.web.dto.quote.QuoteSummaryResponse;
import com.j30n.stoblyx.application.port.in.book.BookContentSearchUseCase;
import com.j30n.stoblyx.application.port.out.book.BookPort;
import com.j30n.stoblyx.application.port.out.quote.QuotePort;
import com.j30n.stoblyx.application.port.out.user.UserPort;
import com.j30n.stoblyx.domain.model.Book;
import com.j30n.stoblyx.domain.model.BookInfo;
import com.j30n.stoblyx.domain.model.Quote;
import com.j30n.stoblyx.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("BookContentQuoteSummaryService 테스트")
class BookContentQuoteSummaryServiceTest {

    @Mock
    private BookContentSearchUseCase bookContentSearchUseCase;

    @Mock
    private BookPort bookPort;

    @Mock
    private QuotePort quotePort;

    @Mock
    private UserPort userPort;

    @InjectMocks
    private BookContentQuoteSummaryService bookContentQuoteSummaryService;

    private Book testBook;
    private User testUser;
    private Quote testQuote;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 설정
        BookInfo bookInfo = BookInfo.builder()
            .title("사피엔스: 유인원에서 사이보그까지, 인간 역사의 대담한 역사")
            .author("유발 하라리")
            .description("인류의 역사를 다양한 관점에서 조명한 베스트셀러")
            .build();
        testBook = new Book(bookInfo);

        testUser = User.builder()
            .username("testuser")
            .email("test@example.com")
            .nickname("테스트사용자")
            .build();

        testQuote = Quote.builder()
            .user(testUser)
            .book(testBook)
            .content("인지혁명을 통해 사피엔스는 상상 속의 현실을 창조하는 능력을 얻었다.")
            .memo("테스트 메모")
            .page(15)
            .build();

        // ID 설정 (Java Reflection을 사용)
        try {
            java.lang.reflect.Field idField = testQuote.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(testQuote, 1L);
        } catch (Exception e) {
            // 무시
        }
    }

    @Test
    @DisplayName("키워드 관련 섹션을 찾아 인용구로 요약할 수 있어야 한다")
    void findAndCreateQuoteSummary_ShouldReturnQuoteSummary() {
        // given
        Long bookId = 1L;
        String keyword = "인지혁명";
        int maxSectionLength = 1000;

        String sectionContent = "인지혁명을 통해 사피엔스는 상상 속의 현실을 창조하는 능력을 얻었다.";
        String summarizedContent = "인지혁명으로 사피엔스는 상상력을 얻음";

        when(bookPort.findBookById(eq(bookId))).thenReturn(Optional.of(testBook));
        when(bookContentSearchUseCase.findRelevantSection(eq(bookId), eq(keyword), eq(maxSectionLength)))
            .thenReturn(sectionContent);
        when(bookContentSearchUseCase.findAndSummarizeAsQuote(eq(bookId), eq(keyword), eq(maxSectionLength), eq(1)))
            .thenReturn(summarizedContent);

        // when
        QuoteSummaryResponse result = bookContentQuoteSummaryService.findAndCreateQuoteSummary(bookId, keyword, maxSectionLength);

        // then
        assertThat(result).isNotNull();
        assertThat(result.bookTitle()).isEqualTo(testBook.getTitle());
        assertThat(result.originalContent()).isEqualTo(sectionContent);
        assertThat(result.summarizedContent()).isEqualTo(summarizedContent);
    }

    @Test
    @DisplayName("여러 키워드 관련 섹션을 찾아 각각 인용구로 요약할 수 있어야 한다")
    void findAndCreateMultipleQuoteSummaries_ShouldReturnMultipleQuoteSummaries() {
        // given
        Long bookId = 1L;
        String keyword = "혁명";
        int maxSectionLength = 1000;
        int maxSections = 2;

        List<String> sections = Arrays.asList(
            "인지혁명을 통해 사피엔스는 상상 속의 현실을 창조하는 능력을 얻었다.",
            "농업혁명은 인류 역사의 가장 큰 사기라고 할 수 있다."
        );

        List<String> summaries = Arrays.asList(
            "인지혁명으로 사피엔스는 상상력을 얻음",
            "농업혁명은 인류 역사의 큰 사기"
        );

        when(bookPort.findBookById(eq(bookId))).thenReturn(Optional.of(testBook));
        when(bookContentSearchUseCase.findRelevantSections(eq(bookId), eq(keyword), eq(maxSectionLength), eq(maxSections)))
            .thenReturn(sections);
        when(bookContentSearchUseCase.findAndSummarizeRelevantSections(eq(bookId), eq(keyword), eq(maxSectionLength), eq(maxSections)))
            .thenReturn(summaries);

        // when
        List<QuoteSummaryResponse> results = bookContentQuoteSummaryService.findAndCreateMultipleQuoteSummaries(
            bookId, keyword, maxSectionLength, maxSections);

        // then
        assertThat(results).hasSize(2);
        assertThat(results.get(0).bookTitle()).isEqualTo(testBook.getTitle());
        assertThat(results.get(0).originalContent()).isEqualTo(sections.get(0));
        assertThat(results.get(0).summarizedContent()).isEqualTo(summaries.get(0));
        assertThat(results.get(1).originalContent()).isEqualTo(sections.get(1));
        assertThat(results.get(1).summarizedContent()).isEqualTo(summaries.get(1));
    }

    @Test
    @DisplayName("여러 키워드 관련 섹션을 찾아 통합된 인용구로 요약할 수 있어야 한다")
    void findAndCreateIntegratedQuoteSummary_ShouldReturnIntegratedQuoteSummary() {
        // given
        Long bookId = 1L;
        String keyword = "혁명";
        int maxSectionLength = 1000;
        int maxSections = 2;

        List<String> sections = Arrays.asList(
            "인지혁명을 통해 사피엔스는 상상 속의 현실을 창조하는 능력을 얻었다.",
            "농업혁명은 인류 역사의 가장 큰 사기라고 할 수 있다."
        );
        String integratedSummary = "인류 역사에서 인지혁명과 농업혁명은 중요한 전환점이었다.";

        when(bookPort.findBookById(eq(bookId))).thenReturn(Optional.of(testBook));
        when(bookContentSearchUseCase.findRelevantSections(eq(bookId), eq(keyword), eq(maxSectionLength), eq(maxSections)))
            .thenReturn(sections);
        when(bookContentSearchUseCase.findAndSummarizeAsQuote(eq(bookId), eq(keyword), eq(maxSectionLength), eq(maxSections)))
            .thenReturn(integratedSummary);

        // when
        QuoteSummaryResponse result = bookContentQuoteSummaryService.findAndCreateIntegratedQuoteSummary(
            bookId, keyword, maxSectionLength, maxSections);

        // then
        assertThat(result).isNotNull();
        assertThat(result.bookTitle()).isEqualTo(testBook.getTitle());
        assertThat(result.originalContent()).isEqualTo(String.join("\n\n", sections));
        assertThat(result.summarizedContent()).isEqualTo(integratedSummary);
    }

    @Test
    @DisplayName("키워드 관련 섹션을 찾아 인용구로 저장할 수 있어야 한다")
    void findAndSaveQuote_ShouldSaveAndReturnQuoteId() {
        // given
        Long bookId = 1L;
        Long userId = 1L;
        String keyword = "인지혁명";
        int maxSectionLength = 1000;

        String content = "인지혁명을 통해 사피엔스는 상상 속의 현실을 창조하는 능력을 얻었다.";

        when(bookPort.findBookById(eq(bookId))).thenReturn(Optional.of(testBook));
        when(userPort.findById(eq(userId))).thenReturn(Optional.of(testUser));
        when(bookContentSearchUseCase.findRelevantSection(eq(bookId), eq(keyword), eq(maxSectionLength)))
            .thenReturn(content);
        when(quotePort.save(any(Quote.class))).thenReturn(testQuote);

        // when
        Long quoteId = bookContentQuoteSummaryService.findAndSaveQuote(bookId, userId, keyword, maxSectionLength);

        // then
        assertThat(quoteId).isNotNull();
        assertThat(quoteId).isEqualTo(testQuote.getId());
    }

    @Test
    @DisplayName("여러 키워드 관련 섹션을 찾아 각각 인용구로 저장할 수 있어야 한다")
    void findAndSaveMultipleQuotes_ShouldSaveAndReturnQuoteIds() {
        // given
        Long bookId = 1L;
        Long userId = 1L;
        String keyword = "혁명";
        int maxSectionLength = 1000;
        int maxSections = 2;

        List<String> sections = Arrays.asList(
            "인지혁명을 통해 사피엔스는 상상 속의 현실을 창조하는 능력을 얻었다.",
            "농업혁명은 인류 역사의 가장 큰 사기라고 할 수 있다."
        );

        // ID가 1L과 2L인 Quote를 생성하여 반환하도록 설정
        Quote quote1 = Quote.builder()
            .user(testUser)
            .book(testBook)
            .content(sections.get(0))
            .memo("테스트 메모 1")
            .page(15)
            .build();

        Quote quote2 = Quote.builder()
            .user(testUser)
            .book(testBook)
            .content(sections.get(1))
            .memo("테스트 메모 2")
            .page(30)
            .build();

        when(bookPort.findBookById(eq(bookId))).thenReturn(Optional.of(testBook));
        when(userPort.findById(eq(userId))).thenReturn(Optional.of(testUser));
        when(bookContentSearchUseCase.findRelevantSections(eq(bookId), eq(keyword), eq(maxSectionLength), eq(maxSections)))
            .thenReturn(sections);
        when(quotePort.save(any(Quote.class))).thenReturn(quote1, quote2);

        // when
        List<Long> quoteIds = bookContentQuoteSummaryService.findAndSaveMultipleQuotes(
            bookId, userId, keyword, maxSectionLength, maxSections);

        // then
        assertThat(quoteIds).hasSize(2);
    }
} 