package com.j30n.stoblyx.adapter.in.web.controller;

import com.j30n.stoblyx.adapter.in.web.dto.quote.QuoteSummaryResponse;
import com.j30n.stoblyx.application.service.quote.QuoteSummaryService;
import com.j30n.stoblyx.config.ContextTestConfig;
import com.j30n.stoblyx.config.MonitoringTestConfig;
import com.j30n.stoblyx.config.SecurityTestConfig;
import com.j30n.stoblyx.config.XssTestConfig;
import com.j30n.stoblyx.support.docs.RestDocsUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(QuoteSummaryController.class)
@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureRestDocs
@Import({SecurityTestConfig.class, ContextTestConfig.class, XssTestConfig.class, MonitoringTestConfig.class})
@DisplayName("QuoteSummaryController 테스트")
class QuoteSummaryControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    private RequestPostProcessor testUser;

    @MockBean
    private QuoteSummaryService quoteSummaryService;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .apply(documentationConfiguration(restDocumentation)
                .operationPreprocessors()
                .withRequestDefaults(Preprocessors.prettyPrint())
                .withResponseDefaults(Preprocessors.prettyPrint()))
            .apply(springSecurity())
            .build();

        this.testUser = RestDocsUtils.getTestUser();
    }

    @Test
    @DisplayName("문구 요약 API가 정상적으로 동작해야 한다")
    void summarizeQuote() throws Exception {
        // given
        QuoteSummaryResponse response = QuoteSummaryResponse.builder()
            .id(1L)
            .originalContent("원본 문구 내용")
            .summarizedContent("요약된 문구 내용")
            .bookTitle("테스트 책")
            .authorNickname("테스트 작성자")
            .build();

        when(quoteSummaryService.summarizeQuote(1L)).thenReturn(response);

        // when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/quotes/{quoteId}/summary", 1L)
                .with(testUser))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value("SUCCESS"))
            .andExpect(jsonPath("$.message").value("문구가 성공적으로 요약되었습니다."))
            .andExpect(jsonPath("$.data.originalContent").value("원본 문구 내용"))
            .andExpect(jsonPath("$.data.summarizedContent").value("요약된 문구 내용"))
            .andExpect(jsonPath("$.data.bookTitle").value("테스트 책"))
            .andExpect(jsonPath("$.data.authorNickname").value("테스트 작성자"))
            .andDo(document("quote-summary/summarize-quote",
                pathParameters(
                    parameterWithName("quoteId").description("문구 ID")
                ),
                responseFields(
                    RestDocsUtils.getCommonResponseFieldsWithData())
                    .andWithPrefix("data.",
                        fieldWithPath("id").type(JsonFieldType.NUMBER).description("문구 요약 ID"),
                        fieldWithPath("originalContent").type(JsonFieldType.STRING).description("원본 문구 내용"),
                        fieldWithPath("summarizedContent").type(JsonFieldType.STRING).description("요약된 문구 내용"),
                        fieldWithPath("bookTitle").type(JsonFieldType.STRING).description("책 제목"),
                        fieldWithPath("authorNickname").type(JsonFieldType.STRING).description("작성자 닉네임")
                    )
            ));

        verify(quoteSummaryService).summarizeQuote(1L);
    }

    @Test
    @DisplayName("존재하지 않는 문구 ID로 요약 요청 시 오류가 발생해야 한다")
    void summarizeQuoteWithInvalidId() throws Exception {
        // given
        when(quoteSummaryService.summarizeQuote(999L)).thenThrow(new IllegalArgumentException("존재하지 않는 문구입니다."));

        // when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/quotes/{quoteId}/summary", 999L)
                .with(testUser))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.result").value("ERROR"))
            .andExpect(jsonPath("$.message").value("존재하지 않는 문구입니다."))
            .andDo(document("quote-summary/summarize-quote-error",
                pathParameters(
                    parameterWithName("quoteId").description("존재하지 않는 문구 ID")
                ),
                responseFields(
                    fieldWithPath("result").type(JsonFieldType.STRING).description("결과 상태 (ERROR)"),
                    fieldWithPath("message").type(JsonFieldType.STRING).description("오류 메시지"),
                    fieldWithPath("data").type(JsonFieldType.NULL).description("데이터 (null)")
                )
            ));

        verify(quoteSummaryService).summarizeQuote(999L);
    }
} 