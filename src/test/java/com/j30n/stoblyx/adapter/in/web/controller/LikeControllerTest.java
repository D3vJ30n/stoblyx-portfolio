package com.j30n.stoblyx.adapter.in.web.controller;

import com.j30n.stoblyx.application.service.like.LikeService;
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
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LikeController.class)
@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureRestDocs
@Import({SecurityTestConfig.class, ContextTestConfig.class, XssTestConfig.class})
@DisplayName("LikeController 테스트")
class LikeControllerTest {

    private MockMvc mockMvc;
    
    @Autowired
    private WebApplicationContext context;
    
    private RequestPostProcessor testUser;

    @MockBean
    private LikeService likeService;

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
    @DisplayName("좋아요 추가 API가 정상적으로 동작해야 한다")
    void likeQuote() throws Exception {
        // given
        Long userId = 1L;
        Long quoteId = 1L;
        
        // 예외가 발생하지 않도록 설정
        doNothing().when(likeService).likeQuote(userId, quoteId);
        
        // when & then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/likes/quotes/{quoteId}", quoteId)
                .with(testUser)
                .requestAttr("userId", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.data").value(true))
                .andDo(document("like/like-quote",
                    pathParameters(
                        parameterWithName("quoteId").description("인용구 ID")
                    ),
                    responseFields(
                        RestDocsUtils.getCommonResponseFields())
                    .andWithPrefix("data.", 
                        fieldWithPath("").type(JsonFieldType.BOOLEAN).description("좋아요 상태")
                    )
                ));
        
        verify(likeService).likeQuote(userId, quoteId);
    }
    
    @Test
    @DisplayName("좋아요 취소 API가 정상적으로 동작해야 한다")
    void unlikeQuote() throws Exception {
        // given
        Long userId = 1L;
        Long quoteId = 1L;
        
        // 예외가 발생하지 않도록 설정
        doNothing().when(likeService).unlikeQuote(userId, quoteId);
        
        // when & then
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/likes/quotes/{quoteId}", quoteId)
                .with(testUser)
                .requestAttr("userId", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.data").value(false))
                .andDo(document("like/unlike-quote",
                    pathParameters(
                        parameterWithName("quoteId").description("인용구 ID")
                    ),
                    responseFields(
                        RestDocsUtils.getCommonResponseFields())
                    .andWithPrefix("data.", 
                        fieldWithPath("").type(JsonFieldType.BOOLEAN).description("좋아요 상태")
                    )
                ));
        
        verify(likeService).unlikeQuote(userId, quoteId);
    }
} 