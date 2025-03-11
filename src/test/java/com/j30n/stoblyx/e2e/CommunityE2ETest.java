package com.j30n.stoblyx.e2e;

import com.j30n.stoblyx.e2e.util.AuthTestHelper;
import com.j30n.stoblyx.e2e.util.E2ETestExtension;
import com.j30n.stoblyx.e2e.util.TestDataGenerator;
import com.j30n.stoblyx.e2e.util.TestDataGenerator.TestUser;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * 커뮤니티 기능 E2E 테스트 클래스
 */
@ExtendWith(E2ETestExtension.class)
@TestMethodOrder(OrderAnnotation.class)
@DisplayName("커뮤니티 기능 E2E 테스트")
@Tag("e2e")
class CommunityE2ETest extends BaseE2ETest {

    private TestUser testUser;
    private TestUser secondUser;
    private AuthTestHelper authHelper;
    private AuthTestHelper secondAuthHelper;
    private String createdContentId;
    private String createdCommentId;
    private String createdGroupId;
    
    @Override
    public void setUpAll() {
        super.setUpAll();
        testUser = TestDataGenerator.generateTestUser();
        secondUser = TestDataGenerator.generateTestUser();
    }
    
    @BeforeEach
    public void setUp() {
        super.setUp();
        authHelper = new AuthTestHelper(createRequestSpec());
        secondAuthHelper = new AuthTestHelper(createRequestSpec());
    }
    
    @Test
    @Order(1)
    @DisplayName("사용자 등록 및 로그인")
    void testUserRegistrationAndLogin() {
        // 첫 번째 사용자 회원가입
        Response signUpResponse = authHelper.signUp(
            testUser.getUsername(),
            testUser.getEmail(),
            testUser.getPassword(),
            testUser.getNickname()
        );
        
        assertThat("첫 번째 사용자 회원가입 성공", signUpResponse.getStatusCode(), 
            anyOf(is(HttpStatus.OK.value()), is(HttpStatus.CREATED.value())));
        
        // 첫 번째 사용자 로그인
        Response loginResponse = authHelper.login(
            testUser.getUsername(),
            testUser.getPassword()
        );
        
        assertThat("첫 번째 사용자 로그인 성공", loginResponse.getStatusCode(), is(HttpStatus.OK.value()));
        assertThat("첫 번째 사용자 액세스 토큰 존재", authHelper.getAccessToken(), notNullValue());
        
        // 두 번째 사용자 회원가입
        Response secondSignUpResponse = secondAuthHelper.signUp(
            secondUser.getUsername(),
            secondUser.getEmail(),
            secondUser.getPassword(),
            secondUser.getNickname()
        );
        
        assertThat("두 번째 사용자 회원가입 성공", secondSignUpResponse.getStatusCode(), 
            anyOf(is(HttpStatus.OK.value()), is(HttpStatus.CREATED.value())));
    }
    
    @Test
    @Order(2)
    @DisplayName("콘텐츠 생성 테스트")
    void testCreateContent() {
        // 회원가입 및 로그인이 되어 있지 않은 경우 먼저 실행
        if (authHelper.getAccessToken() == null) {
            testUserRegistrationAndLogin();
        }
        
        // 명언 생성 요청
        Map<String, Object> quoteData = new HashMap<>();
        quoteData.put("content", "성공한 사람이 되려고 노력하기보다 가치있는 사람이 되려고 노력하라.");
        quoteData.put("author", "알버트 아인슈타인");
        quoteData.put("tags", new String[]{"성공", "가치", "노력"});
        
        Response quoteResponse = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .body(quoteData)
            .when()
            .post("/quotes");
            
        assertThat("명언 생성 성공", quoteResponse.getStatusCode(), is(HttpStatus.CREATED.value()));
        String quoteId = quoteResponse.path("data.id").toString();
        
        // 콘텐츠 생성 요청
        Map<String, Object> contentData = new HashMap<>();
        contentData.put("quoteId", quoteId);
        contentData.put("title", "가치 있는 삶을 위한 성찰");
        contentData.put("type", "ARTICLE");
        contentData.put("content", "아인슈타인의 명언을 통해 배우는 가치 있는 삶에 대한 고찰");
        contentData.put("tags", new String[]{"철학", "삶", "성찰"});
        
        Response response = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .body(contentData)
            .when()
            .post("/contents");
            
        // API 응답이 비동기일 경우를 고려
        assertThat("콘텐츠 생성 요청 성공", response.getStatusCode(), 
            anyOf(is(HttpStatus.OK.value()), is(HttpStatus.CREATED.value())));
        
        // 생성된 콘텐츠 ID가 있는 경우 저장
        if (response.path("data.id") != null) {
            createdContentId = response.path("data.id").toString();
            assertThat("콘텐츠 ID 존재", createdContentId, notNullValue());
        }
    }
    
    @Test
    @Order(3)
    @DisplayName("댓글 작성 테스트")
    void testCreateComment() {
        // 회원가입 및 로그인이 되어 있지 않은 경우 먼저 실행
        if (authHelper.getAccessToken() == null) {
            testUserRegistrationAndLogin();
        }
        
        // 콘텐츠 생성이 되어있지 않은 경우 먼저 생성
        if (createdContentId == null) {
            testCreateContent();
        }
        
        // 댓글 작성 요청
        Map<String, Object> commentData = new HashMap<>();
        commentData.put("contentId", createdContentId);
        commentData.put("text", "정말 인상 깊은 글입니다. 많은 생각을 하게 되네요.");
        
        Response response = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .body(commentData)
            .when()
            .post("/comments");
            
        assertThat("댓글 작성 성공", response.getStatusCode(), is(HttpStatus.CREATED.value()));
        assertThat("응답 결과 성공", response.path("result").toString(), 
            equalToIgnoringCase("success"));
        
        // 생성된 댓글 ID 저장
        createdCommentId = response.path("data.id").toString();
        assertThat("댓글 ID 존재", createdCommentId, notNullValue());
    }
    
    @Test
    @Order(4)
    @DisplayName("댓글에 답글 작성 테스트")
    void testCreateReply() {
        // 회원가입 및 로그인이 되어 있지 않은 경우 먼저 실행
        if (authHelper.getAccessToken() == null) {
            testUserRegistrationAndLogin();
        }
        
        // 댓글 작성이 되어있지 않은 경우 먼저 생성
        if (createdCommentId == null) {
            testCreateComment();
        }
        
        // 답글 작성 요청
        Map<String, Object> replyData = new HashMap<>();
        replyData.put("parentId", createdCommentId);
        replyData.put("contentId", createdContentId);
        replyData.put("text", "감사합니다. 더 좋은 글로 보답하겠습니다.");
        
        Response response = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .body(replyData)
            .when()
            .post("/comments");
            
        assertThat("답글 작성 성공", response.getStatusCode(), is(HttpStatus.CREATED.value()));
        assertThat("응답 결과 성공", response.path("result").toString(), 
            equalToIgnoringCase("success"));
    }
    
    @Test
    @Order(5)
    @DisplayName("사용자 팔로우 테스트")
    void testFollowUser() {
        // 회원가입 및 로그인이 되어 있지 않은 경우 먼저 실행
        if (authHelper.getAccessToken() == null || secondAuthHelper.getAccessToken() == null) {
            testUserRegistrationAndLogin();
            
            // 두 번째 사용자 로그인
            Response secondLoginResponse = secondAuthHelper.login(
                secondUser.getUsername(),
                secondUser.getPassword()
            );
            
            assertThat("두 번째 사용자 로그인 성공", 
                secondLoginResponse.getStatusCode(), is(HttpStatus.OK.value()));
        }
        
        // 사용자 정보 조회 (팔로우 대상 사용자 ID 획득)
        Response userResponse = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .when()
            .get("/users/me");
            
        String targetUserId = userResponse.path("data.id").toString();
        
        // 사용자 팔로우 요청 (두 번째 사용자가 첫 번째 사용자를 팔로우)
        Response followResponse = createRequestSpec()
            .headers(secondAuthHelper.getAuthHeaders())
            .when()
            .post("/users/" + targetUserId + "/follow");
            
        assertThat("사용자 팔로우 성공", followResponse.getStatusCode(), is(HttpStatus.OK.value()));
        assertThat("응답 결과 성공", followResponse.path("result").toString(), 
            equalToIgnoringCase("success"));
    }
    
    @Test
    @Order(6)
    @DisplayName("팔로워 및 팔로잉 목록 조회 테스트")
    void testGetFollowersAndFollowing() {
        // 회원가입 및 로그인이 되어 있지 않은 경우 먼저 실행
        if (authHelper.getAccessToken() == null) {
            testUserRegistrationAndLogin();
            testFollowUser();
        }
        
        // 팔로워 목록 조회 요청
        Response followersResponse = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .when()
            .get("/users/me/followers");
            
        assertThat("팔로워 목록 조회 성공", followersResponse.getStatusCode(), is(HttpStatus.OK.value()));
        assertThat("응답 결과 성공", followersResponse.path("result").toString(), 
            equalToIgnoringCase("success"));
            
        // 팔로잉 목록 조회 요청
        Response followingResponse = createRequestSpec()
            .headers(secondAuthHelper.getAuthHeaders())
            .when()
            .get("/users/me/following");
            
        assertThat("팔로잉 목록 조회 성공", followingResponse.getStatusCode(), is(HttpStatus.OK.value()));
        assertThat("응답 결과 성공", followingResponse.path("result").toString(), 
            equalToIgnoringCase("success"));
    }
    
    @Test
    @Order(7)
    @DisplayName("그룹 생성 테스트")
    void testCreateGroup() {
        // 회원가입 및 로그인이 되어 있지 않은 경우 먼저 실행
        if (authHelper.getAccessToken() == null) {
            testUserRegistrationAndLogin();
        }
        
        // 그룹 생성 요청
        Map<String, Object> groupData = new HashMap<>();
        groupData.put("name", "철학 토론 모임");
        groupData.put("description", "철학적 사고와 명언에 대해 토론하는 모임입니다.");
        groupData.put("isPrivate", false);
        groupData.put("tags", new String[]{"철학", "토론", "명언"});
        
        Response response = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .body(groupData)
            .when()
            .post("/groups");
            
        assertThat("그룹 생성 성공", response.getStatusCode(), is(HttpStatus.CREATED.value()));
        assertThat("응답 결과 성공", response.path("result").toString(), 
            equalToIgnoringCase("success"));
        
        // 생성된 그룹 ID 저장
        createdGroupId = response.path("data.id").toString();
        assertThat("그룹 ID 존재", createdGroupId, notNullValue());
    }
    
    @Test
    @Order(8)
    @DisplayName("그룹 초대 및 가입 테스트")
    void testInviteAndJoinGroup() {
        // 회원가입 및 로그인이 되어 있지 않은 경우 먼저 실행
        if (authHelper.getAccessToken() == null || secondAuthHelper.getAccessToken() == null) {
            testUserRegistrationAndLogin();
            
            // 두 번째 사용자 로그인
            Response secondLoginResponse = secondAuthHelper.login(
                secondUser.getUsername(),
                secondUser.getPassword()
            );
            
            assertThat("두 번째 사용자 로그인 성공", 
                secondLoginResponse.getStatusCode(), is(HttpStatus.OK.value()));
        }
        
        // 그룹 생성이 되어있지 않은 경우 먼저 생성
        if (createdGroupId == null) {
            testCreateGroup();
        }
        
        // 사용자 정보 조회 (초대할 사용자 ID 획득)
        Response userResponse = createRequestSpec()
            .headers(secondAuthHelper.getAuthHeaders())
            .when()
            .get("/users/me");
            
        String inviteeUserId = userResponse.path("data.id").toString();
        
        // 그룹 초대 요청
        Map<String, Object> inviteData = new HashMap<>();
        inviteData.put("userId", inviteeUserId);
        
        Response inviteResponse = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .body(inviteData)
            .when()
            .post("/groups/" + createdGroupId + "/invite");
            
        assertThat("그룹 초대 성공", inviteResponse.getStatusCode(), is(HttpStatus.OK.value()));
        
        // 그룹 가입 요청
        Response joinResponse = createRequestSpec()
            .headers(secondAuthHelper.getAuthHeaders())
            .when()
            .post("/groups/" + createdGroupId + "/join");
            
        assertThat("그룹 가입 성공", joinResponse.getStatusCode(), is(HttpStatus.OK.value()));
        assertThat("응답 결과 성공", joinResponse.path("result").toString(), 
            equalToIgnoringCase("success"));
    }
    
    @Test
    @Order(9)
    @DisplayName("그룹 게시물 작성 테스트")
    void testCreateGroupPost() {
        // 회원가입 및 로그인이 되어 있지 않은 경우 먼저 실행
        if (authHelper.getAccessToken() == null) {
            testUserRegistrationAndLogin();
        }
        
        // 그룹 생성이 되어있지 않은 경우 먼저 생성
        if (createdGroupId == null) {
            testCreateGroup();
        }
        
        // 그룹 게시물 작성 요청
        Map<String, Object> postData = new HashMap<>();
        postData.put("groupId", createdGroupId);
        postData.put("title", "오늘의 명언 토론");
        postData.put("content", "아인슈타인의 '성공한 사람이 되려고 노력하기보다 가치있는 사람이 되려고 노력하라'에 대해 토론해봅시다.");
        postData.put("tags", new String[]{"토론", "명언", "아인슈타인"});
        
        Response response = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .body(postData)
            .when()
            .post("/groups/" + createdGroupId + "/posts");
            
        assertThat("그룹 게시물 작성 성공", response.getStatusCode(), is(HttpStatus.CREATED.value()));
        assertThat("응답 결과 성공", response.path("result").toString(), 
            equalToIgnoringCase("success"));
    }
    
    @Test
    @Order(10)
    @DisplayName("그룹 게시물 목록 조회 테스트")
    void testGetGroupPosts() {
        // 회원가입 및 로그인이 되어 있지 않은 경우 먼저 실행
        if (authHelper.getAccessToken() == null) {
            testUserRegistrationAndLogin();
        }
        
        // 그룹 생성이 되어있지 않은 경우 먼저 생성
        if (createdGroupId == null) {
            testCreateGroup();
            testCreateGroupPost();
        }
        
        // 그룹 게시물 목록 조회 요청
        Response response = createRequestSpec()
            .headers(authHelper.getAuthHeaders())
            .when()
            .get("/groups/" + createdGroupId + "/posts");
            
        assertThat("그룹 게시물 목록 조회 성공", response.getStatusCode(), is(HttpStatus.OK.value()));
        assertThat("응답 결과 성공", response.path("result").toString(), 
            equalToIgnoringCase("success"));
    }
} 