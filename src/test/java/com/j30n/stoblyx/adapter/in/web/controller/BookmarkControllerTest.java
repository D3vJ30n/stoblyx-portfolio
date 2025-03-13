package com.j30n.stoblyx.adapter.in.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.j30n.stoblyx.adapter.in.web.dto.bookmark.BookmarkResponse;
import com.j30n.stoblyx.adapter.in.web.dto.bookmark.BulkDeleteRequest;
import com.j30n.stoblyx.application.service.bookmark.BookmarkService;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookmarkController.class)
@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureRestDocs
@ActiveProfiles("test")
@Import({SecurityTestConfig.class, ContextTestConfig.class, XssTestConfig.class, MonitoringTestConfig.class})
@DisplayName("북마크 컨트롤러 테스트")
class BookmarkControllerTest {

    private MockMvc mockMvc;
    
    @Autowired
    private WebApplicationContext context;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private RequestPostProcessor testUser;

    @MockBean
    private BookmarkService bookmarkService;

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
    @DisplayName("북마크 목록 조회 API가 정상적으로 동작해야 한다")
    void getBookmarks() throws Exception {
        // given
        LocalDateTime now = LocalDateTime.now();
        BookmarkResponse bookmark1 = new BookmarkResponse(1L, 101L, "첫 번째 북마크", "첫 번째 콘텐츠 설명", "thumbnail1.jpg", now);
        BookmarkResponse bookmark2 = new BookmarkResponse(2L, 102L, "두 번째 북마크", "두 번째 콘텐츠 설명", "thumbnail2.jpg", now);
        
        PageImpl<BookmarkResponse> bookmarkPage = new PageImpl<>(
            List.of(bookmark1, bookmark2),
            PageRequest.of(0, 10),
            2
        );
        
        when(bookmarkService.getBookmarks(anyLong(), anyString(), any(Pageable.class))).thenReturn(bookmarkPage);
        
        // when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/bookmarks")
                .param("type", "CONTENT")
                .param("page", "0")
                .param("size", "10")
                .with(testUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content[0].id").value(1))
                .andExpect(jsonPath("$.data.content[0].title").value("첫 번째 북마크"))
                .andDo(document("bookmark/get-bookmarks",
                    responseFields(RestDocsUtils.getBookmarkPageResponseFields())
                ));
    }

    @Test
    @DisplayName("북마크 일괄 삭제 API가 정상적으로 동작해야 한다")
    void bulkDeleteBookmarks() throws Exception {
        // given
        BulkDeleteRequest request = new BulkDeleteRequest(List.of(101L, 102L));
        
        doNothing().when(bookmarkService).deleteBookmarks(anyLong(), anyList());
        
        // when & then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/bookmarks/bulk-delete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(testUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andDo(document("bookmark/bulk-delete",
                    requestFields(
                        fieldWithPath("contentIds").type(JsonFieldType.ARRAY).description("삭제할 콘텐츠 ID 목록")
                    ),
                    responseFields(RestDocsUtils.getCommonResponseFieldsWithNullData())
                ));
    }
} 