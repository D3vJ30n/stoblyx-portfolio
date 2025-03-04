package com.j30n.stoblyx.adapter.in.web.controller;

import com.j30n.stoblyx.adapter.in.web.dto.search.SearchRequest;
import com.j30n.stoblyx.adapter.in.web.dto.search.SearchResponse;
import com.j30n.stoblyx.application.port.in.search.SearchUseCase;
import com.j30n.stoblyx.config.SecurityTestConfig;
import com.j30n.stoblyx.config.ContextTestConfig;
import com.j30n.stoblyx.domain.model.Search;
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

@WebMvcTest(SearchController.class)
@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureRestDocs
@Import({SecurityTestConfig.class, ContextTestConfig.class})
@DisplayName("SearchController 테스트")
class SearchControllerTest {

    private MockMvc mockMvc;
    
    @Autowired
    private WebApplicationContext context;
    
    private RequestPostProcessor testUser;

    @MockBean
    private SearchUseCase searchUseCase;
    
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
        
        this.testUser = request -> {
            request.setAttribute("userId", 1L);
            return RestDocsUtils.getTestUser().postProcessRequest(request);
        };
    }

    @Test
    @DisplayName("통합 검색 API가 정상적으로 동작해야 한다")
    void search() throws Exception {
        // given
        SearchResponse result = SearchResponse.builder()
                .id(1L)
                .type("ALL")
                .title("테스트 책")
                .content("테스트 내용")
                .author("테스트 작가")
                .category("테스트 카테고리")
                .createdAt(LocalDateTime.now())
                .build();
        
        // 명시적인 Pageable 객체 생성
        Pageable pageable = PageRequest.of(0, 10);
        PageImpl<SearchResponse> page = new PageImpl<>(List.of(result), pageable, 1);
        
        when(searchUseCase.search(any(SearchRequest.class), any())).thenReturn(page);
        
        // when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/search")
                .with(testUser)
                .param("keyword", "테스트")
                .param("type", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.data.content[0].title").value("테스트 책"))
                .andDo(document("search/search",
                    queryParameters(
                        parameterWithName("keyword").description("검색 키워드"),
                        parameterWithName("type").description("검색 타입 (ALL, BOOK, QUOTE, USER)"),
                        parameterWithName("page").description("페이지 번호 (0부터 시작)").optional(),
                        parameterWithName("size").description("페이지 크기").optional(),
                        parameterWithName("sort").description("정렬 방식 (예: createdAt,desc)").optional()
                    ),
                    relaxedResponseFields(
                        RestDocsUtils.getCommonResponseFieldsWithData())
                    .andWithPrefix("data.content[].", 
                        fieldWithPath("id").type(JsonFieldType.NUMBER).description("검색 결과 ID"),
                        fieldWithPath("type").type(JsonFieldType.STRING).description("검색 결과 타입"),
                        fieldWithPath("title").type(JsonFieldType.STRING).description("제목"),
                        fieldWithPath("content").type(JsonFieldType.STRING).description("내용"),
                        fieldWithPath("author").type(JsonFieldType.STRING).description("작성자"),
                        fieldWithPath("category").type(JsonFieldType.STRING).description("카테고리"),
                        fieldWithPath("createdAt").type(JsonFieldType.STRING).description("생성 시간")
                    )
                    .andWithPrefix("data.", 
                        fieldWithPath("last").type(JsonFieldType.BOOLEAN).description("마지막 페이지 여부"),
                        fieldWithPath("totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 수"),
                        fieldWithPath("totalElements").type(JsonFieldType.NUMBER).description("전체 요소 수"),
                        fieldWithPath("size").type(JsonFieldType.NUMBER).description("페이지 크기"),
                        fieldWithPath("number").type(JsonFieldType.NUMBER).description("현재 페이지 번호"),
                        fieldWithPath("sort.empty").type(JsonFieldType.BOOLEAN).description("정렬 정보 존재 여부"),
                        fieldWithPath("sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬 여부"),
                        fieldWithPath("sort.unsorted").type(JsonFieldType.BOOLEAN).description("비정렬 여부"),
                        fieldWithPath("first").type(JsonFieldType.BOOLEAN).description("첫 페이지 여부"),
                        fieldWithPath("numberOfElements").type(JsonFieldType.NUMBER).description("현재 페이지 요소 수"), 
                        fieldWithPath("empty").type(JsonFieldType.BOOLEAN).description("페이지 결과 존재 여부")
                    )
                ));
        
        verify(searchUseCase).search(any(SearchRequest.class), any());
    }
    
    @Test
    @DisplayName("사용자 검색 기록 조회 API가 정상적으로 동작해야 한다")
    void getUserSearchHistory() throws Exception {
        // given
        Long userId = 1L;
        
        // Search 객체 생성 - 실제 데이터 흐름보다는 테스트 성공을 위한 목 데이터
        Search searchHistory = Search.builder()
                .keyword("테스트 검색")
                .category("ALL")
                .resultCount(10)
                .build();
        
        // id 값 설정을 위한 리플렉션 사용
        java.lang.reflect.Field idField = Search.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(searchHistory, 1L);
        
        // 명시적인 Pageable 객체 생성
        Pageable pageable = PageRequest.of(0, 10);
        PageImpl<Search> page = new PageImpl<>(List.of(searchHistory), pageable, 1);
        
        when(searchUseCase.getUserSearchHistory(eq(userId), any())).thenReturn(page);
        
        // when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/search/history/{userId}", userId)
                .with(testUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andDo(document("search/get-search-history",
                    pathParameters(
                        parameterWithName("userId").description("사용자 ID")
                    ),
                    relaxedResponseFields(
                        RestDocsUtils.getCommonResponseFieldsWithData())
                    .andWithPrefix("data.content[].", 
                        fieldWithPath("id").type(JsonFieldType.NUMBER).description("검색 기록 ID"),
                        fieldWithPath("keyword").type(JsonFieldType.STRING).description("검색 키워드"),
                        fieldWithPath("category").type(JsonFieldType.STRING).description("검색 카테고리"),
                        fieldWithPath("resultCount").type(JsonFieldType.NUMBER).description("검색 결과 수"),
                        fieldWithPath("searchedAt").type(JsonFieldType.STRING).description("검색 시간"),
                        fieldWithPath("userId").type(JsonFieldType.NULL).description("사용자 ID")
                    )
                    .andWithPrefix("data.", 
                        fieldWithPath("last").type(JsonFieldType.BOOLEAN).description("마지막 페이지 여부"),
                        fieldWithPath("totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 수"),
                        fieldWithPath("totalElements").type(JsonFieldType.NUMBER).description("전체 요소 수"),
                        fieldWithPath("size").type(JsonFieldType.NUMBER).description("페이지 크기"),
                        fieldWithPath("number").type(JsonFieldType.NUMBER).description("현재 페이지 번호"),
                        fieldWithPath("sort.empty").type(JsonFieldType.BOOLEAN).description("정렬 정보 존재 여부"),
                        fieldWithPath("sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬 여부"),
                        fieldWithPath("sort.unsorted").type(JsonFieldType.BOOLEAN).description("비정렬 여부"),
                        fieldWithPath("first").type(JsonFieldType.BOOLEAN).description("첫 페이지 여부"),
                        fieldWithPath("numberOfElements").type(JsonFieldType.NUMBER).description("현재 페이지 요소 수"), 
                        fieldWithPath("empty").type(JsonFieldType.BOOLEAN).description("페이지 결과 존재 여부")
                    )
                ));
        
        verify(searchUseCase).getUserSearchHistory(eq(userId), any());
    }
    
    @Test
    @DisplayName("검색 기록 삭제 API가 정상적으로 동작해야 한다")
    void deleteSearchHistory() throws Exception {
        // given
        Long searchId = 1L;
        
        // when & then
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/search/history/{searchId}", searchId)
                .with(testUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("검색 기록이 삭제되었습니다."))
                .andDo(document("search/delete-search-history",
                    pathParameters(
                        parameterWithName("searchId").description("검색 기록 ID")
                    ),
                    responseFields(
                        fieldWithPath("result").type(JsonFieldType.STRING).description("결과 상태 (SUCCESS)"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("성공 메시지"),
                        fieldWithPath("data").type(JsonFieldType.NULL).description("데이터 (없음)")
                    )
                ));
        
        verify(searchUseCase).deleteSearchHistory(searchId);
    }
} 