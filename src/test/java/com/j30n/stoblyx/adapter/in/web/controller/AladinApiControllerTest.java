package com.j30n.stoblyx.adapter.in.web.controller;

import com.j30n.stoblyx.domain.model.Book;
import com.j30n.stoblyx.domain.model.BookInfo;
import com.j30n.stoblyx.infrastructure.external.AladinApiClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("알라딘 API 테스트 컨트롤러 테스트")
class AladinApiControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AladinApiClient aladinApiClient;

    @InjectMocks
    private AladinTestController aladinTestController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(aladinTestController).build();
    }

    @Test
    @DisplayName("키워드로 도서 검색이 정상적으로 동작해야 한다")
    void searchBooks() throws Exception {
        // given
        List<Book> mockBooks = Arrays.asList(
                new Book(BookInfo.builder()
                        .title("테스트 책 1")
                        .author("작가 1")
                        .isbn("1234567890")
                        .build()),
                new Book(BookInfo.builder()
                        .title("테스트 책 2")
                        .author("작가 2")
                        .isbn("0987654321")
                        .build())
        );

        when(aladinApiClient.searchBooks(anyString())).thenReturn(mockBooks);

        // when & then
        mockMvc.perform(get("/aladin/test/search")
                        .param("keyword", "테스트")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].title").value("테스트 책 1"))
                .andExpect(jsonPath("$.data[0].author").value("작가 1"))
                .andExpect(jsonPath("$.data[1].title").value("테스트 책 2"));
    }

    @Test
    @DisplayName("신간 도서 조회가 정상적으로 동작해야 한다")
    void getNewBooks() throws Exception {
        // given
        List<Book> mockBooks = Arrays.asList(
                new Book(BookInfo.builder()
                        .title("신간 책 1")
                        .author("작가 1")
                        .isbn("1111111111")
                        .build()),
                new Book(BookInfo.builder()
                        .title("신간 책 2")
                        .author("작가 2")
                        .isbn("2222222222")
                        .build())
        );

        when(aladinApiClient.getNewBooks()).thenReturn(mockBooks);

        // when & then
        mockMvc.perform(get("/aladin/test/new")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].title").value("신간 책 1"))
                .andExpect(jsonPath("$.data[1].title").value("신간 책 2"));
    }

    @Test
    @DisplayName("베스트셀러 조회가 정상적으로 동작해야 한다")
    void getBestSellers() throws Exception {
        // given
        List<Book> mockBooks = Arrays.asList(
                new Book(BookInfo.builder()
                        .title("베스트셀러 1")
                        .author("인기 작가 1")
                        .isbn("3333333333")
                        .build()),
                new Book(BookInfo.builder()
                        .title("베스트셀러 2")
                        .author("인기 작가 2")
                        .isbn("4444444444")
                        .build())
        );

        when(aladinApiClient.getBestSellers()).thenReturn(mockBooks);

        // when & then
        mockMvc.perform(get("/aladin/test/bestseller")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].title").value("베스트셀러 1"))
                .andExpect(jsonPath("$.data[1].title").value("베스트셀러 2"));
    }

    @Test
    @DisplayName("ISBN으로 도서 상세 조회가 정상적으로 동작해야 한다")
    void getBookByIsbn() throws Exception {
        // given
        Book mockBook = new Book(BookInfo.builder()
                .title("ISBN 테스트 책")
                .author("ISBN 작가")
                .isbn("9788956604992")
                .publisher("테스트 출판사")
                .description("책 설명입니다.")
                .build());

        when(aladinApiClient.getBookDetailByIsbn(anyString())).thenReturn(mockBook);

        // when & then
        mockMvc.perform(get("/aladin/test/book/9788956604992")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.data.title").value("ISBN 테스트 책"))
                .andExpect(jsonPath("$.data.author").value("ISBN 작가"))
                .andExpect(jsonPath("$.data.isbn").value("9788956604992"));
    }
} 