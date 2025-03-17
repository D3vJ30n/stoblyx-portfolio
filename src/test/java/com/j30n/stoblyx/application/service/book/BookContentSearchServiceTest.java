package com.j30n.stoblyx.application.service.book;

import com.j30n.stoblyx.adapter.out.persistence.ai.KoBartClient;
import com.j30n.stoblyx.application.port.out.book.BookPort;
import com.j30n.stoblyx.application.port.out.summary.SummaryPort;
import com.j30n.stoblyx.domain.model.Book;
import com.j30n.stoblyx.domain.model.BookInfo;
import com.j30n.stoblyx.domain.model.Summary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("BookContentSearchService 테스트")
class BookContentSearchServiceTest {

    @Mock
    private BookPort bookPort;

    @Mock
    private SummaryPort summaryPort;

    @Mock
    private KoBartClient koBartClient;

    @InjectMocks
    private BookContentSearchService bookContentSearchService;

    private Book testBook;
    private List<Summary> testSummaries;

    @BeforeEach
    void setUp() {
        // 실제 존재하는 책: '사피엔스' 설정
        BookInfo bookInfo = BookInfo.builder()
            .title("사피엔스: 유인원에서 사이보그까지, 인간 역사의 대담한 역사")
            .author("유발 하라리")
            .description("인류의 역사를 다양한 관점에서 조명한 베스트셀러")
            .build();
        testBook = new Book(bookInfo);

        // 실제 내용 기반 요약 생성
        Summary summary1 = Summary.builder()
            .book(testBook)
            .content("인지혁명을 통해 사피엔스는 상상 속의 현실을 창조하는 능력을 얻었다. 이 능력으로 대규모 협력이 가능해졌고, 다른 인간 종을 물리칠 수 있었다. 신화, 종교, 국가, 돈과 같은 공유된 믿음 체계는 인간 사회의 기반이 되었다.")
            .chapter("1장: 인지혁명")
            .page("15")
            .build();

        Summary summary2 = Summary.builder()
            .book(testBook)
            .content("농업혁명은 인류 역사의 가장 큰 사기라고 할 수 있다. 사피엔스는 몇몇 식물과 동물을 가축화함으로써 더 많은 식량을 생산할 수 있게 되었지만, 대부분의 개인에게는 삶의 질이 오히려 하락했다. 농부들은 수렵채집인보다 더 많은 시간을 노동에 투자해야 했고, 덜 다양한 식단으로 영양 상태도 나빠졌다.")
            .chapter("2장: 농업혁명")
            .page("87")
            .build();

        Summary summary3 = Summary.builder()
            .book(testBook)
            .content("과학혁명은 인류가 무지를 인정하는 것에서 시작되었다. 과학은 '모른다'는 사실을 인정하고, 관찰과 수학을 통해 지식을 얻으려는 시도이다. 이러한 접근법은 기술 발전과 결합하여 인류의 능력을 극적으로 확장시켰다. 제국주의, 자본주의와 결합한 과학은 전 세계적인 단일 문화권을 형성하는 데 기여했다.")
            .chapter("4장: 과학혁명")
            .page("248")
            .build();

        testSummaries = Arrays.asList(summary1, summary2, summary3);
    }

    @Test
    @DisplayName("검색어와 관련된 단일 섹션을 찾을 수 있어야 한다")
    void findRelevantSection_ShouldReturnSingleSection() {
        // given
        Long bookId = 1L;
        String keyword = "혁명";

        when(bookPort.findBookById(eq(bookId))).thenReturn(Optional.of(testBook));
        when(summaryPort.findByBook(eq(testBook), any(Pageable.class)))
            .thenReturn(new PageImpl<>(testSummaries));

        // when
        String result = bookContentSearchService.findRelevantSection(bookId, keyword, 1000);

        // then
        assertThat(result).isNotEmpty();
        assertThat(result).containsIgnoringCase(keyword);
    }

    @Test
    @DisplayName("검색어와 관련된 여러 섹션을 찾을 수 있어야 한다")
    void findRelevantSections_ShouldReturnMultipleSections() {
        // given
        Long bookId = 1L;
        String keyword = "인류";
        int maxSections = 2;

        when(bookPort.findBookById(eq(bookId))).thenReturn(Optional.of(testBook));
        when(summaryPort.findByBook(eq(testBook), any(Pageable.class)))
            .thenReturn(new PageImpl<>(testSummaries));

        // when
        List<String> results = bookContentSearchService.findRelevantSections(bookId, keyword, 1000, maxSections);

        // then
        assertThat(results).isNotEmpty();
        assertThat(results.size()).isLessThanOrEqualTo(maxSections);
        assertThat(results.get(0)).containsIgnoringCase(keyword);
    }

    @Test
    @DisplayName("검색어가 없는 경우 빈 결과를 반환해야 한다")
    void findRelevantSections_WithEmptyKeyword_ShouldReturnEmptyList() {
        // given
        Long bookId = 1L;
        String keyword = "";

        // when
        List<String> results = bookContentSearchService.findRelevantSections(bookId, keyword, 1000, 3);

        // then
        assertThat(results).isEmpty();
    }

    @Test
    @DisplayName("책에 요약이 없는 경우 빈 결과를 반환해야 한다")
    void findRelevantSections_WithNoSummaries_ShouldReturnEmptyList() {
        // given
        Long bookId = 1L;
        String keyword = "사피엔스";

        when(bookPort.findBookById(eq(bookId))).thenReturn(Optional.of(testBook));
        when(summaryPort.findByBook(eq(testBook), any(Pageable.class)))
            .thenReturn(new PageImpl<>(Collections.emptyList()));

        // when
        List<String> results = bookContentSearchService.findRelevantSections(bookId, keyword, 1000, 3);

        // then
        assertThat(results).isEmpty();
    }

    @Test
    @DisplayName("검색어와 관련이 없는 경우에도 결과를 반환할 수 있어야 한다")
    void findRelevantSections_WithIrrelevantKeyword_ShouldReturnSections() {
        // given
        Long bookId = 1L;
        String keyword = "컴퓨터"; // 관련 없는 키워드

        when(bookPort.findBookById(eq(bookId))).thenReturn(Optional.of(testBook));
        when(summaryPort.findByBook(eq(testBook), any(Pageable.class)))
            .thenReturn(new PageImpl<>(testSummaries));

        // when
        List<String> results = bookContentSearchService.findRelevantSections(bookId, keyword, 1000, 3);

        // then
        assertThat(results).isNotEmpty(); // 관련 없어도 일부 섹션 반환
    }

    @Test
    @DisplayName("섹션 길이 제한이 적용되어야 한다")
    void findRelevantSection_WithLengthLimit_ShouldTruncateResult() {
        // given
        Long bookId = 1L;
        String keyword = "혁명";
        int maxLength = 20; // 매우 짧은 길이 제한

        when(bookPort.findBookById(eq(bookId))).thenReturn(Optional.of(testBook));
        when(summaryPort.findByBook(eq(testBook), any(Pageable.class)))
            .thenReturn(new PageImpl<>(testSummaries));

        // when
        String result = bookContentSearchService.findRelevantSection(bookId, keyword, maxLength);

        // then
        assertThat(result).isNotEmpty();
        assertThat(result.length()).isLessThanOrEqualTo(maxLength);
    }

    @Test
    @DisplayName("섹션 내용이 성공적으로 요약되어야 한다")
    void findAndSummarizeRelevantSection_ShouldSummarizeSection() {
        // given
        Long bookId = 1L;
        String keyword = "혁명";
        String sectionContent = "인지혁명을 통해 사피엔스는 상상 속의 현실을 창조하는 능력을 얻었다.";
        String expectedSummary = "인지혁명으로 사피엔스는 상상력을 얻음";

        when(bookPort.findBookById(eq(bookId))).thenReturn(Optional.of(testBook));
        when(summaryPort.findByBook(eq(testBook), any(Pageable.class)))
            .thenReturn(new PageImpl<>(testSummaries));
        when(koBartClient.summarizeChapter(anyString())).thenReturn(expectedSummary);

        // when
        String result = bookContentSearchService.findAndSummarizeRelevantSection(bookId, keyword, 1000);

        // then
        assertThat(result).isEqualTo(expectedSummary);
    }

    @Test
    @DisplayName("여러 섹션 내용이 성공적으로 요약되어야 한다")
    void findAndSummarizeRelevantSections_ShouldSummarizeMultipleSections() {
        // given
        Long bookId = 1L;
        String keyword = "혁명";
        List<String> expectedSummaries = Arrays.asList(
            "인지혁명 요약",
            "농업혁명 요약"
        );

        when(bookPort.findBookById(eq(bookId))).thenReturn(Optional.of(testBook));
        when(summaryPort.findByBook(eq(testBook), any(Pageable.class)))
            .thenReturn(new PageImpl<>(testSummaries));
        when(koBartClient.summarizeChapters(anyList())).thenReturn(expectedSummaries);

        // when
        List<String> results = bookContentSearchService.findAndSummarizeRelevantSections(bookId, keyword, 1000, 2);

        // then
        assertThat(results).isEqualTo(expectedSummaries);
    }

    @Test
    @DisplayName("섹션 내용이 인용구 형태로 요약되어야 한다")
    void findAndSummarizeAsQuote_ShouldSummarizeAsQuote() {
        // given
        Long bookId = 1L;
        String keyword = "혁명";
        String expectedQuote = "인류의 세 가지 혁명은 인지, 농업, 과학 혁명이며 각각 인류 역사의 중요한 전환점이 되었다.";

        when(bookPort.findBookById(eq(bookId))).thenReturn(Optional.of(testBook));
        when(summaryPort.findByBook(eq(testBook), any(Pageable.class)))
            .thenReturn(new PageImpl<>(testSummaries));
        when(koBartClient.summarizeChaptersAndQuote(anyList())).thenReturn(expectedQuote);

        // when
        String result = bookContentSearchService.findAndSummarizeAsQuote(bookId, keyword, 1000, 3);

        // then
        assertThat(result).isEqualTo(expectedQuote);
    }

    @Test
    @DisplayName("섹션을 찾을 수 없는 경우 빈 문자열을 반환해야 한다")
    void findAndSummarizeAsQuote_WhenNoSections_ShouldReturnEmptyString() {
        // given
        Long bookId = 1L;
        String keyword = "없는키워드";

        when(bookPort.findBookById(eq(bookId))).thenReturn(Optional.of(testBook));
        when(summaryPort.findByBook(eq(testBook), any(Pageable.class)))
            .thenReturn(new PageImpl<>(Collections.emptyList()));

        // when
        String result = bookContentSearchService.findAndSummarizeAsQuote(bookId, keyword, 1000, 3);

        // then
        assertThat(result).isEmpty();
    }
} 