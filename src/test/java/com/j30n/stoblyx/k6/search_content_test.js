import { sleep, check, group } from "k6";
import http from "k6/http";
import { SharedArray } from "k6/data";
import { Counter, Rate, Trend } from "k6/metrics";
import {
  randomIntBetween,
  randomItem,
} from "https://jslib.k6.io/k6-utils/1.2.0/index.js";

// 사용자 지표 정의
const searchSuccess = new Rate("search_success_rate");
const bookDetailsSuccess = new Rate("book_details_success_rate");
const contentViewSuccess = new Rate("content_view_success_rate");
const quoteViewSuccess = new Rate("quote_view_success_rate");
const searchLatency = new Trend("search_latency");
const bookLatency = new Trend("book_latency");
const contentLatency = new Trend("content_latency");
const searchErrors = new Counter("search_errors");
const bookErrors = new Counter("book_errors");
const contentErrors = new Counter("content_errors");

// 테스트 환경 설정
const BASE_URL = __ENV.BASE_URL || "http://localhost:8091";

// 사전 정의된 검색어
const SEARCH_KEYWORDS = new SharedArray("search_keywords", function () {
  return [
    "소설",
    "자기계발",
    "역사",
    "철학",
    "과학",
    "예술",
    "건강",
    "경제",
    "심리학",
    "기술",
    "사랑",
    "인생",
    "행복",
    "성공",
    "관계",
    "생산성",
    "영감",
    "창의성",
    "리더십",
    "명상",
  ];
});

// 고정 사용자 계정 (테스트 전에 생성되어 있어야 함)
const TEST_USER = {
  email: "k6_test@example.com",
  password: "Password123!",
};

export const options = {
  stages: [
    { duration: "30s", target: 10 }, // 서서히 사용자 증가
    { duration: "1m", target: 50 }, // 부하 증가
    { duration: "1m", target: 100 }, // 최대 부하
    { duration: "30s", target: 0 }, // 정리
  ],
  thresholds: {
    http_req_duration: ["p(95)<2000"], // 요청 응답 시간 95%가 2초 이내여야 함
    http_req_failed: ["rate<0.05"], // 요청 실패율 5% 미만이어야 함
    search_success_rate: ["rate>0.9"], // 검색 성공률 90% 이상
    book_details_success_rate: ["rate>0.9"], // 도서 상세 조회 성공률 90% 이상
    content_view_success_rate: ["rate>0.9"], // 콘텐츠 조회 성공률 90% 이상
  },
};

// 기본 헤더 설정
const headers = {
  "Content-Type": "application/json",
  Accept: "application/json",
};

// 테스트 초기화 함수 - 로그인하여 인증 토큰 획득
function getAuthToken() {
  const loginPayload = JSON.stringify({
    email: TEST_USER.email,
    password: TEST_USER.password,
  });

  const loginResponse = http.post(`${BASE_URL}/auth/login`, loginPayload, {
    headers,
  });

  if (loginResponse.status === 200) {
    try {
      const body = JSON.parse(loginResponse.body);
      if (body.data && body.data.accessToken) {
        return body.data.accessToken;
      }
    } catch (e) {
      console.error(`로그인 응답 파싱 실패: ${e.message}`);
    }
  }

  console.error(`로그인 실패: ${loginResponse.status} - ${loginResponse.body}`);
  return null;
}

// 메인 테스트 함수
export default function () {
  // 인증 토큰 획득
  const authToken = getAuthToken();
  if (!authToken) {
    console.error("인증 실패로 테스트를 중단합니다.");
    return;
  }

  // 인증 헤더 설정
  const authHeaders = {
    ...headers,
    Authorization: `Bearer ${authToken}`,
  };

  // 무작위 검색어 선택
  const searchKeyword = randomItem(SEARCH_KEYWORDS);

  group("2. 검색 기능", function () {
    // 인기 검색어 조회
    const popularTermsResponse = http.get(
      `${BASE_URL}/search/popular-terms?limit=10`,
      { headers: authHeaders }
    );
    searchLatency.add(popularTermsResponse.timings.duration);

    check(popularTermsResponse, {
      "인기 검색어 조회 성공": (r) => r.status === 200,
      "인기 검색어 데이터 확인": (r) => {
        try {
          const body = JSON.parse(r.body);
          return body.result === "SUCCESS" && body.data;
        } catch (e) {
          return false;
        }
      },
    }) || searchErrors.add(1);

    // 검색 실행
    const searchResponse = http.get(
      `${BASE_URL}/search?keyword=${encodeURIComponent(searchKeyword)}`,
      { headers: authHeaders }
    );
    searchLatency.add(searchResponse.timings.duration);

    const searchCheck = check(searchResponse, {
      "검색 응답 상태 코드 확인": (r) => r.status === 200,
      "검색 결과 데이터 확인": (r) => {
        try {
          const body = JSON.parse(r.body);
          return body.result === "SUCCESS" && body.data;
        } catch (e) {
          return false;
        }
      },
    });

    if (searchCheck) {
      searchSuccess.add(1);
    } else {
      searchSuccess.add(0);
      searchErrors.add(1);
    }

    sleep(randomIntBetween(1, 2));
  });

  group("3. 검색 결과 페이지", function () {
    // 책 목록 조회 (검색 결과 시뮬레이션)
    const booksResponse = http.get(
      `${BASE_URL}/books/search?q=${encodeURIComponent(
        searchKeyword
      )}&page=0&size=10`,
      { headers: authHeaders }
    );
    bookLatency.add(booksResponse.timings.duration);

    check(booksResponse, {
      "책 검색 결과 응답 상태 코드 확인": (r) => r.status === 200,
      "책 검색 결과 데이터 확인": (r) => {
        try {
          const body = JSON.parse(r.body);
          return body.result === "SUCCESS" && body.data;
        } catch (e) {
          return false;
        }
      },
    }) || bookErrors.add(1);

    sleep(randomIntBetween(1, 2));
  });

  // 책 목록 조회 (일반)
  const allBooksResponse = http.get(`${BASE_URL}/books?page=0&size=10`, {
    headers: authHeaders,
  });
  bookLatency.add(allBooksResponse.timings.duration);

  // 첫 번째 책 ID 추출
  let bookId = null;
  try {
    const body = JSON.parse(allBooksResponse.body);
    if (body.data && body.data.content && body.data.content.length > 0) {
      bookId = body.data.content[0].id;
    }
  } catch (e) {
    console.error(`책 목록 데이터 파싱 실패: ${e.message}`);
  }

  if (bookId) {
    group("4. 책 상세 정보 페이지", function () {
      // 책 상세 정보 조회
      const bookDetailResponse = http.get(`${BASE_URL}/books/${bookId}`, {
        headers: authHeaders,
      });
      bookLatency.add(bookDetailResponse.timings.duration);

      const bookDetailCheck = check(bookDetailResponse, {
        "책 상세 정보 응답 상태 코드 확인": (r) => r.status === 200,
        "책 상세 정보 데이터 확인": (r) => {
          try {
            const body = JSON.parse(r.body);
            return body.result === "SUCCESS" && body.data;
          } catch (e) {
            return false;
          }
        },
      });

      if (bookDetailCheck) {
        bookDetailsSuccess.add(1);
      } else {
        bookDetailsSuccess.add(0);
        bookErrors.add(1);
      }

      sleep(randomIntBetween(2, 3));
    });
  }

  group("5. 콘텐츠 보기", function () {
    // 트렌딩 콘텐츠 조회
    const trendingResponse = http.get(
      `${BASE_URL}/contents/trending?page=0&size=10`,
      { headers: authHeaders }
    );
    contentLatency.add(trendingResponse.timings.duration);

    check(trendingResponse, {
      "트렌딩 콘텐츠 응답 상태 코드 확인": (r) => r.status === 200,
      "트렌딩 콘텐츠 데이터 확인": (r) => {
        try {
          const body = JSON.parse(r.body);
          return body.result === "SUCCESS" && body.data;
        } catch (e) {
          return false;
        }
      },
    }) || contentErrors.add(1);

    // 콘텐츠 ID 추출
    let contentId = null;
    try {
      const body = JSON.parse(trendingResponse.body);
      if (body.data && body.data.content && body.data.content.length > 0) {
        contentId = body.data.content[0].id;
      }
    } catch (e) {
      console.error(`콘텐츠 데이터 파싱 실패: ${e.message}`);
    }

    if (contentId) {
      // 콘텐츠 상세 조회
      const contentDetailResponse = http.get(
        `${BASE_URL}/contents/${contentId}`,
        { headers: authHeaders }
      );
      contentLatency.add(contentDetailResponse.timings.duration);

      const contentDetailCheck = check(contentDetailResponse, {
        "콘텐츠 상세 응답 상태 코드 확인": (r) => r.status === 200,
        "콘텐츠 상세 데이터 확인": (r) => {
          try {
            const body = JSON.parse(r.body);
            return body.result === "SUCCESS" && body.data;
          } catch (e) {
            return false;
          }
        },
      });

      if (contentDetailCheck) {
        contentViewSuccess.add(1);
      } else {
        contentViewSuccess.add(0);
        contentErrors.add(1);
      }

      // 인용구 조회
      const quotesResponse = http.get(
        `${BASE_URL}/quotes/content/${contentId}?page=0&size=5`,
        { headers: authHeaders }
      );
      contentLatency.add(quotesResponse.timings.duration);

      const quoteCheck = check(quotesResponse, {
        "인용구 응답 상태 코드 확인": (r) => r.status === 200,
        "인용구 데이터 확인": (r) => {
          try {
            const body = JSON.parse(r.body);
            return body.result === "SUCCESS";
          } catch (e) {
            return false;
          }
        },
      });

      if (quoteCheck) {
        quoteViewSuccess.add(1);
      } else {
        quoteViewSuccess.add(0);
        contentErrors.add(1);
      }

      // 콘텐츠 상호작용 기록
      const interactionPayload = JSON.stringify({
        contentId: contentId,
        interactionType: "VIEW",
        duration: randomIntBetween(30, 300),
      });

      http.post(`${BASE_URL}/contents/interaction`, interactionPayload, {
        headers: authHeaders,
      });
    }

    sleep(randomIntBetween(2, 4));
  });

  // 추천 콘텐츠 조회
  const recommendedResponse = http.get(
    `${BASE_URL}/contents/recommended?page=0&size=10`,
    { headers: authHeaders }
  );
  contentLatency.add(recommendedResponse.timings.duration);

  check(recommendedResponse, {
    "추천 콘텐츠 응답 상태 코드 확인": (r) => r.status === 200,
  }) || contentErrors.add(1);

  // 북마크된 콘텐츠 조회 (선택적)
  if (bookId) {
    const bookContentResponse = http.get(
      `${BASE_URL}/contents/books/${bookId}?page=0&size=10`,
      { headers: authHeaders }
    );
    contentLatency.add(bookContentResponse.timings.duration);

    check(bookContentResponse, {
      "도서 관련 콘텐츠 응답 상태 코드 확인": (r) => r.status === 200,
    }) || contentErrors.add(1);
  }
}

// 요약 보고서 핸들러
export function handleSummary(data) {
  return {
    "search_content_summary.json": JSON.stringify(data),
    stdout: `
===== 검색 및 콘텐츠 조회 성능 요약 =====
총 가상 사용자: ${data.metrics.vus.max}
테스트 지속 시간: ${data.state.testRunDurationMs / 1000}초

요청 메트릭:
- 총 요청 수: ${data.metrics.http_reqs.count}
- 평균 응답 시간: ${data.metrics.http_req_duration.avg.toFixed(2)}ms
- 95% 응답 시간: ${data.metrics.http_req_duration.p(95).toFixed(2)}ms
- 요청 실패율: ${(data.metrics.http_req_failed.rate * 100).toFixed(2)}%

검색 및 콘텐츠 흐름 성공률:
- 검색 성공률: ${(data.metrics.search_success_rate
      ? data.metrics.search_success_rate.rate * 100
      : 0
    ).toFixed(2)}%
- 도서 상세 조회 성공률: ${(data.metrics.book_details_success_rate
      ? data.metrics.book_details_success_rate.rate * 100
      : 0
    ).toFixed(2)}%
- 콘텐츠 조회 성공률: ${(data.metrics.content_view_success_rate
      ? data.metrics.content_view_success_rate.rate * 100
      : 0
    ).toFixed(2)}%
- 인용구 조회 성공률: ${(data.metrics.quote_view_success_rate
      ? data.metrics.quote_view_success_rate.rate * 100
      : 0
    ).toFixed(2)}%

오류 수:
- 검색 오류: ${
      data.metrics.search_errors ? data.metrics.search_errors.count : 0
    }
- 도서 오류: ${data.metrics.book_errors ? data.metrics.book_errors.count : 0}
- 콘텐츠 오류: ${
      data.metrics.content_errors ? data.metrics.content_errors.count : 0
    }

응답 시간(ms):
- 검색: 평균 ${(data.metrics.search_latency
      ? data.metrics.search_latency.avg
      : 0
    ).toFixed(2)}, 최대 ${(data.metrics.search_latency
      ? data.metrics.search_latency.max
      : 0
    ).toFixed(2)}
- 도서: 평균 ${(data.metrics.book_latency
      ? data.metrics.book_latency.avg
      : 0
    ).toFixed(2)}, 최대 ${(data.metrics.book_latency
      ? data.metrics.book_latency.max
      : 0
    ).toFixed(2)}
- 콘텐츠: 평균 ${(data.metrics.content_latency
      ? data.metrics.content_latency.avg
      : 0
    ).toFixed(2)}, 최대 ${(data.metrics.content_latency
      ? data.metrics.content_latency.max
      : 0
    ).toFixed(2)}
`,
  };
}
