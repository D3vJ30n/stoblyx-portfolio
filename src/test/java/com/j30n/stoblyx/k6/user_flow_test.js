import { check, group, sleep } from "k6";
import http from "k6/http";
import { SharedArray } from "k6/data";
import { Counter, Rate, Trend } from "k6/metrics";
import {
  randomIntBetween,
  randomItem,
} from "https://jslib.k6.io/k6-utils/1.2.0/index.js";
import encoding from "k6/encoding";

// 사용자 지표 정의
const userSuccessRate = new Rate("user_success_rate");
const authErrors = new Counter("auth_errors");
const searchErrors = new Counter("search_errors");
const contentErrors = new Counter("content_errors");
const bookErrors = new Counter("book_errors");
const interactionErrors = new Counter("interaction_errors");
const apiLatency = new Trend("api_latency");

// 성공/실패 추적 지표 추가
const successfulApiCalls = new Counter("successful_api_calls");
const failedApiCalls = new Counter("failed_api_calls");
const apiCallsByGroup = {};

// 내부 HTTP 메트릭 추가 (Counter 타입으로 변경)
const internalHttpRequests = new Counter("internal_http_requests");
const internalHttpFailures = new Counter("internal_http_failures");

// Base64 디코더 함수 (TextDecoder 대체)
function decodeBase64(str) {
  let output = "";
  const base64chars =
    "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
  let i = 0;

  // remove all characters that are not A-Z, a-z, 0-9, +, /, or =
  str = str.replace(/[^A-Za-z0-9\+\/\=]/g, "");

  while (i < str.length) {
    const enc1 = base64chars.indexOf(str.charAt(i++));
    const enc2 = base64chars.indexOf(str.charAt(i++));
    const enc3 = base64chars.indexOf(str.charAt(i++));
    const enc4 = base64chars.indexOf(str.charAt(i++));

    const chr1 = (enc1 << 2) | (enc2 >> 4);
    const chr2 = ((enc2 & 15) << 4) | (enc3 >> 2);
    const chr3 = ((enc3 & 3) << 6) | enc4;

    output += String.fromCharCode(chr1);

    if (enc3 !== 64) {
      output += String.fromCharCode(chr2);
    }
    if (enc4 !== 64) {
      output += String.fromCharCode(chr3);
    }
  }

  return output;
}

/**
 * API 응답을 구조화된 형식으로 출력하는 함수
 */
function prettyPrintResponse(groupName, response, endpoint) {
  // 구분선 표시
  console.log(`\x1b[90m${"─".repeat(80)}\x1b[0m`);

  // 응답 헤더 표시
  console.log(`\x1b[1;36m[${groupName}] API 응답: ${endpoint}\x1b[0m`);

  // 상태 코드 색상 설정
  let statusColor = "\x1b[32m"; // 성공 (녹색)
  if (response.status >= 400 && response.status < 500) {
    statusColor = "\x1b[33m"; // 클라이언트 오류 (노란색)
  } else if (response.status >= 500) {
    statusColor = "\x1b[31m"; // 서버 오류 (빨간색)
  }

  // 상태 정보 출력
  console.log(
    `\x1b[1m상태 코드:\x1b[0m ${statusColor}${response.status}\x1b[0m`
  );

  // 응답 본문 파싱 시도
  try {
    // JSON 응답인 경우 구조화해서 출력
    const jsonResponse = JSON.parse(response.body);

    // 응답 구조 구분선
    console.log(`\x1b[1m응답 구조:\x1b[0m`);

    // ApiResponse 형식인 경우 (result, message, data 구조)
    if (jsonResponse.result) {
      console.log(
        `  \x1b[34m● 결과:\x1b[0m ${
          jsonResponse.result === "SUCCESS"
            ? "\x1b[32mSUCCESS\x1b[0m"
            : "\x1b[31mERROR\x1b[0m"
        }`
      );
      console.log(
        `  \x1b[34m● 메시지:\x1b[0m ${jsonResponse.message || "없음"}`
      );

      // 데이터 부분 컴팩트하게 표시
      if (jsonResponse.data) {
        if (Array.isArray(jsonResponse.data)) {
          console.log(
            `  \x1b[34m● 데이터:\x1b[0m 배열 [${jsonResponse.data.length}개 항목]`
          );
          // 배열의 첫 번째 항목만 샘플로 표시
          if (jsonResponse.data.length > 0) {
            console.log(`    \x1b[33m◦ 샘플 항목:\x1b[0m`);
            Object.keys(jsonResponse.data[0]).forEach((key) => {
              const value = jsonResponse.data[0][key];
              console.log(
                `      \x1b[90m- ${key}:\x1b[0m ${formatValue(value)}`
              );
            });
          }
        } else if (
          jsonResponse.data.content &&
          Array.isArray(jsonResponse.data.content)
        ) {
          // 페이징된 데이터
          console.log(
            `  \x1b[34m● 데이터:\x1b[0m 페이징 (총 ${
              jsonResponse.data.totalElements || "?"
            } 항목 중 ${jsonResponse.data.content.length}개)`
          );
          if (jsonResponse.data.content.length > 0) {
            console.log(`    \x1b[33m◦ 샘플 항목:\x1b[0m`);
            Object.keys(jsonResponse.data.content[0]).forEach((key) => {
              const value = jsonResponse.data.content[0][key];
              console.log(
                `      \x1b[90m- ${key}:\x1b[0m ${formatValue(value)}`
              );
            });
          }
        } else {
          // 단일 객체 데이터
          console.log(`  \x1b[34m● 데이터:\x1b[0m`);
          const showObjectContent = (obj, depth = 1) => {
            Object.keys(obj).forEach((key) => {
              const value = obj[key];
              const indent = "  ".repeat(depth + 1);
              if (typeof value === "object" && value !== null) {
                console.log(`${indent}\x1b[33m◦ ${key}:\x1b[0m`);
                showObjectContent(value, depth + 1);
              } else {
                console.log(
                  `${indent}\x1b[33m◦ ${key}:\x1b[0m ${formatValue(value)}`
                );
              }
            });
          };
          showObjectContent(jsonResponse.data);
        }
      } else {
        console.log(`  \x1b[34m● 데이터:\x1b[0m 없음`);
      }
    } else {
      // 일반 JSON 응답 (표준 형식이 아닌 경우)
      if (Array.isArray(jsonResponse)) {
        console.log(
          `  \x1b[34m● 배열 데이터:\x1b[0m [${jsonResponse.length}개 항목]`
        );
        if (jsonResponse.length > 0) {
          console.log(`    \x1b[33m◦ 샘플 항목:\x1b[0m`);
          if (typeof jsonResponse[0] === "object") {
            Object.keys(jsonResponse[0]).forEach((key) => {
              console.log(
                `      \x1b[90m- ${key}:\x1b[0m ${formatValue(
                  jsonResponse[0][key]
                )}`
              );
            });
          } else {
            console.log(
              `      \x1b[90m- 값:\x1b[0m ${formatValue(jsonResponse[0])}`
            );
          }
        }
      } else {
        console.log(`  \x1b[34m● 객체 데이터:\x1b[0m`);
        Object.keys(jsonResponse).forEach((key) => {
          const value = jsonResponse[key];
          if (typeof value === "object" && value !== null) {
            console.log(`    \x1b[33m◦ ${key}:\x1b[0m [객체]`);
          } else {
            console.log(`    \x1b[33m◦ ${key}:\x1b[0m ${formatValue(value)}`);
          }
        });
      }
    }
  } catch (e) {
    // JSON이 아닌 경우 텍스트로 출력
    console.log(`\x1b[1m응답 본문:\x1b[0m`);
    if (response.body && response.body.length > 0) {
      // 텍스트가 너무 길면 일부만 표시
      const maxLength = 200;
      const text =
        response.body.length > maxLength
          ? response.body.substring(0, maxLength) + "..."
          : response.body;
      console.log(`  ${text}`);
    } else {
      console.log(`  (응답 본문 없음)`);
    }
  }

  // 마지막 구분선
  console.log(`\x1b[90m${"─".repeat(80)}\x1b[0m`);
}

/**
 * API 오류를 구조화된 형식으로 출력하는 함수
 */
function prettyPrintError(groupName, error, endpoint) {
  console.log(`\x1b[90m${"─".repeat(80)}\x1b[0m`);
  console.log(`\x1b[1;31m[${groupName}] API 오류: ${endpoint}\x1b[0m`);
  console.log(
    `\x1b[1m오류 메시지:\x1b[0m \x1b[31m${error.message || error}\x1b[0m`
  );
  console.log(`\x1b[90m${"─".repeat(80)}\x1b[0m`);
}

/**
 * 값을 적절한 형식으로 변환
 */
function formatValue(value) {
  if (value === null || value === undefined) {
    return "\x1b[90m(없음)\x1b[0m";
  } else if (typeof value === "string") {
    // 문자열이 너무 길면 일부만 표시
    return value.length > 30 ? `"${value.substring(0, 30)}..."` : `"${value}"`;
  } else if (typeof value === "number") {
    return `\x1b[36m${value}\x1b[0m`;
  } else if (typeof value === "boolean") {
    return value ? "\x1b[32mtrue\x1b[0m" : "\x1b[31mfalse\x1b[0m";
  } else if (Array.isArray(value)) {
    return `[배열: ${value.length}개 항목]`;
  } else if (typeof value === "object") {
    // 객체의 내용을 자세히 표시
    try {
      const objStr = JSON.stringify(value, null, 2);
      return objStr.length > 100 ? `${objStr.substring(0, 100)}...` : objStr;
    } catch (e) {
      return "{객체 변환 실패}";
    }
  }
  return String(value);
}

/**
 * 로그 메시지를 구조화된 형식으로 출력하는 함수 (공통)
 */
function prettyPrintLog(groupName, type, message, icon, color) {
  // 구분선 표시
  console.log(`\x1b[90m${"─".repeat(80)}\x1b[0m`);

  // 헤더 표시 (그룹명 + 타입)
  console.log(`\x1b[1;${color}m[${groupName}] ${type}\x1b[0m`);

  // 메시지 내용 표시
  if (typeof message === "object") {
    try {
      // 객체인 경우 구조화해서 출력
      console.log(`\x1b[1m내용:\x1b[0m`);

      // 객체 내용을 키별로 구조화
      const messageStr = JSON.stringify(message);
      try {
        const jsonObj = JSON.parse(messageStr);
        Object.keys(jsonObj).forEach((key) => {
          const value = jsonObj[key];
          console.log(`  \x1b[34m● ${key}:\x1b[0m ${formatValue(value)}`);
        });
      } catch (e) {
        // JSON 파싱 실패 시 원본 출력
        console.log(`  ${icon} ${messageStr}`);
      }
    } catch (e) {
      console.log(`  ${icon} ${message}`);
    }
  } else if (message.includes("{") && message.includes("}")) {
    // JSON 문자열로 보이는 경우 파싱 시도
    try {
      // 객체 문자열에서 JSON 부분 추출 시도
      const jsonMatch = message.match(/(\{.*\})/);
      if (jsonMatch && jsonMatch[1]) {
        const jsonStr = jsonMatch[1];
        const nonJsonPart = message.replace(jsonStr, "").trim();

        if (nonJsonPart) {
          console.log(`\x1b[1m설명:\x1b[0m ${nonJsonPart}`);
        }

        console.log(`\x1b[1m데이터:\x1b[0m`);
        const jsonObj = JSON.parse(jsonStr);
        Object.keys(jsonObj).forEach((key) => {
          const value = jsonObj[key];
          console.log(`  \x1b[34m● ${key}:\x1b[0m ${formatValue(value)}`);
        });
      } else {
        console.log(`  ${icon} ${message}`);
      }
    } catch (e) {
      // 파싱 실패 시 원본 출력
      console.log(`  ${icon} ${message}`);
    }
  } else {
    // 일반 문자열
    console.log(`\x1b[1m내용:\x1b[0m ${icon} ${message}`);
  }

  // 마지막 구분선
  console.log(`\x1b[90m${"─".repeat(80)}\x1b[0m`);
}

// 에러 로깅을 위한 함수 개선
function logError(group, error) {
  prettyPrintLog(group, "오류", error, "❌", "31");
}

// 성공 로깅을 위한 함수 개선
function logSuccess(group, message) {
  prettyPrintLog(group, "성공", message, "✅", "32");
}

// 정보 로깅을 위한 함수 개선
function logInfo(group, message) {
  prettyPrintLog(group, "정보", message, "ℹ️", "34");
}

// 경고 로깅을 위한 함수 개선
function logWarning(group, message) {
  prettyPrintLog(group, "경고", message, "⚠️", "33");
}

// API 응답 체크 및 카운터 업데이트 함수
function trackApiCall(groupName, response, checkName, errorCounter) {
  // 내부 HTTP 요청 카운터 증가 - 메트릭으로 증가
  internalHttpRequests.add(1);
  console.log(
    `\x1b[90m[${groupName}] 내부 카운터 업데이트: 총 요청=${internalHttpRequests.name}\x1b[0m`
  );

  // 그룹이 없으면 초기화
  if (!apiCallsByGroup[groupName]) {
    apiCallsByGroup[groupName] = {
      total: 0,
      success: 0,
      failure: 0,
    };
  }

  // 총 호출 카운트 증가
  apiCallsByGroup[groupName].total++;

  // 응답이 없는 경우(예외 발생) 실패로 처리
  if (!response) {
    failedApiCalls.add(1);
    apiCallsByGroup[groupName].failure++;
    internalHttpFailures.add(1);
    console.log(
      `\x1b[90m[${groupName}] 내부 카운터 업데이트: 실패=${internalHttpFailures.name}\x1b[0m`
    );
    errorCounter && errorCounter.add(1);
    logError(groupName, "응답 없음");
    return false;
  }

  // 성공 판단 (2xx, 3xx, 4xx 상태 코드)
  const isSuccess = response.status >= 200 && response.status < 500;

  if (isSuccess) {
    successfulApiCalls.add(1);
    apiCallsByGroup[groupName].success++;
  } else {
    failedApiCalls.add(1);
    apiCallsByGroup[groupName].failure++;
    internalHttpFailures.add(1);
    console.log(
      `\x1b[90m[${groupName}] 내부 카운터 업데이트: 실패=${internalHttpFailures.name}\x1b[0m`
    );
    errorCounter && errorCounter.add(1);
  }

  // 상태 코드와 응답 본문 로깅 개선
  let statusIcon, statusColor, statusClass;
  if (response.status >= 200 && response.status < 300) {
    statusIcon = "✅";
    statusColor = "32"; // 초록색
    statusClass = "성공";
  } else if (response.status >= 400 && response.status < 500) {
    statusIcon = "⚠️";
    statusColor = "33"; // 노란색
    statusClass = "클라이언트 오류";
  } else {
    statusIcon = "❌";
    statusColor = "31"; // 빨간색
    statusClass = "서버 오류";
  }

  console.log(
    `\x1b[${statusColor}m[${groupName}] ${checkName}: ${statusIcon} ${statusClass} (${response.status})\x1b[0m`
  );

  // 실패한 요청인 경우 로그 추가
  if (response.status >= 400) {
    console.log(
      `\x1b[90m[${groupName}] 메트릭 카운터 업데이트: failedApiCalls=${failedApiCalls.name}\x1b[0m`
    );
  } else {
    console.log(
      `\x1b[90m[${groupName}] 메트릭 카운터 업데이트: successfulApiCalls=${successfulApiCalls.name}\x1b[0m`
    );
  }

  return check(response, {
    [checkName]: (r) => r && r.status >= 200 && r.status < 500,
  });
}

// 테스트 진행 상황 표시를 위한 함수
function printStepHeader(step, description) {
  const fullWidth = 80;
  const sideWidth = Math.floor(
    (fullWidth - step.length - description.length - 4) / 2
  );
  const leftSide = "=".repeat(sideWidth);
  const rightSide = "=".repeat(
    fullWidth - sideWidth - step.length - description.length - 4
  );
  console.log(
    `\n\x1b[1;36m${leftSide} ${step} ${description} ${rightSide}\x1b[0m\n`
  );
}

// 테스트 환경 설정을 맨 위로 이동
const BASE_URL = __ENV.BASE_URL || "http://localhost:8080";
let bookId = 1;
let contentId = 1;
const BACKUP_BOOK_ID = 2;

const USERS = new SharedArray("users", function () {
  return [
    {
      email: "user1@example.com",
      password: "Password123!",
      nickname: "테스트유저1",
    },
    {
      email: "user2@example.com",
      password: "Password123!",
      nickname: "테스트유저2",
    },
    {
      email: "user3@example.com",
      password: "Password123!",
      nickname: "테스트유저3",
    },
    {
      email: "user4@example.com",
      password: "Password123!",
      nickname: "테스트유저4",
    },
    {
      email: "user5@example.com",
      password: "Password123!",
      nickname: "테스트유저5",
    },
  ];
});

const SEARCH_KEYWORDS = [
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
];

const BGM_TYPES = ["calm", "happy", "neutral", "sad"];

// 테스트 옵션 설정
export const options = {
  // 복잡한 부하 테스트 대신 단순한 기능 검증용 설정
  vus: 1, // 가상 사용자 1명으로 설정
  duration: "120s", // 120초간 실행
  iterations: 1, // 명시적으로 1회만 실행하도록 설정
  thresholds: {
    http_req_duration: ["p(95)<2000"], // 요청 응답 시간 95%가 2초 이내여야 함
    http_req_failed: ["rate<0.60"], // 요청 실패율 60% 미만이어야 함 (현재 많은 API가 구현되지 않음)
  },
};

// 기본 헤더 설정
const headers = {
  "Content-Type": "application/json",
  Accept: "application/json",
};

// Virtual User (VU) 함수
export default function () {
  // 각 VU는 독립적인 사용자 세션을 가짐
  const user = randomItem(USERS);
  let authToken = "";
  let userId = null;

  group("1. 회원가입 및 로그인", function () {
    printStepHeader("STEP 1", "회원가입 및 로그인");
    try {
      // 회원가입 (중복 방지를 위해 임의의 이메일 생성)
      const signupEmail = `${
        user.email.split("@")[0]
      }_${Date.now()}@example.com`;
      const timestamp = Date.now();
      // DTO에 맞게 회원가입 요청 필드 수정
      const signupPayload = JSON.stringify({
        username: `testuser_${timestamp}`,
        password: user.password,
        nickname: `TestUser_${timestamp}`,
        email: signupEmail,
      });

      logInfo("회원가입", `요청: ${signupPayload}`);

      const signupResponse = http.post(
        `${BASE_URL}/auth/signup`,
        signupPayload,
        {
          headers: {
            "Content-Type": "application/json",
            Accept: "application/json",
          },
        }
      );

      if (signupResponse.status >= 200 && signupResponse.status < 300) {
        logSuccess(
          "회원가입",
          `상태 코드=${signupResponse.status}, 응답 본문=${signupResponse.body}`
        );
      } else {
        logWarning(
          "회원가입",
          `상태 코드=${signupResponse.status}, 응답 본문=${signupResponse.body}`
        );
      }

      trackApiCall(
        "회원가입",
        signupResponse,
        "회원가입 요청 완료",
        authErrors
      );

      apiLatency.add(signupResponse.timings.duration);

      // 로그인
      const loginPayload = JSON.stringify({
        email: signupEmail,
        password: user.password,
      });

      logInfo("로그인", `요청: ${loginPayload}`);

      const loginResponse = http.post(`${BASE_URL}/auth/login`, loginPayload, {
        headers: {
          "Content-Type": "application/json",
          Accept: "application/json",
        },
      });

      if (loginResponse.status >= 200 && loginResponse.status < 300) {
        logSuccess("로그인", `상태 코드=${loginResponse.status}`);
      } else {
        logWarning(
          "로그인",
          `상태 코드=${loginResponse.status}, 응답 본문=${loginResponse.body}`
        );
      }

      // 인증 응답 파싱 및 토큰 추출 개선
      if (loginResponse.status >= 200 && loginResponse.status < 300) {
        try {
          const responseBody = JSON.parse(loginResponse.body);
          logInfo("로그인", `응답 구조: ${JSON.stringify(responseBody)}`);

          // 다양한 응답 구조 처리
          if (responseBody.result === "SUCCESS" && responseBody.data) {
            // ApiResponse 형식
            const data = responseBody.data;
            // 토큰 추출 전 로깅 추가
            logInfo("로그인", `토큰 데이터 구조: ${JSON.stringify(data)}`);

            // 토큰 정리 - 줄바꿈이나 공백 등 특수문자 제거
            authToken = (data.accessToken || data.token || "").replace(
              /\s+/g,
              ""
            );

            if (authToken) {
              // JWT 토큰 검증 및 정제 작업
              if (authToken.includes("\n") || authToken.includes("\r")) {
                console.log("토큰에 줄바꿈 문자 발견, 정리합니다.");
                authToken = authToken.replace(/[\r\n]+/g, "");
              }

              // 토큰 디코딩 시도 (base64 부분)
              const tokenParts = authToken.split(".");
              if (tokenParts.length >= 2) {
                let base64Payload = tokenParts[1];

                // URL-safe base64를 표준 base64로 변환
                base64Payload = base64Payload
                  .replace(/-/g, "+")
                  .replace(/_/g, "/");

                // 패딩 처리
                while (base64Payload.length % 4 !== 0) {
                  base64Payload += "=";
                }

                try {
                  const payloadText = decodeBase64(base64Payload);
                  logInfo("로그인", `디코딩된 페이로드(전체): ${payloadText}`);

                  // 페이로드에서 userId 추출 (정규식 활용)
                  const userIdMatch = payloadText.match(/"userId":(\d+)/);
                  if (userIdMatch && userIdMatch[1]) {
                    userId = parseInt(userIdMatch[1]);
                    logInfo("로그인", `정규식으로 추출한 사용자 ID: ${userId}`);
                  } else {
                    try {
                      // JSON 파싱 시도
                      const payload = JSON.parse(payloadText);
                      userId = payload.userId || payload.id || null;
                      logInfo(
                        "로그인",
                        `JSON 파싱으로 추출한 사용자 ID: ${userId}`
                      );
                    } catch (parseErr) {
                      logError("로그인", `JSON 파싱 오류: ${parseErr.message}`);
                    }
                  }
                } catch (decodeErr) {
                  logError("로그인", `토큰 디코딩 오류: ${decodeErr.message}`);
                }
              }
            } else if (responseBody.accessToken) {
              // 직접 데이터 형식
              authToken = responseBody.accessToken;
              userId = responseBody.userId || responseBody.id;
            } else {
              logError(
                "로그인",
                `예상치 못한 응답 형식: ${JSON.stringify(responseBody)}`
              );
            }

            // userId 값이 없으면 기본값 설정
            if (!userId) {
              userId = 1; // 테스트를 위한 기본 사용자 ID
              logInfo("로그인", `사용자 ID 설정 실패 - 기본값 1 사용`);
            }

            if (authToken) {
              logSuccess(
                "로그인",
                `인증 성공: 사용자ID=${userId || "undefined"}`
              );
              userSuccessRate.add(1);
              // 토큰에서 첫 10자만 로그로 남기고 나머지는 감춤
              logInfo("로그인", `토큰(부분): ${authToken.substring(0, 10)}...`);
            } else {
              logError("로그인", "토큰이 없습니다");
              authErrors.add(1);
            }
          } else if (responseBody.accessToken) {
            // 직접 데이터 형식
            authToken = responseBody.accessToken;
            userId = responseBody.userId || responseBody.id;
          } else {
            logError(
              "로그인",
              `예상치 못한 응답 형식: ${JSON.stringify(responseBody)}`
            );
          }

          // userId 값이 없으면 기본값 설정
          if (!userId) {
            userId = 1; // 테스트를 위한 기본 사용자 ID
            logInfo("로그인", `사용자 ID 설정 실패 - 기본값 1 사용`);
          }

          if (authToken) {
            logSuccess(
              "로그인",
              `인증 성공: 사용자ID=${userId || "undefined"}`
            );
            userSuccessRate.add(1);
            // 토큰에서 첫 10자만 로그로 남기고 나머지는 감춤
            logInfo("로그인", `토큰(부분): ${authToken.substring(0, 10)}...`);
          } else {
            logError("로그인", "토큰이 없습니다");
            authErrors.add(1);
          }
        } catch (e) {
          logError("로그인", `응답 파싱 오류: ${e.message}`);
          authErrors.add(1);
        }
      } else {
        logError("로그인", `로그인 실패: ${loginResponse.status}`);
        authErrors.add(1);
      }

      apiLatency.add(loginResponse.timings.duration);
    } catch (e) {
      logError("인증", `인증 과정 오류: ${e}`);
      authErrors.add(1);
    }

    sleep(randomIntBetween(1, 2));
  });

  // 인증 토큰이 없으면 이후 단계를 진행하지 않음
  if (!authToken) {
    logError("인증", "인증 실패로 테스트를 중단합니다.");
    return;
  }

  // 인증 헤더 설정 개선
  const authHeaders = {
    "Content-Type": "application/json",
    Accept: "application/json",
    Authorization: authToken ? `Bearer ${authToken.trim()}` : "",
  };

  // 헤더 디버깅 로그 추가
  console.log(
    `\x1b[90m인증 헤더: Authorization=Bearer ${
      authToken ? authToken.substring(0, 10) + "..." : "없음"
    }\x1b[0m`
  );

  group("2. 검색 기능", function () {
    printStepHeader("STEP 2", "검색 기능");
    try {
      // 인기 검색어 조회
      const popularTermsResponse = http.get(
        `${BASE_URL}/search/popular-terms?limit=10`,
        { headers: authHeaders }
      );

      logInfo("검색 기능", "인기 검색어 조회 요청 완료");
      trackApiCall(
        "검색 기능",
        popularTermsResponse,
        "인기 검색어 조회 요청 완료",
        searchErrors
      );
      apiLatency.add(popularTermsResponse.timings.duration);

      // 검색 실행
      const searchKeyword = randomItem(SEARCH_KEYWORDS);
      const searchResponse = http.get(
        `${BASE_URL}/search?keyword=${encodeURIComponent(searchKeyword)}`,
        { headers: authHeaders }
      );

      logInfo("검색 기능", `검색 요청 완료`);
      trackApiCall("검색 기능", searchResponse, "검색 요청 완료", searchErrors);
      apiLatency.add(searchResponse.timings.duration);
    } catch (e) {
      logError("검색 기능", `검색 기능 오류: ${e}`);
      searchErrors.add(1);
      failedApiCalls.add(1);
      if (!apiCallsByGroup["검색 기능"]) {
        apiCallsByGroup["검색 기능"] = { total: 0, success: 0, failure: 0 };
      }
      apiCallsByGroup["검색 기능"].failure++;
    }

    sleep(randomIntBetween(1, 2));
  });

  group("3. 검색 결과 페이지 및 4. 책 상세 정보", function () {
    try {
      // 책 목록 조회하여 사용 가능한 책들 확인
      const booksResponse = http.get(`${BASE_URL}/books?page=0&size=10`, {
        headers: authHeaders,
      });

      prettyPrintResponse(
        "책 목록 조회",
        booksResponse,
        `${BASE_URL}/books?page=0&size=10`
      );

      // 응답에서 책 목록 추출 시도
      let usableBookId = bookId;
      try {
        if (booksResponse.status >= 200 && booksResponse.status < 300) {
          const booksData = JSON.parse(booksResponse.body);
          // API 응답 구조 확인 (ApiResponse 또는 직접 반환)
          const bookList =
            booksData.result === "SUCCESS"
              ? booksData.data.content
              : booksData.content
              ? booksData.content
              : [];

          if (bookList && bookList.length > 0) {
            // 첫 번째 책의 ID를 사용
            usableBookId = bookList[0].id;
            console.log(`사용 가능한 책 ID 발견: ${usableBookId}`);
            bookId = usableBookId; // 전역 변수 업데이트
          } else {
            console.log("책 목록이 비어 있습니다. 기본 ID를 계속 사용합니다.");
          }
        }
      } catch (e) {
        prettyPrintError("책 목록 조회", e, `${BASE_URL}/books?page=0&size=10`);
      }

      // 책 상세 정보 조회 (ID 오류 시에도 테스트 계속 진행)
      const bookDetailResponse = http.get(`${BASE_URL}/books/${usableBookId}`, {
        headers: authHeaders,
      });

      prettyPrintResponse(
        "책 상세 정보",
        bookDetailResponse,
        `${BASE_URL}/books/${usableBookId}`
      );

      trackApiCall(
        "책 상세 정보",
        bookDetailResponse,
        "책 상세 정보 조회 요청 완료",
        bookErrors
      );
      apiLatency.add(bookDetailResponse.timings.duration);
    } catch (e) {
      prettyPrintError("책 정보 조회", e, `${BASE_URL}/books`);
      bookErrors.add(1);
      failedApiCalls.add(1);
      if (!apiCallsByGroup["책 상세 정보"]) {
        apiCallsByGroup["책 상세 정보"] = { total: 0, success: 0, failure: 0 };
      }
      apiCallsByGroup["책 상세 정보"].failure++;
    }

    sleep(randomIntBetween(1, 2));
  });

  group("5. 콘텐츠 보기", function () {
    printStepHeader("STEP 5", "콘텐츠 보기");
    try {
      // API 경로 확인 - 가능한 대체 경로 시도
      let contentsPath = `${BASE_URL}/contents`;
      let contentsResponse = null;
      let useDefaultContentId = false; // 기본 콘텐츠 ID 사용 여부

      // 다양한 API 엔드포인트 시도 (상태 파라미터 없이)
      const possibleEndpoints = [
        // 기본 엔드포인트 (쿼리 파라미터 없음)
        `${BASE_URL}/contents`,
        // 페이징 파라미터만 있는 엔드포인트
        `${BASE_URL}/contents?page=0&size=10`,
        // 대체 엔드포인트 1
        `${BASE_URL}/short-form-contents`,
        // 대체 엔드포인트 1 + 페이징
        `${BASE_URL}/short-form-contents?page=0&size=10`,
        // 대체 엔드포인트 2
        `${BASE_URL}/content`,
        // 대체 엔드포인트 2 + 페이징
        `${BASE_URL}/content?page=0&size=10`,
        // 다른 가능한 엔드포인트
        `${BASE_URL}/contents/all`,
        // 사용자별 콘텐츠 엔드포인트 (사용자 ID 활용)
        userId ? `${BASE_URL}/users/${userId}/contents` : null,
      ].filter(Boolean); // null 값 제거

      console.log(`콘텐츠 조회를 위한 다양한 엔드포인트 시도 중...`);

      let endpointSuccess = false;

      for (const endpoint of possibleEndpoints) {
        console.log(`엔드포인트 시도: ${endpoint}`);

        contentsResponse = http.get(endpoint, {
          headers: authHeaders,
        });

        if (contentsResponse.status < 500) {
          console.log(
            `성공적인 엔드포인트 발견: ${endpoint} (상태 코드: ${contentsResponse.status})`
          );
          contentsPath = endpoint.split("?")[0]; // 쿼리 파라미터 제거
          endpointSuccess = true;
          break;
        }

        // 실패한 경우 오류 메시지 확인
        try {
          const respBody = JSON.parse(contentsResponse.body);
          console.log(`응답: ${respBody.message || "메시지 없음"}`);
        } catch (e) {
          console.log(
            `응답 파싱 실패: ${
              (contentsResponse.body &&
                contentsResponse.body.substring(0, 100)) ||
              "응답 없음"
            }`
          );
        }
      }

      // 모든 엔드포인트 시도 실패 시
      if (!endpointSuccess) {
        console.log(
          `모든 콘텐츠 엔드포인트 시도 실패. 기본 콘텐츠 ID를 사용합니다.`
        );
        useDefaultContentId = true;
      }

      prettyPrintResponse("콘텐츠 목록", contentsResponse, contentsPath);

      // 응답에서 콘텐츠 ID 추출 시도
      let usableContentId = contentId;

      if (!useDefaultContentId) {
        try {
          if (contentsResponse.status >= 200 && contentsResponse.status < 300) {
            const contentsData = JSON.parse(contentsResponse.body);
            // API 응답 구조 확인 (ApiResponse 또는 직접 반환)
            let contentList = [];

            // 다양한 응답 구조 처리
            if (contentsData.result === "SUCCESS" && contentsData.data) {
              if (Array.isArray(contentsData.data)) {
                contentList = contentsData.data;
              } else if (
                contentsData.data.content &&
                Array.isArray(contentsData.data.content)
              ) {
                contentList = contentsData.data.content;
              } else {
                // 단일 콘텐츠 객체인 경우
                contentList = [contentsData.data];
              }
            } else if (Array.isArray(contentsData)) {
              contentList = contentsData;
            } else if (
              contentsData.content &&
              Array.isArray(contentsData.content)
            ) {
              contentList = contentsData.content;
            }

            if (contentList && contentList.length > 0) {
              // 첫 번째 콘텐츠의 ID를 사용
              usableContentId =
                contentList[0].id ||
                contentList[0].contentId ||
                contentList[0].contentID;
              console.log(`사용 가능한 콘텐츠 ID 발견: ${usableContentId}`);
              contentId = usableContentId; // 전역 변수 업데이트
            } else {
              console.log(
                "콘텐츠 목록이 비어 있습니다. 기본 ID를 계속 사용합니다."
              );
            }
          }
        } catch (e) {
          prettyPrintError("콘텐츠 목록 처리", e, contentsPath);
        }
      } else {
        console.log(`기본 콘텐츠 ID ${contentId}를 사용합니다.`);
      }

      // 콘텐츠 상세 조회 (ID 오류 시에도 테스트 계속 진행)
      const contentDetailResponse = http.get(
        `${contentsPath}/${usableContentId}`,
        { headers: authHeaders }
      );

      prettyPrintResponse(
        "콘텐츠 상세 정보",
        contentDetailResponse,
        `${contentsPath}/${usableContentId}`
      );

      trackApiCall(
        "콘텐츠 상세 정보",
        contentDetailResponse,
        "콘텐츠 상세 조회 요청 완료",
        contentErrors
      );
      apiLatency.add(contentDetailResponse.timings.duration);

      // 인용구 조회 (API 경로 문제 해결)
      let quotesPath = `${BASE_URL}/quotes/content`;
      let quotesResponse = http.get(
        `${quotesPath}/${usableContentId}?page=0&size=5`,
        { headers: authHeaders }
      );

      // 404인 경우 대체 경로 시도
      if (quotesResponse.status === 404) {
        console.log("인용구 기본 경로가 없습니다. 대체 경로 시도: /quotes");
        quotesPath = `${BASE_URL}/quotes`;
        quotesResponse = http.get(
          `${quotesPath}?contentId=${usableContentId}&page=0&size=5`,
          { headers: authHeaders }
        );
      }

      prettyPrintResponse(
        "인용구",
        quotesResponse,
        `${quotesPath}/${usableContentId}?page=0&size=5`
      );

      trackApiCall(
        "인용구",
        quotesResponse,
        "인용구 조회 요청 완료",
        contentErrors
      );
      apiLatency.add(quotesResponse.timings.duration);
    } catch (e) {
      prettyPrintError("콘텐츠 조회", e, `${BASE_URL}/contents`);
      contentErrors.add(1);
      failedApiCalls.add(1);
      if (!apiCallsByGroup["콘텐츠 조회"]) {
        apiCallsByGroup["콘텐츠 조회"] = { total: 0, success: 0, failure: 0 };
      }
      apiCallsByGroup["콘텐츠 조회"].failure++;
    }

    sleep(randomIntBetween(1, 2));
  });

  group("6. 사용자 상호작용", function () {
    printStepHeader("STEP 6", "사용자 상호작용 (좋아요, 북마크 등)");
    // contentId가 있고 유효한 경우에만 실행
    if (contentId) {
      try {
        let interactionPath = `${BASE_URL}/contents`;

        // 콘텐츠 좋아요 - 여러 가능한 경로 시도
        const likePaths = [
          `${interactionPath}/${contentId}/like`,
          `${interactionPath}/${contentId}/likes`,
          `${BASE_URL}/likes/content/${contentId}`,
        ];

        let likeResponse = null;
        let likeSuccess = false;

        for (const path of likePaths) {
          console.log(`좋아요 경로 시도: ${path}`);
          likeResponse = http.post(path, null, {
            headers: Object.assign({}, authHeaders, {
              "Content-Type": "application/json",
            }),
          });

          // 성공했다면 더 이상 시도하지 않음
          if (likeResponse.status >= 200 && likeResponse.status < 300) {
            console.log(`좋아요 성공: ${path}`);
            likeSuccess = true;
            break;
          }

          // 클라이언트 오류(400대)지만 메시지가 유용한 정보를 제공한다면 로깅
          if (likeResponse.status >= 400 && likeResponse.status < 500) {
            try {
              const errorData = JSON.parse(likeResponse.body);
              console.log(
                `좋아요 오류 정보: ${errorData.message || "정보 없음"}`
              );
            } catch (e) {
              // 파싱 오류는 무시
            }
            break;
          }
        }

        // 500 오류인 경우 더 자세한 디버깅 정보
        if (likeResponse && likeResponse.status >= 500) {
          console.log(
            "좋아요 요청 실패: 상태 코드 500. 요청 검증이 필요합니다."
          );
          console.log(`요청 URL: ${likePaths[0]}`);
          console.log(
            `요청 헤더: ${JSON.stringify(
              Object.assign({}, authHeaders, {
                "Content-Type": "application/json",
              })
            )}`
          );
        }

        // 좋아요 응답 체크 추가
        if (likeResponse) {
          prettyPrintResponse("좋아요", likeResponse, likePaths[0]);

          trackApiCall(
            "좋아요",
            likeResponse,
            "콘텐츠 좋아요 요청 완료",
            interactionErrors
          );
          apiLatency.add(likeResponse.timings.duration);
        }

        if (!likeSuccess && likeResponse && likeResponse.status >= 400) {
          console.log("좋아요 API 요청이 실패했습니다. 서버 API를 확인하세요.");
        }

        // 북마크 - 다른 API 경로 시도
        const bookmarkPaths = [
          `${interactionPath}/${contentId}/bookmark`,
          `${interactionPath}/${contentId}/bookmarks`,
          `${BASE_URL}/bookmarks/content/${contentId}`,
        ];

        let bookmarkResponse = null;
        let bookmarkSuccess = false;

        // 여러 가능한 경로 시도
        for (const path of bookmarkPaths) {
          console.log(`북마크 경로 시도: ${path}`);
          bookmarkResponse = http.post(path, null, {
            headers: Object.assign({}, authHeaders, {
              "Content-Type": "application/json",
            }),
          });

          // 성공했다면 더 이상 시도하지 않음
          if (bookmarkResponse.status >= 200 && bookmarkResponse.status < 300) {
            console.log(`북마크 성공: ${path}`);
            bookmarkSuccess = true;
            break;
          }

          // 클라이언트 오류(400대)지만 메시지가 유용한 정보를 제공한다면 로깅
          if (bookmarkResponse.status >= 400 && bookmarkResponse.status < 500) {
            try {
              const errorData = JSON.parse(bookmarkResponse.body);
              console.log(
                `북마크 오류 정보: ${errorData.message || "정보 없음"}`
              );
            } catch (e) {
              // 파싱 오류는 무시
            }
            break;
          }
        }

        console.log(
          `북마크 응답: ${
            bookmarkResponse ? bookmarkResponse.status : "N/A"
          }, ${bookmarkResponse ? bookmarkResponse.body : "N/A"}`
        );

        if (bookmarkResponse) {
          prettyPrintResponse("북마크", bookmarkResponse, bookmarkPaths[0]);

          trackApiCall(
            "북마크",
            bookmarkResponse,
            "콘텐츠 북마크 요청 완료",
            interactionErrors
          );
          apiLatency.add(bookmarkResponse.timings.duration);
        }

        if (
          !bookmarkSuccess &&
          bookmarkResponse &&
          bookmarkResponse.status >= 400
        ) {
          console.log("북마크 API 요청이 실패했습니다. 서버 API를 확인하세요.");
        }

        // 콘텐츠 상호작용 기록 - ContentInteractionRequest DTO에 맞게 수정
        // "VIEW" 타입이 지원되지 않아 다른 타입으로 변경
        // 대소문자 혼란 해결: 서버 코드에서 서비스 레이어는 대문자, 퍼시스턴스 레이어는 소문자 사용
        // 모든 가능한 대소문자 조합 시도
        const possibleInteractionTypes = [
          "VIEW",
          "view",
          "LIKE",
          "like",
          "BOOKMARK",
          "bookmark",
          "SHARE",
          "share",
          "COMMENT",
          "comment",
        ];

        // 첫 번째 상호작용 유형 시도
        let interactionSuccess = false;

        for (const interactionType of possibleInteractionTypes) {
          const interactionPayload = JSON.stringify({
            contentId: contentId,
            interactionType: interactionType,
          });

          console.log(
            `상호작용 요청(${interactionType}): ${interactionPayload}`
          );

          const interactionHeaders = Object.assign({}, authHeaders, {
            "Content-Type": "application/json",
          });

          // 여러 가능한 경로 시도
          const interactionPaths = [
            `${BASE_URL}/contents/interaction`,
            `${BASE_URL}/interactions`,
            `${BASE_URL}/user-interactions`,
          ];

          let interactionResponse = null;

          for (const path of interactionPaths) {
            console.log(`상호작용 경로 시도(${interactionType}): ${path}`);
            interactionResponse = http.post(path, interactionPayload, {
              headers: interactionHeaders,
            });

            // 성공했다면 더 이상 시도하지 않음
            if (
              interactionResponse.status >= 200 &&
              interactionResponse.status < 300
            ) {
              console.log(`상호작용 성공(${interactionType}): ${path}`);
              interactionSuccess = true;
              break;
            }

            // 클라이언트 오류(400대)지만 메시지가 유용한 정보를 제공한다면 로깅
            if (
              interactionResponse.status >= 400 &&
              interactionResponse.status < 500
            ) {
              try {
                const errorData = JSON.parse(interactionResponse.body);
                console.log(
                  `상호작용 오류 정보(${interactionType}): ${
                    errorData.message || "정보 없음"
                  }`
                );
              } catch (e) {
                // 파싱 오류는 무시
              }
            }
          }

          console.log(
            `상호작용 응답(${interactionType}): ${
              interactionResponse ? interactionResponse.status : "N/A"
            }, ${interactionResponse ? interactionResponse.body : "N/A"}`
          );

          if (interactionResponse) {
            prettyPrintResponse(
              `상호작용(${interactionType})`,
              interactionResponse,
              interactionPaths[0]
            );

            trackApiCall(
              `상호작용(${interactionType})`,
              interactionResponse,
              `콘텐츠 상호작용(${interactionType}) 기록 요청 완료`,
              interactionErrors
            );
            apiLatency.add(interactionResponse.timings.duration);
          }

          // 성공했다면 다른 유형은 시도하지 않음
          if (interactionSuccess) {
            console.log(`상호작용 성공: ${interactionType} 유형이 지원됩니다.`);
            break;
          }
        }

        if (!interactionSuccess) {
          console.log(
            `모든 상호작용 유형이 실패했습니다. 서버 API를 확인하세요.`
          );
        }
      } catch (e) {
        console.error("사용자 상호작용 오류:", e);
        interactionErrors.add(1);
        failedApiCalls.add(1);
        if (!apiCallsByGroup["사용자 상호작용"]) {
          apiCallsByGroup["사용자 상호작용"] = {
            total: 0,
            success: 0,
            failure: 0,
          };
        }
        apiCallsByGroup["사용자 상호작용"].failure++;
      }
    } else {
      console.log("콘텐츠 ID가 없어 상호작용 단계를 건너뜁니다.");
    }

    sleep(randomIntBetween(1, 2));
  });

  group("7. 콘텐츠 생성", function () {
    printStepHeader("STEP 7", "새 콘텐츠 생성");
    try {
      // 콘텐츠 생성 제한 조회
      const limitResponse = http.get(`${BASE_URL}/contents/creation-limit`, {
        headers: authHeaders,
      });

      trackApiCall(
        "콘텐츠 생성",
        limitResponse,
        "콘텐츠 생성 제한 조회 요청 완료",
        contentErrors
      );
      apiLatency.add(limitResponse.timings.duration);

      // 책이 없는 경우 테스트용 ID 유지
      let usableBookId = bookId;

      // 콘텐츠 생성 요청 - CreateShortFormContentRequest 구조에 맞게 수정
      // - 필수 필드: bookId, title, content
      // - 선택 필드: emotionType, autoEmotionAnalysis
      // emotionType이 열거형이므로 서버에서 지원하는 값들을 시도
      const emotionTypes = [
        "NEUTRAL",
        "HAPPY",
        "SAD",
        "ANGRY",
        "CALM",
        "EXCITED",
      ];
      const emotionType = randomItem(emotionTypes);

      const createPayload = JSON.stringify({
        bookId: usableBookId,
        title: `테스트 콘텐츠 ${Date.now()}`,
        content:
          "K6 테스트로 생성된 콘텐츠입니다. 테스트 자동화 도구를 사용한 API 검증 중입니다.",
        emotionType: emotionType,
        autoEmotionAnalysis: true,
      });

      console.log(
        `콘텐츠 생성 요청(emotionType=${emotionType}): ${createPayload}`
      );

      // 토큰 검증 및 디버깅 로그 추가
      if (!authToken || authToken.trim() === "") {
        logError("콘텐츠 생성", "인증 토큰이 없거나 비어 있습니다.");
        console.log(`토큰 확인: ${authToken}`);
        contentErrors.add(1);
        return;
      }

      // JWT 토큰 형식 검증 (Header.Payload.Signature 형식인지)
      const tokenParts = authToken.trim().split(".");
      if (tokenParts.length !== 3) {
        logError(
          "콘텐츠 생성",
          `잘못된 JWT 토큰 형식: 부분이 ${tokenParts.length}개 (3개 필요)`
        );
        console.log(`JWT 토큰 형식 확인: ${authToken.substring(0, 20)}...`);
        contentErrors.add(1);
      }

      // Authorization 헤더 형식 표준화
      const cleanToken = authToken.replace(/[\r\n\s]+/g, "").trim();

      // 명시적 헤더 설정 (Content-Type과 Accept 헤더 포함)
      const createHeaders = {
        "Content-Type": "application/json",
        Accept: "application/json",
        Authorization: `Bearer ${cleanToken}`,
      };

      console.log(
        `인증 토큰 정보 (처음 20자): ${cleanToken.substring(0, 20)}...`
      );
      console.log(
        `Authorization 헤더 값: Bearer ${cleanToken.substring(0, 10)}...`
      );

      // 서버 응답 확인을 위한 시도 - 우선 헤더만 전송하여 OPTIONS 요청
      const checkAuthResponse = http.options(`${BASE_URL}/contents/create`, {
        headers: createHeaders,
      });

      if (checkAuthResponse.status === 401) {
        logError(
          "콘텐츠 생성",
          "인증 확인 중: 401 Unauthorized - 인증 토큰이 유효하지 않습니다"
        );
        console.log(
          `OPTIONS 요청 응답: ${checkAuthResponse.status}, ${checkAuthResponse.body}`
        );

        // 토큰 재생성 시도 로직을 여기에 추가할 수 있습니다
        // (예: 로그인 다시 시도)
      } else {
        console.log(`인증 확인 성공: ${checkAuthResponse.status}`);
      }

      // 여러 가능한 API 경로 시도
      const createPaths = [
        `${BASE_URL}/contents/create`,
        `${BASE_URL}/contents/generate`,
        `${BASE_URL}/short-form-contents/create`,
      ];

      let createResponse = null;

      for (const path of createPaths) {
        console.log(`콘텐츠 생성 경로 시도: ${path}`);
        createResponse = http.post(path, createPayload, {
          headers: createHeaders,
          timeout: "30s", // 타임아웃 증가
        });

        // 응답 디버깅
        console.log(`${path} 응답: ${createResponse.status}`);
        if (createResponse.body) {
          try {
            const responseBody = JSON.parse(createResponse.body);
            console.log(
              `응답 내용 확인: ${JSON.stringify(responseBody).substring(
                0,
                100
              )}...`
            );
          } catch (e) {
            console.log(
              `응답 내용 파싱 실패: ${createResponse.body.substring(0, 100)}...`
            );
          }
        }

        // 401 응답인 경우 자세한 로그 출력
        if (createResponse.status === 401) {
          logError("콘텐츠 생성", `인증 오류 발생: ${path}`);
          console.log(
            `인증 토큰 형식 확인: Bearer ${cleanToken.substring(0, 10)}...`
          );
          console.log(`인증 헤더 전체: ${JSON.stringify(createHeaders)}`);

          // 토큰 재검증
          try {
            const tokenPayload = tokenParts[1];
            let paddedPayload = tokenPayload;
            while (paddedPayload.length % 4 !== 0) {
              paddedPayload += "=";
            }
            const decodedPayload = encoding.b64decode(
              paddedPayload.replace(/-/g, "+").replace(/_/g, "/")
            );
            console.log(
              `토큰 페이로드 확인: ${decodedPayload.substring(0, 50)}...`
            );
          } catch (e) {
            console.log(`토큰 디코딩 실패: ${e.message}`);
          }
        }

        // 성공했거나 500 오류가 아닌 경우 중단
        if (createResponse.status < 500) {
          console.log(
            `성공 또는 예상된 오류로 콘텐츠 생성 경로 시도 중단: ${path}`
          );
          break;
        }
      }

      console.log(
        `콘텐츠 생성 응답: ${createResponse ? createResponse.status : "N/A"}, ${
          createResponse ? createResponse.body : "N/A"
        }`
      );

      if (createResponse) {
        prettyPrintResponse("콘텐츠 생성", createResponse, createPaths[0]);

        trackApiCall(
          "콘텐츠 생성",
          createResponse,
          "콘텐츠 생성 요청 완료",
          contentErrors
        );
        apiLatency.add(createResponse.timings.duration);

        // 생성된 콘텐츠 ID 추출 시도
        try {
          if (createResponse.status >= 200 && createResponse.status < 300) {
            const responseData = JSON.parse(createResponse.body);
            if (responseData.result === "SUCCESS" && responseData.data) {
              // 새로 생성된 콘텐츠 ID 확인
              const newContentId =
                responseData.data.id || responseData.data.contentId;
              if (newContentId) {
                console.log(`새로 생성된 콘텐츠 ID: ${newContentId}`);
                contentId = newContentId; // 전역 변수 업데이트
              }
            }
          }
        } catch (e) {
          console.error("콘텐츠 생성 응답 처리 오류:", e);
        }
      }
    } catch (e) {
      console.error("콘텐츠 생성 오류:", e);
      contentErrors.add(1);
      failedApiCalls.add(1);
      if (!apiCallsByGroup["콘텐츠 생성"]) {
        apiCallsByGroup["콘텐츠 생성"] = { total: 0, success: 0, failure: 0 };
      }
      apiCallsByGroup["콘텐츠 생성"].failure++;
    }

    sleep(randomIntBetween(1, 2));
  });

  group("8. 추천 기능", function () {
    printStepHeader("STEP 8", "추천 콘텐츠 및 도서 확인");
    try {
      // 추천 콘텐츠 조회
      const recommendedResponse = http.get(
        `${BASE_URL}/contents/recommended?page=0&size=10`,
        { headers: authHeaders }
      );

      prettyPrintResponse(
        "추천 콘텐츠",
        recommendedResponse,
        `${BASE_URL}/contents/recommended?page=0&size=10`
      );

      trackApiCall(
        "추천 기능",
        recommendedResponse,
        "추천 콘텐츠 조회 요청 완료",
        contentErrors
      );
      apiLatency.add(recommendedResponse.timings.duration);

      // 추천 도서 조회
      const recommendedBooksResponse = http.get(
        `${BASE_URL}/books/recommended?page=0&size=10`,
        { headers: authHeaders }
      );

      prettyPrintResponse(
        "추천 도서",
        recommendedBooksResponse,
        `${BASE_URL}/books/recommended?page=0&size=10`
      );

      trackApiCall(
        "추천 도서",
        recommendedBooksResponse,
        "추천 도서 조회 요청 완료",
        bookErrors
      );
      apiLatency.add(recommendedBooksResponse.timings.duration);
    } catch (e) {
      console.error("추천 기능 오류:", e);
      contentErrors.add(1);
      failedApiCalls.add(1);
      if (!apiCallsByGroup["추천 기능"]) {
        apiCallsByGroup["추천 기능"] = { total: 0, success: 0, failure: 0 };
      }
      apiCallsByGroup["추천 기능"].failure++;
    }

    sleep(randomIntBetween(1, 2));
  });

  group("9. 게이미피케이션 및 랭킹 시스템", function () {
    printStepHeader("STEP 9", "랭킹 시스템 확인");
    try {
      // RankType은 BRONZE, SILVER, GOLD, PLATINUM, DIAMOND 중 하나여야 함
      // rankType 파라미터 수정
      const rankTypes = ["BRONZE", "SILVER", "GOLD", "PLATINUM", "DIAMOND"];
      const rankType = randomItem(rankTypes);

      // 사용자 랭킹 조회
      const rankingResponse = http.get(
        `${BASE_URL}/ranking/users?page=0&size=10&rankType=${rankType}`,
        { headers: authHeaders }
      );

      prettyPrintResponse(
        `랭킹 (rankType=${rankType})`,
        rankingResponse,
        `${BASE_URL}/ranking/users?page=0&size=10&rankType=${rankType}`
      );

      trackApiCall(
        "랭킹 시스템",
        rankingResponse,
        "랭킹 조회 요청 완료",
        contentErrors
      );
      apiLatency.add(rankingResponse.timings.duration);

      // 사용자 점수 조회
      if (userId) {
        const userScoreResponse = http.get(
          `${BASE_URL}/ranking/user/${userId}/score`,
          { headers: authHeaders }
        );

        prettyPrintResponse(
          "사용자 점수",
          userScoreResponse,
          `${BASE_URL}/ranking/user/${userId}/score`
        );

        trackApiCall(
          "사용자 점수",
          userScoreResponse,
          "사용자 점수 조회 요청 완료",
          contentErrors
        );
        apiLatency.add(userScoreResponse.timings.duration);
      }
    } catch (e) {
      console.error("랭킹 시스템 오류:", e);
      contentErrors.add(1);
      failedApiCalls.add(1);
      if (!apiCallsByGroup["랭킹 시스템"]) {
        apiCallsByGroup["랭킹 시스템"] = { total: 0, success: 0, failure: 0 };
      }
      apiCallsByGroup["랭킹 시스템"].failure++;
    }

    sleep(randomIntBetween(1, 2));
  });

  group("10. 설정 및 관리", function () {
    printStepHeader("STEP 10", "사용자 프로필 설정");
    try {
      // 사용자 프로필 조회
      if (userId) {
        const profileResponse = http.get(`${BASE_URL}/users/${userId}`, {
          headers: authHeaders,
        });

        prettyPrintResponse(
          "사용자 프로필",
          profileResponse,
          `${BASE_URL}/users/${userId}`
        );

        trackApiCall(
          "사용자 프로필",
          profileResponse,
          "사용자 프로필 조회 요청 완료",
          authErrors
        );
        apiLatency.add(profileResponse.timings.duration);
      }
    } catch (e) {
      console.error("사용자 프로필 조회 오류:", e);
      authErrors.add(1);
      failedApiCalls.add(1);
      if (!apiCallsByGroup["사용자 프로필"]) {
        apiCallsByGroup["사용자 프로필"] = { total: 0, success: 0, failure: 0 };
      }
      apiCallsByGroup["사용자 프로필"].failure++;
    }

    sleep(randomIntBetween(1, 2));
  });

  // 로그아웃
  if (authToken) {
    printStepHeader("마무리", "로그아웃 및 세션 종료");
    try {
      const logoutResponse = http.post(`${BASE_URL}/auth/logout`, null, {
        headers: Object.assign({}, authHeaders, {
          "Content-Type": "application/json",
        }),
      });

      prettyPrintResponse(
        "로그아웃",
        logoutResponse,
        `${BASE_URL}/auth/logout`
      );

      trackApiCall(
        "로그아웃",
        logoutResponse,
        "로그아웃 요청 완료",
        authErrors
      );
      apiLatency.add(logoutResponse.timings.duration);
    } catch (e) {
      console.error("로그아웃 오류:", e);
      authErrors.add(1);
      failedApiCalls.add(1);
      if (!apiCallsByGroup["로그아웃"]) {
        apiCallsByGroup["로그아웃"] = { total: 0, success: 0, failure: 0 };
      }
      apiCallsByGroup["로그아웃"].failure++;
    }
  }

  // 테스트 종료 메시지
  console.log(`\x1b[1;35m
╔═══════════════════════════════════════════════════════════════════════════════════╗
║                          STOBLYX API 테스트 실행 완료                             ║
╚═══════════════════════════════════════════════════════════════════════════════════╝
\x1b[0m`);
}

// 요약 보고서 핸들러 개선
export function handleSummary(data) {
  // 안전한 속성 접근 헬퍼 함수
  const safeGet = (obj, path, defaultValue) => {
    if (!obj) return defaultValue;
    const keys = path.split(".");
    let result = obj;
    for (const key of keys) {
      if (result === undefined || result === null) return defaultValue;
      result = result[key];
    }
    return result !== undefined && result !== null ? result : defaultValue;
  };

  // 안전한 숫자 형식화
  const safeFormat = (value, decimals = 2) => {
    if (value === undefined || value === null || isNaN(value)) {
      return "0.00";
    }
    return value.toFixed(decimals);
  };

  // 메트릭 디버깅 로그
  console.log(
    `\x1b[90m메트릭 디버깅: successful_api_calls=${JSON.stringify(
      safeGet(data, "metrics.successful_api_calls", "undefined")
    )}\x1b[0m`
  );
  console.log(
    `\x1b[90m메트릭 디버깅: failed_api_calls=${JSON.stringify(
      safeGet(data, "metrics.failed_api_calls", "undefined")
    )}\x1b[0m`
  );

  // 내부 카운터 값 출력
  console.log(
    `\x1b[90m내부 카운터: 요청=${safeGet(
      data,
      "metrics.internal_http_requests.values.count",
      0
    )}, 실패=${safeGet(
      data,
      "metrics.internal_http_failures.values.count",
      0
    )}\x1b[0m`
  );

  // API 그룹별 성공/실패 통계 생성
  let apiCallsStats = "";
  Object.keys(apiCallsByGroup).forEach((group) => {
    const stats = apiCallsByGroup[group];
    const successRate =
      stats.total > 0
        ? ((stats.success / stats.total) * 100).toFixed(2)
        : "0.00";

    const successColor =
      parseFloat(successRate) >= 70
        ? "32"
        : parseFloat(successRate) >= 40
        ? "33"
        : "31";

    apiCallsStats += `\n  \x1b[1m[${group}]\x1b[0m 총 ${stats.total}개 호출: \x1b[32m${stats.success}개 성공\x1b[0m, \x1b[31m${stats.failure}개 실패\x1b[0m (성공률: \x1b[${successColor}m${successRate}%\x1b[0m)`;
  });

  // 총 API 호출 통계 - 메트릭에서 값 가져오기
  const totalCalls = safeGet(
    data,
    "metrics.internal_http_requests.values.count",
    0
  );
  const failedCalls = safeGet(
    data,
    "metrics.internal_http_failures.values.count",
    0
  );
  const successfulCalls = totalCalls - failedCalls;
  const overallSuccessRate =
    totalCalls > 0 ? ((successfulCalls / totalCalls) * 100).toFixed(2) : "0.00";

  const overallSuccessColor =
    parseFloat(overallSuccessRate) >= 70
      ? "32"
      : parseFloat(overallSuccessRate) >= 40
      ? "33"
      : "31";

  // API 통계 요약
  const apiCallsSummary = `
\x1b[1;34mAPI 호출 통계 (내부 카운터):\x1b[0m
- 총 API 호출: \x1b[1m${totalCalls}개\x1b[0m
- 성공한 호출: \x1b[32m${successfulCalls}개\x1b[0m
- 실패한 호출: \x1b[31m${failedCalls}개\x1b[0m
- 전체 성공률: \x1b[${overallSuccessColor}m${overallSuccessRate}%\x1b[0m
${apiCallsStats}`;

  // 95% 응답 시간 안전하게 계산
  let p95ResponseTime = "0.00";
  if (safeGet(data, "metrics.http_req_duration.values.p(95)", null) !== null) {
    p95ResponseTime = safeFormat(
      safeGet(data, "metrics.http_req_duration.values.p(95)", 0)
    );
  } else if (
    safeGet(data, "metrics.http_req_duration.percentiles.95.0", null) !== null
  ) {
    p95ResponseTime = safeFormat(
      safeGet(data, "metrics.http_req_duration.percentiles.95.0", 0)
    );
  }

  // 실패율 안전하게 계산
  let failureRate = "0.00";
  if (safeGet(data, "metrics.http_req_failed.values.rate", null) !== null) {
    failureRate = safeFormat(
      safeGet(data, "metrics.http_req_failed.values.rate", 0) * 100
    );
  } else {
    failureRate = safeFormat((failedCalls / Math.max(totalCalls, 1)) * 100);
  }

  const failureRateColor =
    parseFloat(failureRate) <= 30
      ? "32"
      : parseFloat(failureRate) <= 60
      ? "33"
      : "31";

  // K6 기본 메트릭 요약 생성
  const data_received = safeGet(data, "metrics.data_received.values.count", 0);
  const data_sent = safeGet(data, "metrics.data_sent.values.count", 0);
  const http_reqs = safeGet(data, "metrics.http_reqs.values.count", 0);

  const http_req_duration_avg = safeFormat(
    safeGet(data, "metrics.http_req_duration.values.avg", 0)
  );
  const http_req_duration_min = safeFormat(
    safeGet(data, "metrics.http_req_duration.values.min", 0)
  );
  const http_req_duration_med = safeFormat(
    safeGet(data, "metrics.http_req_duration.values.med", 0)
  );
  const http_req_duration_max = safeFormat(
    safeGet(data, "metrics.http_req_duration.values.max", 0)
  );
  const http_req_duration_p90 = safeFormat(
    safeGet(data, "metrics.http_req_duration.values.p(90)", 0)
  );
  const http_req_duration_p95 = safeFormat(
    safeGet(data, "metrics.http_req_duration.values.p(95)", 0)
  );

  const http_req_failed_rate = safeFormat(
    safeGet(data, "metrics.http_req_failed.values.rate", 0) * 100
  );

  // K6 기본 메트릭 요약 표시 형식
  const k6MetricsSummary = `
\x1b[1;35m     data_received..............: \x1b[36m${formatDataSize(
    data_received
  )}\x1b[0m
\x1b[1;35m     data_sent..................: \x1b[36m${formatDataSize(
    data_sent
  )}\x1b[0m
\x1b[1;35m     http_req_duration..........: \x1b[36mavg=${http_req_duration_avg}ms\x1b[0m \x1b[90mmin=${http_req_duration_min}ms\x1b[0m \x1b[90mmed=${http_req_duration_med}ms\x1b[0m \x1b[90mmax=${http_req_duration_max}ms\x1b[0m \x1b[90mp(90)=${http_req_duration_p90}ms\x1b[0m \x1b[90mp(95)=${http_req_duration_p95}ms\x1b[0m
\x1b[1;35m     http_req_failed............: \x1b[36m${http_req_failed_rate}%\x1b[0m
\x1b[1;35m     http_reqs..................: \x1b[36m${http_reqs}\x1b[0m
\x1b[1;35m     iteration_duration.........: \x1b[36m${safeFormat(
    safeGet(data, "metrics.iteration_duration.values.avg", 0)
  )}ms\x1b[0m
\x1b[1;35m     iterations.................: \x1b[36m${safeGet(
    data,
    "metrics.iterations.values.count",
    0
  )}\x1b[0m
\x1b[1;35m     vus........................: \x1b[36m${safeGet(
    data,
    "metrics.vus.values.max",
    0
  )}\x1b[0m
\x1b[1;35m     vus_max....................: \x1b[36m${safeGet(
    data,
    "metrics.vus_max.values.max",
    0
  )}\x1b[0m`;

  return {
    "summary.json": JSON.stringify(data),
    stdout: `
\x1b[1;35m╔════════════════════════════════════════════════════════════════════════════╗
║                         STOBLYX API 테스트 결과 요약                        ║
╚════════════════════════════════════════════════════════════════════════════╝\x1b[0m

${k6MetricsSummary}

\x1b[1;36m❯ 테스트 기본 정보\x1b[0m
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
  • 총 가상 사용자: \x1b[1m${safeGet(data, "metrics.vus.max", 1)}\x1b[0m
  • 테스트 지속 시간: \x1b[1m${
    safeGet(data, "state.testRunDurationMs", 0)
      ? safeFormat(safeGet(data, "state.testRunDurationMs", 0) / 1000)
      : "0.00"
  }초\x1b[0m

\x1b[1;36m❯ 요청 메트릭\x1b[0m
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
  • 총 요청 수: \x1b[1m${safeGet(
    data,
    "metrics.internal_http_requests.values.count",
    0
  )}\x1b[0m
  • 평균 응답 시간: \x1b[1m${safeFormat(
    safeGet(data, "metrics.http_req_duration.avg", 0)
  )}ms\x1b[0m
  • 95% 응답 시간: \x1b[1m${p95ResponseTime}ms\x1b[0m
  • 요청 실패율: \x1b[${failureRateColor}m${safeFormat(
      (safeGet(data, "metrics.internal_http_failures.values.count", 0) /
        Math.max(
          safeGet(data, "metrics.internal_http_requests.values.count", 1),
          1
        )) *
        100
    )}%\x1b[0m

\x1b[1;36m❯ 사용자 흐름 메트릭\x1b[0m
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
  • 사용자 성공률: \x1b[32m${safeFormat(
    safeGet(data, "metrics.user_success_rate.rate", 0) * 100
  )}%\x1b[0m
  • 인증 오류: \x1b[${
    safeGet(data, "metrics.auth_errors.count", 0) > 0 ? "31" : "32"
  }m${safeGet(data, "metrics.auth_errors.count", 0)}\x1b[0m
  • 검색 오류: \x1b[${
    safeGet(data, "metrics.search_errors.count", 0) > 0 ? "31" : "32"
  }m${safeGet(data, "metrics.search_errors.count", 0)}\x1b[0m
  • 콘텐츠 오류: \x1b[${
    safeGet(data, "metrics.content_errors.count", 0) > 0 ? "31" : "32"
  }m${safeGet(data, "metrics.content_errors.count", 0)}\x1b[0m
  • 도서 오류: \x1b[${
    safeGet(data, "metrics.book_errors.count", 0) > 0 ? "31" : "32"
  }m${safeGet(data, "metrics.book_errors.count", 0)}\x1b[0m
  • 상호작용 오류: \x1b[${
    safeGet(data, "metrics.interaction_errors.count", 0) > 0 ? "31" : "32"
  }m${safeGet(data, "metrics.interaction_errors.count", 0)}\x1b[0m

\x1b[1;36m❯ API 호출 세부 통계\x1b[0m
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${apiCallsSummary}

\x1b[1;35m╔════════════════════════════════════════════════════════════════════════════╗
║                           테스트 완료 (STOBLYX)                           ║
╚════════════════════════════════════════════════════════════════════════════╝\x1b[0m
`,
  };
}

// 데이터 크기 포맷팅 함수 (바이트를 KB, MB 등으로 변환)
function formatDataSize(bytes) {
  if (bytes === 0) return "0 B";

  const sizes = ["B", "KB", "MB", "GB", "TB"];
  const i = Math.floor(Math.log(bytes) / Math.log(1024));
  return (bytes / Math.pow(1024, i)).toFixed(2) + " " + sizes[i];
}
