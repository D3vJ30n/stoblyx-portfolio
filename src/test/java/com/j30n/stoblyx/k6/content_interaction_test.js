import { sleep, check, group } from "k6";
import http from "k6/http";
import { SharedArray } from "k6/data";
import { Counter, Rate, Trend } from "k6/metrics";
import {
  randomIntBetween,
  randomItem,
} from "https://jslib.k6.io/k6-utils/1.2.0/index.js";

// 사용자 지표 정의
const createContentSuccess = new Rate("create_content_success_rate");
const likeSuccess = new Rate("like_success_rate");
const bookmarkSuccess = new Rate("bookmark_success_rate");
const commentSuccess = new Rate("comment_success_rate");
const contentInteractionLatency = new Trend("content_interaction_latency");
const contentCreationLatency = new Trend("content_creation_latency");
const contentErrors = new Counter("content_errors");
const interactionErrors = new Counter("interaction_errors");

// 테스트 환경 설정
const BASE_URL = __ENV.BASE_URL || "http://localhost:8091";

// 고정 사용자 계정 (테스트 전에 생성되어 있어야 함)
const TEST_USERS = new SharedArray("test_users", function () {
  return [
    { email: "k6_test1@example.com", password: "Password123!" },
    { email: "k6_test2@example.com", password: "Password123!" },
    { email: "k6_test3@example.com", password: "Password123!" },
    { email: "k6_test4@example.com", password: "Password123!" },
    { email: "k6_test5@example.com", password: "Password123!" },
  ];
});

// BGM 타입
const BGM_TYPES = ["calm", "happy", "neutral", "sad"];

// 댓글 템플릿
const COMMENT_TEMPLATES = [
  "정말 좋은 콘텐츠네요!",
  "이 책은 제 인생을 바꿨어요.",
  "이 문구가 정말 마음에 와닿네요.",
  "너무 감동적입니다.",
  "공감되는 내용이에요.",
  "이런 콘텐츠 더 많이 보고 싶어요.",
  "자주 찾아오겠습니다.",
  "좋은 정보 감사합니다.",
  "이 내용을 친구들과 공유하고 싶어요.",
  "너무 유익한 내용이에요.",
];

export const options = {
  stages: [
    { duration: "30s", target: 10 }, // 서서히 사용자 증가
    { duration: "1m", target: 30 }, // 중간 부하
    { duration: "30s", target: 50 }, // 최대 부하
    { duration: "30s", target: 0 }, // 정리
  ],
  thresholds: {
    http_req_duration: ["p(95)<3000"], // 요청 응답 시간 95%가 3초 이내여야 함 (콘텐츠 생성은 시간이 더 소요될 수 있음)
    http_req_failed: ["rate<0.05"], // 요청 실패율 5% 미만이어야 함
    create_content_success_rate: ["rate>0.85"], // 콘텐츠 생성 성공률 85% 이상 (비동기 처리로 약간 낮게 설정)
    like_success_rate: ["rate>0.9"], // 좋아요 성공률 90% 이상
    bookmark_success_rate: ["rate>0.9"], // 북마크 성공률 90% 이상
    comment_success_rate: ["rate>0.9"], // 댓글 성공률 90% 이상
  },
};

// 기본 헤더 설정
const headers = {
  "Content-Type": "application/json",
  Accept: "application/json",
};

// 테스트 초기화 함수 - 로그인하여 인증 토큰 획득
function getAuthToken(user) {
  const loginPayload = JSON.stringify({
    email: user.email,
    password: user.password,
  });

  const loginResponse = http.post(`${BASE_URL}/auth/login`, loginPayload, {
    headers,
  });

  if (loginResponse.status === 200) {
    try {
      const body = JSON.parse(loginResponse.body);
      if (body.data && body.data.accessToken) {
        return {
          accessToken: body.data.accessToken,
          userId: body.data.userId,
        };
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
  // 무작위 사용자 선택
  const user = randomItem(TEST_USERS);

  // 인증 토큰 획득
  const authData = getAuthToken(user);
  if (!authData) {
    console.error("인증 실패로 테스트를 중단합니다.");
    return;
  }

  // 인증 헤더 설정
  const authHeaders = {
    ...headers,
    Authorization: `Bearer ${authData.accessToken}`,
  };

  let contentId = null;
  let bookId = null;
  let quoteId = null;

  // 책 목록 조회하여 책 ID 가져오기
  const booksResponse = http.get(`${BASE_URL}/books?page=0&size=10`, {
    headers: authHeaders,
  });
  try {
    const body = JSON.parse(booksResponse.body);
    if (body.data && body.data.content && body.data.content.length > 0) {
      bookId = body.data.content[0].id;
    }
  } catch (e) {
    console.error(`책 목록 데이터 파싱 실패: ${e.message}`);
  }

  // 인용구 목록 조회하여 인용구 ID 가져오기
  if (bookId) {
    const quotesResponse = http.get(
      `${BASE_URL}/quotes/book/${bookId}?page=0&size=10`,
      { headers: authHeaders }
    );
    try {
      const body = JSON.parse(quotesResponse.body);
      if (body.data && body.data.content && body.data.content.length > 0) {
        quoteId = body.data.content[0].id;
      }
    } catch (e) {
      console.error(`인용구 목록 데이터 파싱 실패: ${e.message}`);
    }
  }

  group("7. 콘텐츠 생성", function () {
    // 콘텐츠 생성 제한 조회
    const limitResponse = http.get(`${BASE_URL}/contents/creation-limit`, {
      headers: authHeaders,
    });

    check(limitResponse, {
      "콘텐츠 생성 제한 조회 성공": (r) => r.status === 200,
    }) || contentErrors.add(1);

    // 콘텐츠 생성 요청 (책 기반)
    if (bookId) {
      const bgmType = randomItem(BGM_TYPES);

      const createPayload = JSON.stringify({
        bookId: bookId,
        contentType: "SHORT_FORM",
        bgmType: bgmType,
        useAutoEmotion: randomIntBetween(0, 1) === 1,
      });

      const startTime = new Date();
      const createResponse = http.post(
        `${BASE_URL}/contents/create`,
        createPayload,
        { headers: authHeaders }
      );
      const endTime = new Date();

      contentCreationLatency.add(endTime - startTime);

      const createContentCheck = check(createResponse, {
        "콘텐츠 생성 요청 성공": (r) => r.status === 200 || r.status === 202,
        "콘텐츠 생성 응답 데이터 확인": (r) => {
          try {
            const body = JSON.parse(r.body);
            return body.result === "SUCCESS" && body.data;
          } catch (e) {
            return false;
          }
        },
      });

      if (createContentCheck) {
        createContentSuccess.add(1);
        try {
          const body = JSON.parse(createResponse.body);
          if (body.data && body.data.id) {
            // 콘텐츠 생성 작업 ID
            const contentJobId = body.data.id;

            // 콘텐츠 생성 상태 조회 (선택적)
            sleep(2); // 작업이 시작될 시간 기다림

            const statusResponse = http.get(
              `${BASE_URL}/contents/status/${contentJobId}`,
              { headers: authHeaders }
            );

            check(statusResponse, {
              "콘텐츠 생성 상태 조회 성공": (r) => r.status === 200,
            }) || contentErrors.add(1);
          }
        } catch (e) {
          console.error(`콘텐츠 생성 응답 파싱 실패: ${e.message}`);
        }
      } else {
        createContentSuccess.add(0);
        contentErrors.add(1);
      }
    }

    // 인용구 기반 콘텐츠 생성 (선택적)
    if (quoteId) {
      const quoteContentResponse = http.post(
        `${BASE_URL}/contents/quotes/${quoteId}`,
        null,
        { headers: authHeaders }
      );

      check(quoteContentResponse, {
        "인용구 기반 콘텐츠 생성 성공": (r) =>
          r.status === 200 || r.status === 202,
      }) || contentErrors.add(1);

      contentCreationLatency.add(quoteContentResponse.timings.duration);
    }

    sleep(randomIntBetween(2, 4));
  });

  // 트렌딩 콘텐츠 조회
  const trendingResponse = http.get(
    `${BASE_URL}/contents/trending?page=0&size=10`,
    { headers: authHeaders }
  );

  try {
    const body = JSON.parse(trendingResponse.body);
    if (body.data && body.data.content && body.data.content.length > 0) {
      contentId = body.data.content[0].id;
    }
  } catch (e) {
    console.error(`트렌딩 콘텐츠 데이터 파싱 실패: ${e.message}`);
  }

  group("6. 사용자 상호작용", function () {
    if (contentId) {
      // 콘텐츠 상세 조회
      const contentDetailResponse = http.get(
        `${BASE_URL}/contents/${contentId}`,
        { headers: authHeaders }
      );

      check(contentDetailResponse, {
        "콘텐츠 상세 조회 성공": (r) => r.status === 200,
      }) || contentErrors.add(1);

      // 콘텐츠 좋아요
      const likeResponse = http.post(
        `${BASE_URL}/contents/${contentId}/like`,
        null,
        { headers: authHeaders }
      );

      contentInteractionLatency.add(likeResponse.timings.duration);

      const likeCheck = check(likeResponse, {
        "콘텐츠 좋아요 성공": (r) => r.status === 200,
        "콘텐츠 좋아요 응답 데이터 확인": (r) => {
          try {
            const body = JSON.parse(r.body);
            return body.result === "SUCCESS";
          } catch (e) {
            return false;
          }
        },
      });

      if (likeCheck) {
        likeSuccess.add(1);
      } else {
        likeSuccess.add(0);
        interactionErrors.add(1);
      }

      // 콘텐츠 북마크
      const bookmarkResponse = http.post(
        `${BASE_URL}/contents/${contentId}/bookmark`,
        null,
        { headers: authHeaders }
      );

      contentInteractionLatency.add(bookmarkResponse.timings.duration);

      const bookmarkCheck = check(bookmarkResponse, {
        "콘텐츠 북마크 성공": (r) => r.status === 200,
        "콘텐츠 북마크 응답 데이터 확인": (r) => {
          try {
            const body = JSON.parse(r.body);
            return body.result === "SUCCESS";
          } catch (e) {
            return false;
          }
        },
      });

      if (bookmarkCheck) {
        bookmarkSuccess.add(1);
      } else {
        bookmarkSuccess.add(0);
        interactionErrors.add(1);
      }

      // 북마크 상태 조회
      const bookmarkStatusResponse = http.get(
        `${BASE_URL}/contents/${contentId}/bookmark/status`,
        { headers: authHeaders }
      );

      check(bookmarkStatusResponse, {
        "북마크 상태 조회 성공": (r) => r.status === 200,
      }) || interactionErrors.add(1);

      // 콘텐츠 댓글 작성
      const commentText = randomItem(COMMENT_TEMPLATES);
      const commentPayload = JSON.stringify({
        contentId: contentId,
        comment: commentText,
      });

      const commentResponse = http.post(
        `${BASE_URL}/comments`,
        commentPayload,
        { headers: authHeaders }
      );

      contentInteractionLatency.add(commentResponse.timings.duration);

      const commentCheck = check(commentResponse, {
        "댓글 작성 성공": (r) => r.status === 200 || r.status === 201,
        "댓글 작성 응답 데이터 확인": (r) => {
          try {
            const body = JSON.parse(r.body);
            return body.result === "SUCCESS";
          } catch (e) {
            return false;
          }
        },
      });

      if (commentCheck) {
        commentSuccess.add(1);

        // 댓글 ID 추출
        let commentId = null;
        try {
          const body = JSON.parse(commentResponse.body);
          if (body.data && body.data.id) {
            commentId = body.data.id;
          }
        } catch (e) {
          console.error(`댓글 응답 파싱 실패: ${e.message}`);
        }

        // 댓글 목록 조회
        if (commentId) {
          const commentsResponse = http.get(
            `${BASE_URL}/comments/content/${contentId}?page=0&size=10`,
            { headers: authHeaders }
          );

          check(commentsResponse, {
            "댓글 목록 조회 성공": (r) => r.status === 200,
          }) || interactionErrors.add(1);
        }
      } else {
        commentSuccess.add(0);
        interactionErrors.add(1);
      }

      // 콘텐츠 상호작용 기록
      const interactionPayload = JSON.stringify({
        contentId: contentId,
        interactionType: "VIEW",
        duration: randomIntBetween(30, 300),
      });

      const interactionResponse = http.post(
        `${BASE_URL}/contents/interaction`,
        interactionPayload,
        { headers: authHeaders }
      );

      contentInteractionLatency.add(interactionResponse.timings.duration);

      check(interactionResponse, {
        "콘텐츠 상호작용 기록 성공": (r) => r.status === 200,
      }) || interactionErrors.add(1);
    } else {
      console.error("콘텐츠 ID를 찾을 수 없어 상호작용 테스트를 건너뜁니다.");
    }

    sleep(randomIntBetween(1, 3));
  });

  group("9. 게이미피케이션 및 랭킹 시스템", function () {
    // 사용자 랭킹 조회
    const rankingResponse = http.get(
      `${BASE_URL}/ranking/users?page=0&size=10`,
      { headers: authHeaders }
    );

    check(rankingResponse, {
      "랭킹 조회 성공": (r) => r.status === 200,
    }) || interactionErrors.add(1);

    // 사용자 점수 조회
    const userScoreResponse = http.get(
      `${BASE_URL}/ranking/user/${authData.userId}/score`,
      { headers: authHeaders }
    );

    check(userScoreResponse, {
      "사용자 점수 조회 성공": (r) => r.status === 200,
    }) || interactionErrors.add(1);

    sleep(randomIntBetween(1, 2));
  });
}

// 요약 보고서 핸들러
export function handleSummary(data) {
  return {
    "content_interaction_summary.json": JSON.stringify(data),
    stdout: `
===== 콘텐츠 생성 및 상호작용 성능 요약 =====
총 가상 사용자: ${data.metrics.vus.max}
테스트 지속 시간: ${data.state.testRunDurationMs / 1000}초

요청 메트릭:
- 총 요청 수: ${data.metrics.http_reqs.count}
- 평균 응답 시간: ${data.metrics.http_req_duration.avg.toFixed(2)}ms
- 95% 응답 시간: ${data.metrics.http_req_duration.p(95).toFixed(2)}ms
- 요청 실패율: ${(data.metrics.http_req_failed.rate * 100).toFixed(2)}%

콘텐츠 생성 및 상호작용 성공률:
- 콘텐츠 생성 성공률: ${(data.metrics.create_content_success_rate
      ? data.metrics.create_content_success_rate.rate * 100
      : 0
    ).toFixed(2)}%
- 좋아요 성공률: ${(data.metrics.like_success_rate
      ? data.metrics.like_success_rate.rate * 100
      : 0
    ).toFixed(2)}%
- 북마크 성공률: ${(data.metrics.bookmark_success_rate
      ? data.metrics.bookmark_success_rate.rate * 100
      : 0
    ).toFixed(2)}%
- 댓글 성공률: ${(data.metrics.comment_success_rate
      ? data.metrics.comment_success_rate.rate * 100
      : 0
    ).toFixed(2)}%

오류 수:
- 콘텐츠 오류: ${
      data.metrics.content_errors ? data.metrics.content_errors.count : 0
    }
- 상호작용 오류: ${
      data.metrics.interaction_errors
        ? data.metrics.interaction_errors.count
        : 0
    }

응답 시간(ms):
- 콘텐츠 생성: 평균 ${(data.metrics.content_creation_latency
      ? data.metrics.content_creation_latency.avg
      : 0
    ).toFixed(2)}, 최대 ${(data.metrics.content_creation_latency
      ? data.metrics.content_creation_latency.max
      : 0
    ).toFixed(2)}
- 콘텐츠 상호작용: 평균 ${(data.metrics.content_interaction_latency
      ? data.metrics.content_interaction_latency.avg
      : 0
    ).toFixed(2)}, 최대 ${(data.metrics.content_interaction_latency
      ? data.metrics.content_interaction_latency.max
      : 0
    ).toFixed(2)}
`,
  };
}
