package com.j30n.stoblyx.api;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Book API 통합 테스트 클래스
 */
@DisplayName("Book API 통합 테스트")
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class BookApiTest extends BaseApiTest {

    private static final String BOOK_API_PATH = "/books";

    @Test
    @DisplayName("책 목록 조회 API 테스트")
    public void testGetBooks() {
        given(requestSpec)
            .when()
                .get(BOOK_API_PATH)
            .then()
                .statusCode(200)
                .body("result", equalTo("SUCCESS"))
                .body("data", notNullValue())
                .body("data.content", instanceOf(Iterable.class));
    }

    @Test
    @DisplayName("책 상세 조회 API 테스트")
    public void testGetBook() {
        // 먼저 책 목록을 조회하여 첫 번째 책의 ID를 가져옴
        Integer bookId = given(requestSpec)
                .when()
                    .get(BOOK_API_PATH)
                .then()
                    .statusCode(200)
                    .extract()
                    .path("data.content[0].id");

        // ID가 없는 경우 테스트 데이터 생성
        if (bookId == null) {
            bookId = createTestBook();
        }

        // 책 상세 조회 테스트
        given(requestSpec)
            .when()
                .get(BOOK_API_PATH + "/" + bookId)
            .then()
                .statusCode(200)
                .body("result", equalTo("SUCCESS"))
                .body("data.id", equalTo(bookId))
                .body("data.title", notNullValue())
                .body("data.author", notNullValue());
    }

    @Test
    @DisplayName("책 등록 API 테스트")
    public void testCreateBook() {
        // 책 등록 요청 데이터 생성
        Map<String, String> bookData = new HashMap<>();
        bookData.put("title", "테스트 책 제목");
        bookData.put("author", "테스트 저자");
        bookData.put("description", "테스트 책 설명입니다.");
        bookData.put("coverImageUrl", "https://example.com/cover.jpg");

        // 인증된 요청으로 책 등록
        given(givenAuth(userToken))
            .contentType(ContentType.JSON)
            .body(bookData)
            .when()
                .post(BOOK_API_PATH)
            .then()
                .statusCode(201)
                .body("result", equalTo("SUCCESS"))
                .body("data.id", notNullValue())
                .body("data.title", equalTo("테스트 책 제목"))
                .body("data.author", equalTo("테스트 저자"));
    }

    @Test
    @DisplayName("책 수정 API 테스트")
    public void testUpdateBook() {
        // 테스트용 책 생성
        Integer bookId = createTestBook();

        // 책 수정 요청 데이터 생성
        Map<String, String> updateData = new HashMap<>();
        updateData.put("title", "수정된 책 제목");
        updateData.put("author", "수정된 저자");
        updateData.put("description", "수정된 책 설명입니다.");

        // 인증된 요청으로 책 수정
        given(givenAuth(userToken))
            .contentType(ContentType.JSON)
            .body(updateData)
            .when()
                .put(BOOK_API_PATH + "/" + bookId)
            .then()
                .statusCode(200)
                .body("result", equalTo("SUCCESS"))
                .body("data.id", equalTo(bookId))
                .body("data.title", equalTo("수정된 책 제목"))
                .body("data.author", equalTo("수정된 저자"));
    }

    @Test
    @DisplayName("책 삭제 API 테스트")
    public void testDeleteBook() {
        // 테스트용 책 생성
        Integer bookId = createTestBook();

        // 인증된 요청으로 책 삭제
        given(givenAuth(userToken))
            .when()
                .delete(BOOK_API_PATH + "/" + bookId)
            .then()
                .statusCode(204);

        // 삭제된 책 조회 시 404 응답 확인
        given(requestSpec)
            .when()
                .get(BOOK_API_PATH + "/" + bookId)
            .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("인증 없이 책 등록 시 실패 테스트")
    public void testCreateBookWithoutAuth() {
        // 책 등록 요청 데이터 생성
        Map<String, String> bookData = new HashMap<>();
        bookData.put("title", "테스트 책 제목");
        bookData.put("author", "테스트 저자");

        // 인증 없이 책 등록 요청
        given(requestSpec)
            .contentType(ContentType.JSON)
            .body(bookData)
            .when()
                .post(BOOK_API_PATH)
            .then()
                .statusCode(401); // 인증 실패 응답 코드
    }

    /**
     * 테스트용 책 데이터 생성 및 책 ID 반환
     */
    private Integer createTestBook() {
        // 책 등록 요청 데이터 생성
        Map<String, String> bookData = new HashMap<>();
        bookData.put("title", "테스트 책 제목");
        bookData.put("author", "테스트 저자");
        bookData.put("description", "테스트 책 설명입니다.");
        bookData.put("coverImageUrl", "https://example.com/cover.jpg");

        // 책 등록하고 생성된 ID 반환
        return given(givenAuth(userToken))
            .contentType(ContentType.JSON)
            .body(bookData)
            .when()
                .post(BOOK_API_PATH)
            .then()
                .statusCode(201)
                .extract()
                .path("data.id");
    }
} 