package com.j30n.stoblyx.adapter.in.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.j30n.stoblyx.adapter.in.web.dto.summary.SummaryRequest;
import com.j30n.stoblyx.adapter.in.web.dto.summary.SummaryResponse;
import com.j30n.stoblyx.application.port.in.summary.SummaryUseCase;
import com.j30n.stoblyx.config.SecurityTestConfig;
import com.j30n.stoblyx.config.ContextTestConfig;
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

@WebMvcTest(SummaryController.class)
@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureRestDocs
@Import({SecurityTestConfig.class, ContextTestConfig.class, XssTestConfig.class})
@DisplayName("SummaryController 테스트")
class SummaryControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private WebApplicationContext context;
    
    private RequestPostProcessor testUser;

    @MockBean
    private SummaryUseCase summaryUseCase;
    
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
    @DisplayName("요약 생성 API가 정상적으로 동작해야 한다")
    void createSummary() throws Exception {
        // given
        Long bookId = 1L;
        SummaryRequest request = new SummaryRequest("이 책은 테스트에 관한 내용입니다.", "제1장", "42");
        SummaryResponse response = new SummaryResponse(
                1L,        // summaryId
                bookId,    // bookId
                "이 책은 테스트에 관한 내용입니다.", // content
                "제1장",    // chapter
                "42",      // page (String 타입으로 변경)
                LocalDateTime.now(), // createdAt
                LocalDateTime.now()  // modifiedAt 추가
        );
        
        when(summaryUseCase.createSummary(eq(bookId), any(SummaryRequest.class))).thenReturn(response);
        
        // when & then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/books/{bookId}/summaries", bookId)
                .with(testUser)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.data.content").value("이 책은 테스트에 관한 내용입니다."))
                .andDo(document("summary/create-summary",
                    pathParameters(
                        parameterWithName("bookId").description("책 ID")
                    ),
                    requestFields(
                        fieldWithPath("content").type(JsonFieldType.STRING).description("요약 내용"),
                        fieldWithPath("chapter").type(JsonFieldType.STRING).description("장/절"),
                        fieldWithPath("page").type(JsonFieldType.STRING).description("페이지")
                    ),
                    responseFields(
                        RestDocsUtils.getCommonResponseFieldsWithData())
                    .andWithPrefix("data.", 
                        fieldWithPath("id").type(JsonFieldType.NUMBER).description("요약 ID"),
                        fieldWithPath("bookId").type(JsonFieldType.NUMBER).description("책 ID"),
                        fieldWithPath("content").type(JsonFieldType.STRING).description("요약 내용"),
                        fieldWithPath("chapter").type(JsonFieldType.STRING).description("장/절"),
                        fieldWithPath("page").type(JsonFieldType.STRING).description("페이지"),
                        fieldWithPath("createdAt").type(JsonFieldType.STRING).description("생성 시간"),
                        fieldWithPath("modifiedAt").type(JsonFieldType.STRING).description("수정 시간")
                    )
                ));
        
        verify(summaryUseCase).createSummary(eq(bookId), any(SummaryRequest.class));
    }
    
    @Test
    @DisplayName("요약 조회 API가 정상적으로 동작해야 한다")
    void getSummary() throws Exception {
        // given
        Long bookId = 1L;
        Long summaryId = 1L;
        SummaryResponse response = new SummaryResponse(
                summaryId, // summaryId
                bookId,    // bookId
                "이 책은 테스트에 관한 내용입니다.", // content
                "제1장",    // chapter
                "42",      // page (String 타입으로 변경)
                LocalDateTime.now(), // createdAt
                LocalDateTime.now()  // modifiedAt 추가
        );
        
        when(summaryUseCase.getSummary(summaryId)).thenReturn(response);
        
        // when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/books/{bookId}/summaries/{summaryId}", bookId, summaryId)
                .with(testUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.data.content").value("이 책은 테스트에 관한 내용입니다."))
                .andDo(document("summary/get-summary",
                    pathParameters(
                        parameterWithName("bookId").description("책 ID"),
                        parameterWithName("summaryId").description("요약 ID")
                    ),
                    responseFields(
                        RestDocsUtils.getCommonResponseFieldsWithData())
                    .andWithPrefix("data.", 
                        fieldWithPath("id").type(JsonFieldType.NUMBER).description("요약 ID"),
                        fieldWithPath("bookId").type(JsonFieldType.NUMBER).description("책 ID"),
                        fieldWithPath("content").type(JsonFieldType.STRING).description("요약 내용"),
                        fieldWithPath("chapter").type(JsonFieldType.STRING).description("장/절"),
                        fieldWithPath("page").type(JsonFieldType.STRING).description("페이지"),
                        fieldWithPath("createdAt").type(JsonFieldType.STRING).description("생성 시간"),
                        fieldWithPath("modifiedAt").type(JsonFieldType.STRING).description("수정 시간")
                    )
                ));
        
        verify(summaryUseCase).getSummary(summaryId);
    }
} 