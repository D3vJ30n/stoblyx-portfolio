package com.j30n.stoblyx.adapter.in.web.controller;

import com.j30n.stoblyx.adapter.in.web.dto.content.ContentResponse;
import com.j30n.stoblyx.adapter.in.web.dto.bookmark.BookmarkStatusResponse;
import com.j30n.stoblyx.application.service.content.ContentService;
import com.j30n.stoblyx.application.service.bookmark.BookmarkService;
import com.j30n.stoblyx.config.SecurityTestConfig;
import com.j30n.stoblyx.config.ContextTestConfig;
import com.j30n.stoblyx.config.MonitoringTestConfig;
import com.j30n.stoblyx.config.XssTestConfig;
import com.j30n.stoblyx.domain.enums.ContentStatus;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ContentController.class)
@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureRestDocs
@Import({SecurityTestConfig.class, ContextTestConfig.class, XssTestConfig.class, MonitoringTestConfig.class})
@DisplayName("ContentController 테스트")
class ContentControllerTest {

    private MockMvc mockMvc;
    
    @Autowired
    private WebApplicationContext context;
    
    private RequestPostProcessor testUser;

    @MockBean
    private ContentService contentService;
    
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
    @DisplayName("콘텐츠 생성 API가 정상적으로 동작해야 한다")
    void generateContent() throws Exception {
        // given
        Long quoteId = 1L;
        ContentResponse response = ContentResponse.builder()
                .id(1L)
                .subtitles("테스트 콘텐츠 내용")
                .status(ContentStatus.PUBLISHED)
                .videoUrl("http://example.com/video.mp4")
                .thumbnailUrl("http://example.com/thumbnail.jpg")
                .bgmUrl("http://example.com/audio.mp3")
                .viewCount(0)
                .likeCount(0)
                .shareCount(0)
                .isLiked(false)
                .isBookmarked(false)
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build();
                
        when(contentService.generateContent(anyLong())).thenReturn(response);
        
        // when & then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/contents/quotes/{quoteId}", quoteId)
                    .with(testUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andDo(document("content/generate-content",
                    pathParameters(
                        parameterWithName("quoteId").description("인용구 ID")
                    ),
                    responseFields(
                        RestDocsUtils.getCommonResponseFieldsWithData())
                    .andWithPrefix("data.", 
                        fieldWithPath("id").type(JsonFieldType.NUMBER).description("콘텐츠 ID"),
                        fieldWithPath("subtitles").type(JsonFieldType.STRING).description("콘텐츠 내용"),
                        fieldWithPath("status").type(JsonFieldType.STRING).description("콘텐츠 상태"),
                        fieldWithPath("videoUrl").type(JsonFieldType.STRING).description("비디오 URL"),
                        fieldWithPath("thumbnailUrl").type(JsonFieldType.STRING).description("썸네일 URL"),
                        fieldWithPath("bgmUrl").type(JsonFieldType.STRING).description("BGM URL"),
                        fieldWithPath("viewCount").type(JsonFieldType.NUMBER).description("조회수"),
                        fieldWithPath("likeCount").type(JsonFieldType.NUMBER).description("좋아요 수"),
                        fieldWithPath("shareCount").type(JsonFieldType.NUMBER).description("공유 수"),
                        fieldWithPath("isLiked").type(JsonFieldType.BOOLEAN).description("좋아요 여부"),
                        fieldWithPath("isBookmarked").type(JsonFieldType.BOOLEAN).description("북마크 여부"),
                        fieldWithPath("book").type(JsonFieldType.OBJECT).description("책 정보").optional(),
                        fieldWithPath("quote").type(JsonFieldType.OBJECT).description("인용구 정보").optional(),
                        fieldWithPath("createdAt").type(JsonFieldType.STRING).description("생성 시간"),
                        fieldWithPath("modifiedAt").type(JsonFieldType.STRING).description("수정 시간")
                    )
                ));
    }

    @Test
    @DisplayName("콘텐츠 조회 API가 정상적으로 동작해야 한다")
    void getContent() throws Exception {
        // given
        Long contentId = 1L;
        ContentResponse response = ContentResponse.builder()
                .id(contentId)
                .subtitles("테스트 콘텐츠 내용")
                .status(ContentStatus.PUBLISHED)
                .videoUrl("http://example.com/video.mp4")
                .thumbnailUrl("http://example.com/thumbnail.jpg")
                .bgmUrl("http://example.com/audio.mp3")
                .viewCount(0)
                .likeCount(0)
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build();
                
        when(contentService.getContent(eq(contentId))).thenReturn(response);
        
        // when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/contents/{contentId}", contentId)
                    .with(testUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.data.id").value(contentId))
                .andDo(document("content/get-content",
                    pathParameters(
                        parameterWithName("contentId").description("콘텐츠 ID")
                    ),
                    responseFields(
                        RestDocsUtils.getCommonResponseFieldsWithData())
                    .andWithPrefix("data.", 
                        fieldWithPath("id").type(JsonFieldType.NUMBER).description("콘텐츠 ID"),
                        fieldWithPath("subtitles").type(JsonFieldType.STRING).description("콘텐츠 내용"),
                        fieldWithPath("status").type(JsonFieldType.STRING).description("콘텐츠 상태"),
                        fieldWithPath("videoUrl").type(JsonFieldType.STRING).description("비디오 URL"),
                        fieldWithPath("thumbnailUrl").type(JsonFieldType.STRING).description("썸네일 URL"),
                        fieldWithPath("bgmUrl").type(JsonFieldType.STRING).description("BGM URL"),
                        fieldWithPath("viewCount").type(JsonFieldType.NUMBER).description("조회수"),
                        fieldWithPath("likeCount").type(JsonFieldType.NUMBER).description("좋아요 수"),
                        fieldWithPath("shareCount").type(JsonFieldType.NUMBER).description("공유 수"),
                        fieldWithPath("isLiked").type(JsonFieldType.BOOLEAN).description("좋아요 여부"),
                        fieldWithPath("isBookmarked").type(JsonFieldType.BOOLEAN).description("북마크 여부"),
                        fieldWithPath("book").type(JsonFieldType.OBJECT).description("책 정보").optional(),
                        fieldWithPath("quote").type(JsonFieldType.OBJECT).description("인용구 정보").optional(),
                        fieldWithPath("createdAt").type(JsonFieldType.STRING).description("생성 시간"),
                        fieldWithPath("modifiedAt").type(JsonFieldType.STRING).description("수정 시간")
                    )
                ));
    }

    @Test
    @DisplayName("트렌딩 콘텐츠 목록 조회 API가 정상적으로 동작해야 한다")
    void getTrendingContents() throws Exception {
        // given
        List<ContentResponse> contents = List.of(
            ContentResponse.builder()
                .id(1L)
                .subtitles("트렌딩 콘텐츠 내용 1")
                .status(ContentStatus.PUBLISHED)
                .videoUrl("http://example.com/video1.mp4")
                .thumbnailUrl("http://example.com/thumbnail1.jpg")
                .bgmUrl("http://example.com/audio1.mp3")
                .viewCount(150)
                .likeCount(50)
                .shareCount(10)
                .isLiked(false)
                .isBookmarked(false)
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build(),
            ContentResponse.builder()
                .id(2L)
                .subtitles("트렌딩 콘텐츠 내용 2")
                .status(ContentStatus.PUBLISHED)
                .videoUrl("http://example.com/video2.mp4")
                .thumbnailUrl("http://example.com/thumbnail2.jpg")
                .bgmUrl("http://example.com/audio2.mp3")
                .viewCount(120)
                .likeCount(30)
                .shareCount(5)
                .isLiked(false)
                .isBookmarked(false)
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build()
        );
        
        Pageable pageable = PageRequest.of(0, 10);
        PageImpl<ContentResponse> page = new PageImpl<>(contents, pageable, contents.size());
        when(contentService.getTrendingContents(any())).thenReturn(page);
        
        // when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/contents/trending")
                    .with(testUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.data.content[0].id").value(1))
                .andDo(document("content/get-trending-contents",
                    queryParameters(
                        parameterWithName("page").description("페이지 번호 (0부터 시작)").optional(),
                        parameterWithName("size").description("페이지 크기").optional(),
                        parameterWithName("sort").description("정렬 방식 (예: viewCount,desc)").optional()
                    ),
                    responseFields(
                        RestDocsUtils.getCommonResponseFieldsWithData())
                    .andWithPrefix("data.content[].", 
                        fieldWithPath("id").type(JsonFieldType.NUMBER).description("콘텐츠 ID"),
                        fieldWithPath("subtitles").type(JsonFieldType.STRING).description("콘텐츠 내용"),
                        fieldWithPath("status").type(JsonFieldType.STRING).description("콘텐츠 상태"),
                        fieldWithPath("videoUrl").type(JsonFieldType.STRING).description("비디오 URL"),
                        fieldWithPath("thumbnailUrl").type(JsonFieldType.STRING).description("썸네일 URL"),
                        fieldWithPath("bgmUrl").type(JsonFieldType.STRING).description("BGM URL"),
                        fieldWithPath("viewCount").type(JsonFieldType.NUMBER).description("조회수"),
                        fieldWithPath("likeCount").type(JsonFieldType.NUMBER).description("좋아요 수"),
                        fieldWithPath("shareCount").type(JsonFieldType.NUMBER).description("공유 수"),
                        fieldWithPath("isLiked").type(JsonFieldType.BOOLEAN).description("좋아요 여부"),
                        fieldWithPath("isBookmarked").type(JsonFieldType.BOOLEAN).description("북마크 여부"),
                        fieldWithPath("book").type(JsonFieldType.OBJECT).description("책 정보").optional(),
                        fieldWithPath("quote").type(JsonFieldType.OBJECT).description("인용구 정보").optional(),
                        fieldWithPath("createdAt").type(JsonFieldType.STRING).description("생성 시간"),
                        fieldWithPath("modifiedAt").type(JsonFieldType.STRING).description("수정 시간")
                    )
                    .and(RestDocsUtils.getPageResponseFields())
                ));
    }

    @Test
    @DisplayName("추천 콘텐츠 목록 조회 API가 정상적으로 동작해야 한다")
    void getRecommendedContents() throws Exception {
        // given
        List<ContentResponse> contents = List.of(
            ContentResponse.builder()
                .id(1L)
                .subtitles("추천 콘텐츠 내용 1")
                .status(ContentStatus.PUBLISHED)
                .videoUrl("http://example.com/video1.mp4")
                .thumbnailUrl("http://example.com/thumbnail1.jpg")
                .bgmUrl("http://example.com/audio1.mp3")
                .viewCount(150)
                .likeCount(50)
                .shareCount(10)
                .isLiked(false)
                .isBookmarked(false)
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build(),
            ContentResponse.builder()
                .id(2L)
                .subtitles("추천 콘텐츠 내용 2")
                .status(ContentStatus.PUBLISHED)
                .videoUrl("http://example.com/video2.mp4")
                .thumbnailUrl("http://example.com/thumbnail2.jpg")
                .bgmUrl("http://example.com/audio2.mp3")
                .viewCount(120)
                .likeCount(30)
                .shareCount(5)
                .isLiked(false)
                .isBookmarked(false)
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build()
        );
        
        Pageable pageable = PageRequest.of(0, 10);
        PageImpl<ContentResponse> page = new PageImpl<>(contents, pageable, contents.size());
        when(contentService.getRecommendedContents(eq(1L), any())).thenReturn(page);
        
        // when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/contents/recommended")
                    .with(testUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.data.content[0].id").value(1))
                .andDo(document("content/get-recommended-contents",
                    queryParameters(
                        parameterWithName("page").description("페이지 번호 (0부터 시작)").optional(),
                        parameterWithName("size").description("페이지 크기").optional(),
                        parameterWithName("sort").description("정렬 방식 (예: viewCount,desc)").optional()
                    ),
                    responseFields(
                        RestDocsUtils.getCommonResponseFieldsWithData())
                    .andWithPrefix("data.content[].", 
                        fieldWithPath("id").type(JsonFieldType.NUMBER).description("콘텐츠 ID"),
                        fieldWithPath("subtitles").type(JsonFieldType.STRING).description("콘텐츠 내용"),
                        fieldWithPath("status").type(JsonFieldType.STRING).description("콘텐츠 상태"),
                        fieldWithPath("videoUrl").type(JsonFieldType.STRING).description("비디오 URL"),
                        fieldWithPath("thumbnailUrl").type(JsonFieldType.STRING).description("썸네일 URL"),
                        fieldWithPath("bgmUrl").type(JsonFieldType.STRING).description("BGM URL"),
                        fieldWithPath("viewCount").type(JsonFieldType.NUMBER).description("조회수"),
                        fieldWithPath("likeCount").type(JsonFieldType.NUMBER).description("좋아요 수"),
                        fieldWithPath("shareCount").type(JsonFieldType.NUMBER).description("공유 수"),
                        fieldWithPath("isLiked").type(JsonFieldType.BOOLEAN).description("좋아요 여부"),
                        fieldWithPath("isBookmarked").type(JsonFieldType.BOOLEAN).description("북마크 여부"),
                        fieldWithPath("book").type(JsonFieldType.OBJECT).description("책 정보").optional(),
                        fieldWithPath("quote").type(JsonFieldType.OBJECT).description("인용구 정보").optional(),
                        fieldWithPath("createdAt").type(JsonFieldType.STRING).description("생성 시간"),
                        fieldWithPath("modifiedAt").type(JsonFieldType.STRING).description("수정 시간")
                    )
                    .and(RestDocsUtils.getPageResponseFields())
                ));
    }

    @Test
    @DisplayName("특정 책의 콘텐츠 목록 조회 API가 정상적으로 동작해야 한다")
    void getContentsByBook() throws Exception {
        // given
        Long bookId = 1L;
        List<ContentResponse> contents = List.of(
            ContentResponse.builder()
                .id(1L)
                .subtitles("책 콘텐츠 내용 1")
                .status(ContentStatus.PUBLISHED)
                .videoUrl("http://example.com/video1.mp4")
                .thumbnailUrl("http://example.com/thumbnail1.jpg")
                .bgmUrl("http://example.com/audio1.mp3")
                .viewCount(150)
                .likeCount(50)
                .shareCount(10)
                .isLiked(false)
                .isBookmarked(false)
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build(),
            ContentResponse.builder()
                .id(2L)
                .subtitles("책 콘텐츠 내용 2")
                .status(ContentStatus.PUBLISHED)
                .videoUrl("http://example.com/video2.mp4")
                .thumbnailUrl("http://example.com/thumbnail2.jpg")
                .bgmUrl("http://example.com/audio2.mp3")
                .viewCount(120)
                .likeCount(30)
                .shareCount(5)
                .isLiked(false)
                .isBookmarked(false)
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build()
        );
        
        Pageable pageable = PageRequest.of(0, 10);
        PageImpl<ContentResponse> page = new PageImpl<>(contents, pageable, contents.size());
        when(contentService.getContentsByBook(eq(bookId), any())).thenReturn(page);
        
        // when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/contents/books/{bookId}", bookId)
                    .with(testUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.data.content[0].id").value(1))
                .andDo(document("content/get-contents-by-book",
                    pathParameters(
                        parameterWithName("bookId").description("도서 ID")
                    ),
                    queryParameters(
                        parameterWithName("page").description("페이지 번호 (0부터 시작)").optional(),
                        parameterWithName("size").description("페이지 크기").optional(),
                        parameterWithName("sort").description("정렬 방식 (예: viewCount,desc)").optional()
                    ),
                    responseFields(
                        RestDocsUtils.getCommonResponseFieldsWithData())
                    .andWithPrefix("data.content[].", 
                        fieldWithPath("id").type(JsonFieldType.NUMBER).description("콘텐츠 ID"),
                        fieldWithPath("subtitles").type(JsonFieldType.STRING).description("콘텐츠 내용"),
                        fieldWithPath("status").type(JsonFieldType.STRING).description("콘텐츠 상태"),
                        fieldWithPath("videoUrl").type(JsonFieldType.STRING).description("비디오 URL"),
                        fieldWithPath("thumbnailUrl").type(JsonFieldType.STRING).description("썸네일 URL"),
                        fieldWithPath("bgmUrl").type(JsonFieldType.STRING).description("BGM URL"),
                        fieldWithPath("viewCount").type(JsonFieldType.NUMBER).description("조회수"),
                        fieldWithPath("likeCount").type(JsonFieldType.NUMBER).description("좋아요 수"),
                        fieldWithPath("shareCount").type(JsonFieldType.NUMBER).description("공유 수"),
                        fieldWithPath("isLiked").type(JsonFieldType.BOOLEAN).description("좋아요 여부"),
                        fieldWithPath("isBookmarked").type(JsonFieldType.BOOLEAN).description("북마크 여부"),
                        fieldWithPath("book").type(JsonFieldType.OBJECT).description("책 정보").optional(),
                        fieldWithPath("quote").type(JsonFieldType.OBJECT).description("인용구 정보").optional(),
                        fieldWithPath("createdAt").type(JsonFieldType.STRING).description("생성 시간"),
                        fieldWithPath("modifiedAt").type(JsonFieldType.STRING).description("수정 시간")
                    )
                    .and(RestDocsUtils.getPageResponseFields())
                ));
    }

    @Test
    @DisplayName("콘텐츠 검색 API가 정상적으로 동작해야 한다")
    void searchContents() throws Exception {
        // given
        String keyword = "테스트";
        List<ContentResponse> contents = List.of(
            ContentResponse.builder()
                .id(1L)
                .subtitles("테스트 콘텐츠 내용 1")
                .status(ContentStatus.PUBLISHED)
                .videoUrl("http://example.com/video1.mp4")
                .thumbnailUrl("http://example.com/thumbnail1.jpg")
                .bgmUrl("http://example.com/audio1.mp3")
                .viewCount(150)
                .likeCount(50)
                .shareCount(10)
                .isLiked(false)
                .isBookmarked(false)
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build(),
            ContentResponse.builder()
                .id(2L)
                .subtitles("테스트 콘텐츠 내용 2")
                .status(ContentStatus.PUBLISHED)
                .videoUrl("http://example.com/video2.mp4")
                .thumbnailUrl("http://example.com/thumbnail2.jpg")
                .bgmUrl("http://example.com/audio2.mp3")
                .viewCount(120)
                .likeCount(30)
                .shareCount(5)
                .isLiked(false)
                .isBookmarked(false)
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build()
        );
        
        Pageable pageable = PageRequest.of(0, 10);
        PageImpl<ContentResponse> page = new PageImpl<>(contents, pageable, contents.size());
        when(contentService.searchContents(eq(keyword), any())).thenReturn(page);
        
        // when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/contents/search")
                    .param("keyword", keyword)
                    .with(testUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.data.content[0].id").value(1))
                .andDo(document("content/search-contents",
                    queryParameters(
                        parameterWithName("keyword").description("검색 키워드"),
                        parameterWithName("page").description("페이지 번호 (0부터 시작)").optional(),
                        parameterWithName("size").description("페이지 크기").optional(),
                        parameterWithName("sort").description("정렬 방식 (예: viewCount,desc)").optional()
                    ),
                    responseFields(
                        RestDocsUtils.getCommonResponseFieldsWithData())
                    .andWithPrefix("data.content[].", 
                        fieldWithPath("id").type(JsonFieldType.NUMBER).description("콘텐츠 ID"),
                        fieldWithPath("subtitles").type(JsonFieldType.STRING).description("콘텐츠 내용"),
                        fieldWithPath("status").type(JsonFieldType.STRING).description("콘텐츠 상태"),
                        fieldWithPath("videoUrl").type(JsonFieldType.STRING).description("비디오 URL"),
                        fieldWithPath("thumbnailUrl").type(JsonFieldType.STRING).description("썸네일 URL"),
                        fieldWithPath("bgmUrl").type(JsonFieldType.STRING).description("BGM URL"),
                        fieldWithPath("viewCount").type(JsonFieldType.NUMBER).description("조회수"),
                        fieldWithPath("likeCount").type(JsonFieldType.NUMBER).description("좋아요 수"),
                        fieldWithPath("shareCount").type(JsonFieldType.NUMBER).description("공유 수"),
                        fieldWithPath("isLiked").type(JsonFieldType.BOOLEAN).description("좋아요 여부"),
                        fieldWithPath("isBookmarked").type(JsonFieldType.BOOLEAN).description("북마크 여부"),
                        fieldWithPath("book").type(JsonFieldType.OBJECT).description("책 정보").optional(),
                        fieldWithPath("quote").type(JsonFieldType.OBJECT).description("인용구 정보").optional(),
                        fieldWithPath("createdAt").type(JsonFieldType.STRING).description("생성 시간"),
                        fieldWithPath("modifiedAt").type(JsonFieldType.STRING).description("수정 시간")
                    )
                    .and(RestDocsUtils.getPageResponseFields())
                ));
    }

    @Test
    @DisplayName("콘텐츠 북마크 상태 확인 API가 정상적으로 동작해야 한다")
    void checkBookmarkStatus() throws Exception {
        // given
        Long contentId = 1L;
        BookmarkStatusResponse response = new BookmarkStatusResponse(true);
        
        when(bookmarkService.checkBookmarkStatus(anyLong(), eq(contentId))).thenReturn(response);
        
        // when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/contents/{contentId}/bookmark/status", contentId)
                    .with(testUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.data.isBookmarked").value(true))
                .andDo(document("content/check-bookmark-status",
                    pathParameters(
                        parameterWithName("contentId").description("콘텐츠 ID")
                    ),
                    responseFields(
                        RestDocsUtils.getCommonResponseFieldsWithData())
                    .andWithPrefix("data.", 
                        fieldWithPath("isBookmarked").type(JsonFieldType.BOOLEAN).description("북마크 여부")
                    )
                ));
    }
} 