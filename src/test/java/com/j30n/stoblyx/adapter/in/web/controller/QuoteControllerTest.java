package com.j30n.stoblyx.adapter.in.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.j30n.stoblyx.adapter.in.web.dto.quote.QuoteCreateRequest;
import com.j30n.stoblyx.adapter.in.web.dto.quote.QuoteResponse;
import com.j30n.stoblyx.application.service.quote.QuoteService;
import com.j30n.stoblyx.config.SecurityTestConfig;
import com.j30n.stoblyx.config.ContextTestConfig;
import com.j30n.stoblyx.config.MonitoringTestConfig;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(QuoteController.class)
@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureRestDocs
@Import({SecurityTestConfig.class, ContextTestConfig.class, XssTestConfig.class, MonitoringTestConfig.class})
@DisplayName("QuoteController 테스트")
class QuoteControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private WebApplicationContext context;
    
    private RequestPostProcessor testUser;

    @MockBean
    private QuoteService quoteService;
    
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
    @DisplayName("문구 생성 API가 정상적으로 동작해야 한다")
    void createQuote() throws Exception {
        // given
        Long userId = 1L;
        QuoteCreateRequest request = new QuoteCreateRequest(1L, "테스트 문구", "테스트 메모", 42);
        QuoteResponse.UserInfo userInfo = new QuoteResponse.UserInfo(1L, "testuser", "테스트유저", null);
        QuoteResponse.BookInfo bookInfo = new QuoteResponse.BookInfo(1L, "테스트 책", "테스트 작가", null);
        QuoteResponse response = new QuoteResponse(1L, "테스트 문구", "테스트 메모", 42, 0, 0, false, false,
                userInfo, bookInfo, LocalDateTime.now(), LocalDateTime.now());
        
        when(quoteService.createQuote(eq(userId), any(QuoteCreateRequest.class))).thenReturn(response);
        
        // when & then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/quotes")
                .with(testUser)
                .requestAttr("userId", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.data.content").value("테스트 문구"))
                .andDo(document("quote/create-quote",
                    requestFields(
                        fieldWithPath("bookId").type(JsonFieldType.NUMBER).description("책 ID"),
                        fieldWithPath("content").type(JsonFieldType.STRING).description("인용구 내용"),
                        fieldWithPath("memo").type(JsonFieldType.STRING).description("메모"),
                        fieldWithPath("page").type(JsonFieldType.NUMBER).description("페이지 번호")
                    ),
                    responseFields(
                        RestDocsUtils.getCommonResponseFieldsWithData())
                    .andWithPrefix("data.", 
                        fieldWithPath("id").type(JsonFieldType.NUMBER).description("인용구 ID"),
                        fieldWithPath("content").type(JsonFieldType.STRING).description("인용구 내용"),
                        fieldWithPath("memo").type(JsonFieldType.STRING).description("메모"),
                        fieldWithPath("page").type(JsonFieldType.NUMBER).description("페이지 번호"),
                        fieldWithPath("likeCount").type(JsonFieldType.NUMBER).description("좋아요 수"),
                        fieldWithPath("saveCount").type(JsonFieldType.NUMBER).description("저장 수"),
                        fieldWithPath("isLiked").type(JsonFieldType.BOOLEAN).description("좋아요 여부"),
                        fieldWithPath("isSaved").type(JsonFieldType.BOOLEAN).description("저장 여부"),
                        fieldWithPath("user").type(JsonFieldType.OBJECT).description("사용자 정보"),
                        fieldWithPath("user.id").type(JsonFieldType.NUMBER).description("사용자 ID"),
                        fieldWithPath("user.username").type(JsonFieldType.STRING).description("사용자 이름"),
                        fieldWithPath("user.nickname").type(JsonFieldType.STRING).description("사용자 닉네임"),
                        fieldWithPath("user.profileImageUrl").type(JsonFieldType.NULL).description("프로필 이미지"),
                        fieldWithPath("book").type(JsonFieldType.OBJECT).description("책 정보"),
                        fieldWithPath("book.id").type(JsonFieldType.NUMBER).description("책 ID"),
                        fieldWithPath("book.title").type(JsonFieldType.STRING).description("책 제목"),
                        fieldWithPath("book.author").type(JsonFieldType.STRING).description("책 저자"),
                        fieldWithPath("book.thumbnailUrl").type(JsonFieldType.NULL).description("썸네일 URL"),
                        fieldWithPath("createdAt").type(JsonFieldType.STRING).description("생성 시간"),
                        fieldWithPath("modifiedAt").type(JsonFieldType.STRING).description("수정 시간")
                    )
                ));
        
        verify(quoteService).createQuote(eq(userId), any(QuoteCreateRequest.class));
    }
    
    @Test
    @DisplayName("사용자별 문구 목록 조회 API가 정상적으로 동작해야 한다")
    void getQuotes() throws Exception {
        // given
        Long userId = 1L;
        QuoteResponse.UserInfo userInfo = new QuoteResponse.UserInfo(1L, "testuser", "테스트유저", null);
        QuoteResponse.BookInfo bookInfo = new QuoteResponse.BookInfo(1L, "테스트 책", "테스트 작가", null);
        
        List<QuoteResponse> quotes = List.of(
            new QuoteResponse(1L, "테스트 문구 1", "테스트 메모 1", 42, 10, 5, false, false,
                    userInfo, bookInfo, LocalDateTime.now(), LocalDateTime.now()),
            new QuoteResponse(2L, "테스트 문구 2", "테스트 메모 2", 100, 5, 2, true, true,
                    userInfo, bookInfo, LocalDateTime.now(), LocalDateTime.now())
        );
        
        // 명시적인 Pageable 객체 생성
        Pageable pageable = PageRequest.of(0, 10);
        PageImpl<QuoteResponse> page = new PageImpl<>(quotes, pageable, quotes.size());
        
        when(quoteService.getQuotes(eq(userId), any())).thenReturn(page);
        
        // when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/quotes")
                .with(testUser)
                .param("userId", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.data.content[0].content").value("테스트 문구 1"))
                .andDo(document("quote/get-quotes",
                    queryParameters(
                        parameterWithName("userId").description("사용자 ID"),
                        parameterWithName("page").description("페이지 번호 (0부터 시작)").optional(),
                        parameterWithName("size").description("페이지 크기").optional(),
                        parameterWithName("sort").description("정렬 방식 (예: createdAt,desc)").optional()
                    ),
                    RestDocsUtils.relaxedResponseFields(
                        RestDocsUtils.getCommonResponseFieldsWithData())
                    .andWithPrefix("data.content[].", 
                        fieldWithPath("id").type(JsonFieldType.NUMBER).description("인용구 ID"),
                        fieldWithPath("content").type(JsonFieldType.STRING).description("인용구 내용"),
                        fieldWithPath("memo").type(JsonFieldType.STRING).description("메모"),
                        fieldWithPath("page").type(JsonFieldType.NUMBER).description("페이지 번호"),
                        fieldWithPath("likeCount").type(JsonFieldType.NUMBER).description("좋아요 수"),
                        fieldWithPath("saveCount").type(JsonFieldType.NUMBER).description("저장 수"),
                        fieldWithPath("isLiked").type(JsonFieldType.BOOLEAN).description("좋아요 여부"),
                        fieldWithPath("isSaved").type(JsonFieldType.BOOLEAN).description("저장 여부"),
                        fieldWithPath("user").type(JsonFieldType.OBJECT).description("사용자 정보"),
                        fieldWithPath("user.id").type(JsonFieldType.NUMBER).description("사용자 ID"),
                        fieldWithPath("user.username").type(JsonFieldType.STRING).description("사용자 이름"),
                        fieldWithPath("user.nickname").type(JsonFieldType.STRING).description("사용자 닉네임"),
                        fieldWithPath("user.profileImageUrl").type(JsonFieldType.NULL).description("프로필 이미지"),
                        fieldWithPath("book").type(JsonFieldType.OBJECT).description("책 정보"),
                        fieldWithPath("book.id").type(JsonFieldType.NUMBER).description("책 ID"),
                        fieldWithPath("book.title").type(JsonFieldType.STRING).description("책 제목"),
                        fieldWithPath("book.author").type(JsonFieldType.STRING).description("책 저자"),
                        fieldWithPath("book.thumbnailUrl").type(JsonFieldType.NULL).description("썸네일 URL"),
                        fieldWithPath("createdAt").type(JsonFieldType.STRING).description("생성 시간"),
                        fieldWithPath("modifiedAt").type(JsonFieldType.STRING).description("수정 시간")
                    )
                    .and(RestDocsUtils.getPageResponseFields())
                ));
        
        verify(quoteService).getQuotes(eq(userId), any());
    }
} 