import { sleep, check, group } from "k6";
import http from "k6/http";
import { Counter, Rate, Trend } from "k6/metrics";
import { randomString } from "https://jslib.k6.io/k6-utils/1.2.0/index.js";

// 사용자 지표 정의
const signupSuccess = new Rate("signup_success_rate");
const loginSuccess = new Rate("login_success_rate");
const refreshSuccess = new Rate("refresh_success_rate");
const logoutSuccess = new Rate("logout_success_rate");
const authLatency = new Trend("auth_latency");
const authErrors = new Counter("auth_errors");

// 테스트 환경 설정
const BASE_URL = __ENV.BASE_URL || "http://localhost:8091";

export const options = {
  stages: [
    { duration: "30s", target: 20 }, // 서서히 사용자 증가
    { duration: "1m", target: 50 }, // 부하 증가
    { duration: "30s", target: 100 }, // 최대 부하
    { duration: "30s", target: 0 }, // 정리
  ],
  thresholds: {
    http_req_duration: ["p(95)<1500"], // 요청 응답 시간 95%가 1.5초 이내여야 함
    http_req_failed: ["rate<0.05"], // 요청 실패율 5% 미만이어야 함
    signup_success_rate: ["rate>0.9"], // 회원가입 성공률 90% 이상
    login_success_rate: ["rate>0.9"], // 로그인 성공률 90% 이상
    refresh_success_rate: ["rate>0.9"], // 토큰 갱신 성공률 90% 이상
    logout_success_rate: ["rate>0.9"], // 로그아웃 성공률 90% 이상
  },
};

// 기본 헤더 설정
const headers = {
  "Content-Type": "application/json",
  Accept: "application/json",
};

export default function () {
  // 고유 식별자 생성 (타임스탬프와 랜덤 문자열 조합)
  const uniqueId = `${Date.now()}_${randomString(5)}`;
  const email = `test_${uniqueId}@example.com`;
  const password = "Password123!";
  const nickname = `테스트유저_${uniqueId}`;

  let accessToken = null;
  let refreshToken = null;

  group("회원가입 및 로그인 테스트", function () {
    // 1. 회원가입 요청
    const signupPayload = JSON.stringify({
      email: email,
      password: password,
      nickname: nickname,
      termsAgreed: true,
    });

    const signupResponse = http.post(`${BASE_URL}/auth/signup`, signupPayload, {
      headers,
    });
    authLatency.add(signupResponse.timings.duration);

    check(signupResponse, {
      "회원가입 응답 상태 코드 확인": (r) =>
        r.status === 200 || r.status === 201,
      "회원가입 응답 데이터 확인": (r) => {
        try {
          const body = JSON.parse(r.body);
          return body.result === "SUCCESS";
        } catch (e) {
          return false;
        }
      },
    })
      ? signupSuccess.add(1)
      : signupSuccess.add(0);

    if (signupResponse.status !== 200 && signupResponse.status !== 201) {
      console.error(
        `회원가입 실패: ${signupResponse.status} - ${signupResponse.body}`
      );
      authErrors.add(1);
      return;
    }

    sleep(1);

    // 2. 로그인 요청
    const loginPayload = JSON.stringify({
      email: email,
      password: password,
    });

    const loginResponse = http.post(`${BASE_URL}/auth/login`, loginPayload, {
      headers,
    });
    authLatency.add(loginResponse.timings.duration);

    const loginSuccess = check(loginResponse, {
      "로그인 응답 상태 코드 확인": (r) => r.status === 200,
      "로그인 응답 데이터 확인": (r) => {
        try {
          const body = JSON.parse(r.body);
          return (
            body.result === "SUCCESS" && body.data && body.data.accessToken
          );
        } catch (e) {
          return false;
        }
      },
    });

    if (loginSuccess) {
      try {
        const body = JSON.parse(loginResponse.body);
        accessToken = body.data.accessToken;
        refreshToken = body.data.refreshToken;
        loginSuccess.add(1);
      } catch (e) {
        console.error(`로그인 응답 파싱 실패: ${e.message}`);
        loginSuccess.add(0);
        authErrors.add(1);
        return;
      }
    } else {
      console.error(
        `로그인 실패: ${loginResponse.status} - ${loginResponse.body}`
      );
      loginSuccess.add(0);
      authErrors.add(1);
      return;
    }

    sleep(1);

    // 3. 토큰 갱신 테스트
    if (refreshToken) {
      const refreshHeaders = {
        ...headers,
        Authorization: `Bearer ${refreshToken}`,
      };

      const refreshResponse = http.post(`${BASE_URL}/auth/refresh`, null, {
        headers: refreshHeaders,
      });
      authLatency.add(refreshResponse.timings.duration);

      check(refreshResponse, {
        "토큰 갱신 응답 상태 코드 확인": (r) => r.status === 200,
        "토큰 갱신 응답 데이터 확인": (r) => {
          try {
            const body = JSON.parse(r.body);
            return (
              body.result === "SUCCESS" && body.data && body.data.accessToken
            );
          } catch (e) {
            return false;
          }
        },
      })
        ? refreshSuccess.add(1)
        : refreshSuccess.add(0);

      if (refreshResponse.status === 200) {
        try {
          const body = JSON.parse(refreshResponse.body);
          accessToken = body.data.accessToken; // 갱신된 액세스 토큰으로 업데이트
        } catch (e) {
          console.error(`토큰 갱신 응답 파싱 실패: ${e.message}`);
        }
      } else {
        console.error(
          `토큰 갱신 실패: ${refreshResponse.status} - ${refreshResponse.body}`
        );
        authErrors.add(1);
      }

      sleep(1);
    }

    // 4. 로그아웃 테스트
    if (accessToken) {
      const logoutHeaders = {
        ...headers,
        Authorization: `Bearer ${accessToken}`,
      };

      const logoutResponse = http.post(`${BASE_URL}/auth/logout`, null, {
        headers: logoutHeaders,
      });
      authLatency.add(logoutResponse.timings.duration);

      check(logoutResponse, {
        "로그아웃 응답 상태 코드 확인": (r) => r.status === 200,
        "로그아웃 응답 데이터 확인": (r) => {
          try {
            const body = JSON.parse(r.body);
            return body.result === "SUCCESS";
          } catch (e) {
            return false;
          }
        },
      })
        ? logoutSuccess.add(1)
        : logoutSuccess.add(0);

      if (logoutResponse.status !== 200) {
        console.error(
          `로그아웃 실패: ${logoutResponse.status} - ${logoutResponse.body}`
        );
        authErrors.add(1);
      }
    }
  });
}

// 요약 보고서 핸들러
export function handleSummary(data) {
  return {
    "auth_summary.json": JSON.stringify(data),
    stdout: `
===== 인증 흐름 성능 요약 =====
총 가상 사용자: ${data.metrics.vus.max}
테스트 지속 시간: ${data.state.testRunDurationMs / 1000}초

인증 요청 메트릭:
- 총 요청 수: ${data.metrics.http_reqs.count}
- 평균 응답 시간: ${data.metrics.http_req_duration.avg.toFixed(2)}ms
- 95% 응답 시간: ${data.metrics.http_req_duration.p(95).toFixed(2)}ms
- 요청 실패율: ${(data.metrics.http_req_failed.rate * 100).toFixed(2)}%

인증 흐름 성공률:
- 회원가입 성공률: ${(data.metrics.signup_success_rate
      ? data.metrics.signup_success_rate.rate * 100
      : 0
    ).toFixed(2)}%
- 로그인 성공률: ${(data.metrics.login_success_rate
      ? data.metrics.login_success_rate.rate * 100
      : 0
    ).toFixed(2)}%
- 토큰 갱신 성공률: ${(data.metrics.refresh_success_rate
      ? data.metrics.refresh_success_rate.rate * 100
      : 0
    ).toFixed(2)}%
- 로그아웃 성공률: ${(data.metrics.logout_success_rate
      ? data.metrics.logout_success_rate.rate * 100
      : 0
    ).toFixed(2)}%
- 인증 오류 수: ${data.metrics.auth_errors ? data.metrics.auth_errors.count : 0}
`,
  };
}
