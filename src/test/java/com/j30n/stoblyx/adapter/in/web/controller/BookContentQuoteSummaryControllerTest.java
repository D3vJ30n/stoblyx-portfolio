package com.j30n.stoblyx.adapter.in.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.j30n.stoblyx.adapter.in.web.dto.quote.QuoteSummaryResponse;
import com.j30n.stoblyx.application.port.in.book.BookContentQuoteSummaryUseCase;
import com.j30n.stoblyx.config.WebMvcTestConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookContentQuoteSummaryController.class)
@Import(WebMvcTestConfig.class)
@DisplayName("BookContentQuoteSummaryController 테스트")
class BookContentQuoteSummaryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookContentQuoteSummaryUseCase bookContentQuoteSummaryUseCase;

    @Test
    @DisplayName("키워드로 책 내용 검색 및 요약 API가 성공적으로 동작해야 한다")
    void findAndCreateQuoteSummary_ShouldReturnSuccessResponse() throws Exception {
        // given
        Long bookId = 1L;
        String keyword = "인지혁명";
        int maxLength = 1000;

        QuoteSummaryResponse response = QuoteSummaryResponse.builder()
            .id(null)
            .originalContent("인지혁명을 통해 사피엔스는 상상 속의 현실을 창조하는 능력을 얻었다.")
            .summarizedContent("인지혁명으로 사피엔스는 상상력을 얻음")
            .bookTitle("사피엔스")
            .authorNickname("유발 하라리")
            .build();

        when(bookContentQuoteSummaryUseCase.findAndCreateQuoteSummary(eq(bookId), eq(keyword), eq(maxLength)))
            .thenReturn(response);

        // when & then
        mockMvc.perform(get("/books/{bookId}/keyword-summary", bookId)
                .param("keyword", keyword)
                .param("maxLength", String.valueOf(maxLength))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value("SUCCESS"))
            .andExpect(jsonPath("$.data.originalContent").value(response.originalContent()))
            .andExpect(jsonPath("$.data.summarizedContent").value(response.summarizedContent()))
            .andExpect(jsonPath("$.data.bookTitle").value(response.bookTitle()));
    }

    @Test
    @DisplayName("키워드로 여러 책 내용 검색 및 요약 API가 성공적으로 동작해야 한다")
    void findAndCreateMultipleQuoteSummaries_ShouldReturnSuccessResponse() throws Exception {
        // given
        Long bookId = 1L;
        String keyword = "혁명";
        int maxLength = 1000;
        int maxSections = 2;

        List<QuoteSummaryResponse> responses = Arrays.asList(
            QuoteSummaryResponse.builder()
                .id(null)
                .originalContent("인지혁명을 통해 사피엔스는 상상 속의 현실을 창조하는 능력을 얻었다.")
                .summarizedContent("인지혁명으로 사피엔스는 상상력을 얻음")
                .bookTitle("사피엔스")
                .authorNickname("유발 하라리")
                .build(),
            QuoteSummaryResponse.builder()
                .id(null)
                .originalContent("농업혁명은 인류 역사의 가장 큰 사기라고 할 수 있다.")
                .summarizedContent("농업혁명은 인류 역사의 큰 사기")
                .bookTitle("사피엔스")
                .authorNickname("유발 하라리")
                .build()
        );

        when(bookContentQuoteSummaryUseCase.findAndCreateMultipleQuoteSummaries(
            eq(bookId), eq(keyword), eq(maxLength), eq(maxSections)))
            .thenReturn(responses);

        // when & then
        mockMvc.perform(get("/books/{bookId}/keyword-summaries", bookId)
                .param("keyword", keyword)
                .param("maxLength", String.valueOf(maxLength))
                .param("maxSections", String.valueOf(maxSections))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value("SUCCESS"))
            .andExpect(jsonPath("$.data[0].originalContent").value(responses.get(0).originalContent()))
            .andExpect(jsonPath("$.data[0].summarizedContent").value(responses.get(0).summarizedContent()))
            .andExpect(jsonPath("$.data[1].originalContent").value(responses.get(1).originalContent()))
            .andExpect(jsonPath("$.data[1].summarizedContent").value(responses.get(1).summarizedContent()));
    }

    @Test
    @DisplayName("키워드로 통합 책 내용 검색 및 요약 API가 성공적으로 동작해야 한다")
    void findAndCreateIntegratedQuoteSummary_ShouldReturnSuccessResponse() throws Exception {
        // given
        Long bookId = 1L;
        String keyword = "혁명";
        int maxLength = 1000;
        int maxSections = 2;

        QuoteSummaryResponse response = QuoteSummaryResponse.builder()
            .id(null)
            .originalContent("인지혁명을 통해 사피엔스는 상상 속의 현실을 창조하는 능력을 얻었다.\n\n농업혁명은 인류 역사의 가장 큰 사기라고 할 수 있다.")
            .summarizedContent("인류 역사에서 인지혁명과 농업혁명은 중요한 전환점이었다.")
            .bookTitle("사피엔스")
            .authorNickname("유발 하라리")
            .build();

        when(bookContentQuoteSummaryUseCase.findAndCreateIntegratedQuoteSummary(
            eq(bookId), eq(keyword), eq(maxLength), eq(maxSections)))
            .thenReturn(response);

        // when & then
        mockMvc.perform(get("/books/{bookId}/integrated-keyword-summary", bookId)
                .param("keyword", keyword)
                .param("maxLength", String.valueOf(maxLength))
                .param("maxSections", String.valueOf(maxSections))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value("SUCCESS"))
            .andExpect(jsonPath("$.data.originalContent").value(response.originalContent()))
            .andExpect(jsonPath("$.data.summarizedContent").value(response.summarizedContent()))
            .andExpect(jsonPath("$.data.bookTitle").value(response.bookTitle()));
    }

    @Test
    @DisplayName("키워드로 책 내용 검색 및 인용구 저장 API가 성공적으로 동작해야 한다")
    void findAndSaveQuote_ShouldReturnSuccessResponse() throws Exception {
        // given
        Long bookId = 1L;
        Long userId = 1L;
        String keyword = "인지혁명";
        int maxLength = 1000;
        Long quoteId = 100L;

        when(bookContentQuoteSummaryUseCase.findAndSaveQuote(
            eq(bookId), eq(userId), eq(keyword), eq(maxLength)))
            .thenReturn(quoteId);

        // when & then
        mockMvc.perform(post("/books/{bookId}/keyword-quote", bookId)
                .param("userId", String.valueOf(userId))
                .param("keyword", keyword)
                .param("maxLength", String.valueOf(maxLength))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value("SUCCESS"))
            .andExpect(jsonPath("$.data").value(quoteId));
    }

    @Test
    @DisplayName("키워드로 여러 책 내용 검색 및 인용구 저장 API가 성공적으로 동작해야 한다")
    void findAndSaveMultipleQuotes_ShouldReturnSuccessResponse() throws Exception {
        // given
        Long bookId = 1L;
        Long userId = 1L;
        String keyword = "혁명";
        int maxLength = 1000;
        int maxSections = 2;
        List<Long> quoteIds = Arrays.asList(100L, 101L);

        when(bookContentQuoteSummaryUseCase.findAndSaveMultipleQuotes(
            eq(bookId), eq(userId), eq(keyword), eq(maxLength), eq(maxSections)))
            .thenReturn(quoteIds);

        // when & then
        mockMvc.perform(post("/books/{bookId}/keyword-quotes", bookId)
                .param("userId", String.valueOf(userId))
                .param("keyword", keyword)
                .param("maxLength", String.valueOf(maxLength))
                .param("maxSections", String.valueOf(maxSections))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value("SUCCESS"))
            .andExpect(jsonPath("$.data[0]").value(quoteIds.get(0)))
            .andExpect(jsonPath("$.data[1]").value(quoteIds.get(1)));
    }
} 