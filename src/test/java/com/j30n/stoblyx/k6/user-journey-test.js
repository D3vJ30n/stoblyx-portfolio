import http from "k6/http";
import { sleep, group } from "k6";
import { Rate } from "k6/metrics";
import {
  randomString,
  randomInt,
} from "https://jslib.k6.io/k6-utils/1.2.0/index.js";
// prettyJSON 함수를 직접 구현
function prettyJSON(obj) {
  return JSON.stringify(obj, null, 2);
}

// 에러율 측정을 위한 커스텀 메트릭
const errorRate = new Rate("errors");

// 테스트 설정
export const options = {
  vus: 1, // 가상 사용자 수
  iterations: 1, // 각 가상 사용자가 시나리오를 실행할 횟수
  thresholds: {
    errors: ["rate<0.1"], // 10% 이하의 에러율 허용
  },
};

// API 경로 설정
const BASE_URL = "http://localhost:8091";
const USER_API_PATH = `/users`;
const BOOK_API_PATH = `/books`;
const SEARCH_API_PATH = `/search`;
const CONTENT_API_PATH = `/contents`;
const QUOTE_API_PATH = `/quotes`;
const RANKING_API_PATH = `/ranking`;
const RECOMMENDATION_API_PATH = `/recommendations`;
const AUTH_API_PATH = "/auth";
const SIGNUP_PATH = `/signup`;

// 테스트에서 사용할 데이터 저장
let state = {
  authToken: null, // 초기에는 토큰이 없음
  bookId: null,
  quoteId: null,
  contentId: null,
  emailSuffix: randomString(8), // 랜덤 이메일 생성을 위한 접미사
  username: `testuser_${randomString(6)}`,
  password: "Test1234!",
  email: `testuser_${randomString(6)}@example.com`,
};

// 요청을 실행하고 결과를 CLI에 출력하는 함수
function executeRequest(description, method, path, body = null, params = null) {
  // 요청 시작 로그
  console.log(`\n===================================================`);
  console.log(`📌 API 요청: ${description}`);
  console.log(`📡 ${method} ${path}`);
  console.log(`---------------------------------------------------`);

  // 요청 정보 로깅
  logRequestDetails(method, path, params, body);

  // 헤더 설정 및 요청 실행
  const url = `${BASE_URL}${path}`;
  const headers = prepareHeaders();

  try {
    // HTTP 요청 실행
    const response = executeHttpRequest(method, url, body, headers, params);

    // 응답 처리
    return handleResponse(description, response);
  } catch (error) {
    console.log(`❌ 오류 발생: ${description} - ${method} ${path}`);
    console.log(`❌ 오류 메시지: ${error.message}`);
    errorRate.add(1);
    return null;
  } finally {
    console.log(`===================================================\n`);
  }
}

// 요청 정보를 로깅하는 함수
function logRequestDetails(method, path, params, body) {
  console.log(`📤 요청 메서드: ${method}`);
  console.log(`📤 요청 경로: ${path}`);

  // 쿼리 파라미터 로깅
  if (params) {
    console.log(`📤 쿼리 파라미터: ${JSON.stringify(params)}`);
  }

  // 요청 바디 로깅
  if (body) {
    console.log(`📤 요청 바디:`);
    console.log(prettyJSON(body));
  }
}

// 요청 헤더를 준비하는 함수
function prepareHeaders() {
  const headers = {
    "Content-Type": "application/json",
    Accept: "application/json",
  };

  // 인증이 필요한 경우 토큰 추가
  if (state.authToken) {
    headers["Authorization"] = state.authToken;
  }

  return headers;
}

// HTTP 요청을 실행하는 함수
function executeHttpRequest(method, url, body, headers, params) {
  switch (method.toUpperCase()) {
    case "GET":
      return http.get(url, { headers, params });
    case "POST":
      return http.post(url, body ? JSON.stringify(body) : "", { headers });
    case "PUT":
      return http.put(url, body ? JSON.stringify(body) : "", { headers });
    case "DELETE":
      return http.del(url, body ? JSON.stringify(body) : "", { headers });
    default:
      throw new Error(`지원하지 않는 HTTP 메서드: ${method}`);
  }
}

// 응답을 처리하는 함수
function handleResponse(description, response) {
  // 응답 상태 로깅
  console.log(`---------------------------------------------------`);
  console.log(`📥 응답 상태: ${response.status} ${response.status_text}`);

  // 응답 헤더 로깅
  logResponseHeaders(response);

  // 응답 바디 처리
  return processResponseBody(description, response);
}

// 응답 헤더를 로깅하는 함수
function logResponseHeaders(response) {
  console.log(`📥 주요 응답 헤더:`);
  if (response.headers["Content-Type"]) {
    console.log(`   Content-Type: ${response.headers["Content-Type"]}`);
  }
  if (response.headers["Content-Length"]) {
    console.log(`   Content-Length: ${response.headers["Content-Length"]}`);
  }
  if (response.headers["Authorization"]) {
    console.log(`   Authorization: ${response.headers["Authorization"]}`);
  }
}

// 응답 바디를 처리하는 함수
function processResponseBody(description, response) {
  if (response.body) {
    console.log(`📥 응답 바디:`);
    try {
      const responseBody = JSON.parse(response.body);
      console.log(prettyJSON(responseBody));

      // 응답 분석
      analyzeResponse(description, response.status, responseBody);

      return responseBody; // 응답 데이터 반환
    } catch (e) {
      console.log(`   ${response.body}`);
      return null;
    }
  } else {
    console.log(`📥 응답 바디: <비어 있음>`);
    return null;
  }
}

// 응답을 분석하고 비즈니스 로직 관점에서 로깅하는 함수
function analyzeResponse(description, statusCode, responseBody) {
  console.log(`📊 응답 분석:`);

  // 성공 케이스 분석
  if (statusCode >= 200 && statusCode < 300) {
    console.log(`   ✅ 요청 성공: ${description}`);

    // 응답 데이터 분석
    if (responseBody) {
      if (responseBody.result) {
        if (responseBody.result.toUpperCase() === "SUCCESS") {
          console.log(
            `   ✅ 비즈니스 로직 성공: result=${responseBody.result}`
          );
        } else {
          console.log(
            `   ⚠️ 비즈니스 로직 실패: result=${responseBody.result}`
          );
        }
      }

      // 데이터 필드 확인
      if ("data" in responseBody) {
        if (responseBody.data === null) {
          console.log(`   ℹ️ 데이터 없음: data=null`);
        } else if (
          typeof responseBody.data === "object" &&
          Object.keys(responseBody.data).length === 0
        ) {
          console.log(`   ℹ️ 데이터 비어있음: data={}`);
        } else if (
          Array.isArray(responseBody.data) &&
          responseBody.data.length === 0
        ) {
          console.log(`   ℹ️ 데이터 비어있음: data=[]`);
        } else {
          console.log(`   ✅ 데이터 존재함`);

          // 특정 API에 대한 세부 분석
          if (description.includes("책 검색") && responseBody.data.books) {
            console.log(
              `   📚 책 검색 결과: ${responseBody.data.books.length}개의 책 발견`
            );
          } else if (
            description.includes("인용구") &&
            Array.isArray(responseBody.data)
          ) {
            console.log(
              `   📝 인용구 결과: ${responseBody.data.length}개의 인용구 발견`
            );
          }
        }
      }

      // 메시지 필드 확인
      if (responseBody.message) {
        console.log(`   ℹ️ 응답 메시지: ${responseBody.message}`);
      }
    }
  }
  // 클라이언트 오류 분석
  else if (statusCode >= 400 && statusCode < 500) {
    console.log(`   ❌ 클라이언트 오류 (${statusCode}): ${description}`);
    errorRate.add(1);

    if (statusCode === 401) {
      console.log(
        `   🔐 인증 실패: 로그인이 필요하거나 유효하지 않은 토큰입니다.`
      );
    } else if (statusCode === 403) {
      console.log(`   🚫 권한 없음: 해당 리소스에 접근할 권한이 없습니다.`);
    } else if (statusCode === 404) {
      console.log(`   🔍 리소스 없음: 요청한 리소스를 찾을 수 없습니다.`);
    } else if (statusCode === 400) {
      console.log(`   ⚠️ 잘못된 요청: 요청 형식이 올바르지 않습니다.`);

      // 에러 메시지 확인
      if (responseBody && responseBody.message) {
        console.log(`   ❌ 오류 메시지: ${responseBody.message}`);
      }
    }
  }
  // 서버 오류 분석
  else if (statusCode >= 500) {
    console.log(`   ❌ 서버 오류 (${statusCode}): ${description}`);
    console.log(
      `   🛠️ 서버 측 문제로 요청을 처리할 수 없습니다. 시스템 관리자에게 문의하세요.`
    );
    errorRate.add(1);
  }
}

// k6 기본 함수: 초기 설정 진행
export function setup() {
  console.log(`API 엔드포인트 설정:`);
  console.log(`- BASE_URL: ${BASE_URL}`);
  console.log(`- USER_API_PATH: ${USER_API_PATH}`);
  console.log(`- SIGNUP_PATH: ${SIGNUP_PATH}`);

  return state;
}

// k6 기본 함수: 실제 테스트 실행
export default function (data) {
  state = Object.assign({}, data); // setup에서 반환된 상태 저장

  group("인증 단계", function () {
    // 사용자 회원가입 시도
    const randomUsername = `testuser_${randomString(6)}`;
    const randomEmail = `testuser_${randomString(6)}@example.com`;
    const randomNickname = `테스트사용자_${randomString(4)}`;

    // 회원가입 요청
    let signupResponse = executeRequest(
      "사용자 회원가입",
      "POST",
      `${AUTH_API_PATH}/signup`,
      {
        username: randomUsername,
        password: "Test1234!",
        email: randomEmail,
        nickname: randomNickname,
        agreeTerms: true,
      }
    );

    // 로그인 시도 - 올바른 필드 형식 사용
    let loginResponse = executeRequest(
      "사용자 로그인",
      "POST",
      `${AUTH_API_PATH}/login`,
      {
        username: randomUsername,
        password: "Test1234!",
      }
    );

    // 토큰 획득 시도
    if (
      loginResponse &&
      loginResponse.status === 200 &&
      loginResponse.result === "SUCCESS"
    ) {
      if (loginResponse.data && loginResponse.data.accessToken) {
        state.authToken = `Bearer ${loginResponse.data.accessToken}`;
        console.log(`🔑 로그인 성공, 실제 토큰 획득: ${state.authToken}`);
      }
    } else {
      // 로그인 실패 시 모의 토큰 사용
      state.authToken = "Bearer mock-token-for-testing";
      console.log(`⚠️ 로그인 실패, 모의 토큰 사용: ${state.authToken}`);
    }
  });

  console.log(`🔑 현재 사용 중인 토큰: ${state.authToken}`);

  group("1. 회원가입 및 로그인", () => {
    // 1.1 회원가입
    const signupData = {
      email: `test_user_${state.emailSuffix}@example.com`,
      password: "Test1234!",
      name: "테스트 사용자",
      nickname: `tester_${state.emailSuffix}`,
      username: `testuser_${state.emailSuffix}`,
    };

    const signUpResponse = executeRequest(
      "회원가입",
      "POST",
      `${AUTH_API_PATH}/signup`,
      signupData
    );

    // 1.2 로그인 - 올바른 필드 형식 사용
    const loginData = {
      username: signupData.username,
      password: signupData.password,
    };

    const loginResponse = executeRequest(
      "로그인",
      "POST",
      `${AUTH_API_PATH}/login`,
      loginData
    );

    // 로그인 응답에서 토큰 추출 (있는 경우)
    if (loginResponse && loginResponse.data && loginResponse.data.token) {
      state.authToken = `Bearer ${loginResponse.data.token}`;
      console.log(`토큰 저장: ${state.authToken.substring(0, 15)}...`);
    }

    // 1.3 사용자 관심사 설정
    const interestData = {
      genres: ["소설", "자기계발", "과학", "역사"],
      authors: ["김영하", "베르나르 베르베르"],
      keywords: ["미스터리", "SF", "판타지"],
    };

    executeRequest(
      "관심사 설정",
      "PUT",
      `${USER_API_PATH}/me/interests`,
      interestData
    );

    sleep(1);
  });

  group("2. 검색 기능", () => {
    // 2.1 인기 검색어 조회
    executeRequest(
      "인기 검색어 조회",
      "GET",
      `${SEARCH_API_PATH}/popular-terms`
    );

    // 2.2 책 검색
    const searchTerm = "초역 부처의 말";
    const searchResponse = executeRequest(
      "책 검색",
      "GET",
      SEARCH_API_PATH,
      null,
      { query: searchTerm }
    );

    // 검색 결과가 있는 경우 첫 번째 책 ID 사용
    if (
      searchResponse &&
      searchResponse.data &&
      searchResponse.data.books &&
      searchResponse.data.books.length > 0 &&
      searchResponse.data.books[0]
    ) {
      state.bookId = searchResponse.data.books[0].id;
      console.log(`책 ID 저장: ${state.bookId}`);
    } else {
      state.bookId = 1; // 기본값 설정
    }

    sleep(1);
  });

  group("3. 검색 결과 필터링 및 정렬", () => {
    // 3.1 장르별 필터링
    executeRequest("장르별 필터링", "GET", SEARCH_API_PATH, null, {
      query: "소설",
      genre: "판타지",
    });

    // 3.2 출판일 기준 정렬
    executeRequest("출판일 기준 정렬", "GET", SEARCH_API_PATH, null, {
      query: "소설",
      sort: "publishDate",
      order: "desc",
    });

    sleep(1);
  });

  group("4. 책 상세 정보 조회", () => {
    // 4.1 책 상세 정보 조회
    executeRequest(
      "책 상세 정보 조회",
      "GET",
      `${BOOK_API_PATH}/${state.bookId}`
    );

    // 4.2 책 요약 조회
    executeRequest(
      "책 요약 조회",
      "GET",
      `${BOOK_API_PATH}/${state.bookId}/summary`
    );

    // 4.3 책 인용구 목록 조회
    const quotesResponse = executeRequest(
      "인용구 목록 조회",
      "GET",
      `${BOOK_API_PATH}/${state.bookId}/quotes`
    );

    // 인용구가 있는 경우 첫 번째 인용구 ID 사용
    if (
      quotesResponse &&
      quotesResponse.data &&
      quotesResponse.data.quotes &&
      quotesResponse.data.quotes.length > 0
    ) {
      state.quoteId = quotesResponse.data.quotes[0].id;
      console.log(`인용구 ID 저장: ${state.quoteId}`);
    } else {
      // 인용구가 없는 경우 테스트용 인용구 생성
      const quoteData = {
        bookId: state.bookId,
        content: "지식이란 알면 알수록 더 많이 알아야 함을 깨닫는 것이다.",
        page: 42,
      };

      const createQuoteResponse = executeRequest(
        "인용구 생성",
        "POST",
        QUOTE_API_PATH,
        quoteData
      );

      // 응답에서 인용구 ID 추출 (있는 경우)
      if (
        createQuoteResponse &&
        createQuoteResponse.data &&
        createQuoteResponse.data.id
      ) {
        state.quoteId = createQuoteResponse.data.id;
        console.log(`생성된 인용구 ID: ${state.quoteId}`);
      } else {
        state.quoteId = 1; // 기본값 설정
      }
    }

    sleep(1);
  });

  group("5. 콘텐츠 보기", () => {
    // 5.1 책 관련 콘텐츠 조회
    executeRequest(
      "책 관련 콘텐츠 조회",
      "GET",
      `${BOOK_API_PATH}/${state.bookId}/contents`
    );

    // 5.2 미디어 리소스 조회
    executeRequest(
      "미디어 리소스 조회",
      "GET",
      `${BOOK_API_PATH}/${state.bookId}/media`
    );

    // 5.3 인용구 AI 요약 조회
    executeRequest(
      "인용구 AI 요약 조회",
      "GET",
      `${QUOTE_API_PATH}/${state.quoteId}/summary`
    );

    // 5.4 짧은 형태의 콘텐츠 조회
    const shortFormResponse = executeRequest(
      "짧은 형태의 콘텐츠 조회",
      "GET",
      `${BOOK_API_PATH}/${state.bookId}/short-form`
    );

    // 콘텐츠가 있는 경우 첫 번째 콘텐츠 ID 사용
    if (
      shortFormResponse &&
      shortFormResponse.data &&
      shortFormResponse.data.contents &&
      shortFormResponse.data.contents.length > 0
    ) {
      state.contentId = shortFormResponse.data.contents[0].id;
      console.log(`콘텐츠 ID 저장: ${state.contentId}`);
    } else {
      state.contentId = 1; // 기본값 설정
    }

    // 5.5 콘텐츠 상호작용 기록
    const interactionData = {
      contentId: state.contentId,
      interactionType: "VIEW",
    };

    executeRequest(
      "콘텐츠 상호작용 기록",
      "POST",
      `${CONTENT_API_PATH}/interaction`,
      interactionData
    );

    sleep(1);
  });

  group("6. 사용자 상호작용", () => {
    // 6.1 콘텐츠 좋아요
    const likeData = {
      contentId: state.contentId,
    };

    executeRequest(
      "콘텐츠 좋아요",
      "POST",
      `${CONTENT_API_PATH}/like`,
      likeData
    );

    // 6.2 콘텐츠 북마크
    const bookmarkData = {
      contentId: state.contentId,
    };

    executeRequest(
      "콘텐츠 북마크",
      "POST",
      `${CONTENT_API_PATH}/bookmark`,
      bookmarkData
    );

    // 6.3 콘텐츠 댓글
    const commentData = {
      contentId: state.contentId,
      text: "정말 좋은 콘텐츠입니다!",
    };

    executeRequest(
      "콘텐츠 댓글",
      "POST",
      `${CONTENT_API_PATH}/comment`,
      commentData
    );

    // 6.4 인용구 저장
    const saveQuoteData = {
      quoteId: state.quoteId,
    };

    executeRequest(
      "인용구 저장",
      "POST",
      `${QUOTE_API_PATH}/save`,
      saveQuoteData
    );

    // 6.5 인용구 좋아요
    const quoteLikeData = {
      quoteId: state.quoteId,
    };

    executeRequest(
      "인용구 좋아요",
      "POST",
      `${QUOTE_API_PATH}/like`,
      quoteLikeData
    );

    sleep(1);
  });

  group("7. 콘텐츠 생성", () => {
    // 7.1 사용자 랭크 확인
    executeRequest("사용자 랭크 확인", "GET", `${RANKING_API_PATH}/user-rank`);

    // 7.2 콘텐츠 생성 가능 횟수 확인
    executeRequest(
      "콘텐츠 생성 가능 횟수 확인",
      "GET",
      `${CONTENT_API_PATH}/creation-limit`
    );

    // 7.3 숏폼 콘텐츠 생성
    const createContentData = {
      bookId: state.bookId,
      title: "책의 핵심 메시지",
      content: "이 책의 핵심 메시지는 꾸준한 노력이 중요하다는 것입니다.",
      emotionType: "HAPPY",
      autoEmotionAnalysis: false,
    };

    const createContentResponse = executeRequest(
      "숏폼 콘텐츠 생성",
      "POST",
      `${CONTENT_API_PATH}/create`,
      createContentData
    );

    // 7.4 콘텐츠 생성 상태 확인
    let contentCreationId = 1; // 기본값

    if (
      createContentResponse &&
      createContentResponse.data &&
      createContentResponse.data.id
    ) {
      contentCreationId = createContentResponse.data.id;
      console.log(`콘텐츠 생성 ID 저장: ${contentCreationId}`);
    }

    executeRequest(
      "콘텐츠 생성 상태 확인",
      "GET",
      `${CONTENT_API_PATH}/status/${contentCreationId}`
    );

    sleep(1);
  });

  group("8. 추천 기능", () => {
    // 8.1 맞춤형 책 추천
    executeRequest(
      "맞춤형 책 추천 조회",
      "GET",
      `${RECOMMENDATION_API_PATH}/books`
    );

    // 8.2 맞춤형 콘텐츠 추천
    executeRequest(
      "맞춤형 콘텐츠 추천 조회",
      "GET",
      `${RECOMMENDATION_API_PATH}/contents`
    );

    // 8.3 유사 사용자 기반 추천
    executeRequest(
      "유사 사용자 기반 추천 조회",
      "GET",
      `${RECOMMENDATION_API_PATH}/similar-users`
    );

    // 8.4 트렌드 콘텐츠
    executeRequest(
      "트렌드 콘텐츠 조회",
      "GET",
      `${RECOMMENDATION_API_PATH}/trending`
    );

    sleep(1);
  });

  group("9. 게이미피케이션 및 랭킹 시스템", () => {
    // 9.1 사용자 랭크 및 점수 확인
    executeRequest("사용자 점수 확인", "GET", `${RANKING_API_PATH}/user-score`);

    // 9.2 획득한 뱃지 목록 확인
    executeRequest(
      "획득한 뱃지 목록 확인",
      "GET",
      `${RANKING_API_PATH}/badges`
    );

    // 9.3 리더보드 확인
    executeRequest("리더보드 확인", "GET", `${RANKING_API_PATH}/leaderboard`);

    sleep(1);
  });

  group("10. 설정 및 관리", () => {
    // 10.1 사용자 프로필 조회
    executeRequest("사용자 프로필 조회", "GET", `${USER_API_PATH}/profile`);

    // 10.2 알림 설정 조회
    executeRequest(
      "알림 설정 조회",
      "GET",
      `${USER_API_PATH}/notification-settings`
    );

    // 10.3 개인정보 설정 조회
    executeRequest(
      "개인정보 설정 조회",
      "GET",
      `${USER_API_PATH}/privacy-settings`
    );

    sleep(1);
  });

  group("11. 전체 사용자 여정 완료 및 정리", () => {
    // 11.1 저장된 콘텐츠 목록 확인
    executeRequest(
      "저장된 콘텐츠 목록 확인",
      "GET",
      `${USER_API_PATH}/saved-contents`
    );

    // 11.2 사용자 활동 내역 확인
    executeRequest(
      "사용자 활동 내역 확인",
      "GET",
      `${USER_API_PATH}/activities`
    );

    // 11.3 로그아웃
    executeRequest("로그아웃", "POST", `${AUTH_API_PATH}/logout`);

    console.log(`\n✅ 전체 사용자 여정 테스트가 완료되었습니다.`);
  });
}

// k6 기본 함수: 테스트 종료 후 정리
export function teardown(data) {
  console.log(`\n🏁 테스트 완료: 모든 API 엔드포인트 테스트 완료`);
}
