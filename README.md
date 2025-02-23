## 스토블릭스 (Stoblyx) - 이야기의 오벨리스크

### **Stoblyx = Story + Obelisk**

"책 속의 한 문장은 사라지지 않는다. 그것은 오벨리스크처럼 남아, 사람들에게 영감을 준다."

책 속 문장 하나하나가 디지털 기념비(Obelisk)처럼 기억되고, AI를 통해 숏폼 영상으로 재탄생하는 곳. 과거, 현재, 미래를 잇는 독서의 타임캡슐.

---

## 목차

1. @프로젝트 개요
2. @Why Stoblyx?
3. @Stoblyx만의 차별점
4. @아키텍처 설계
5. @주요 기능
6. @데이터베이스 설계
7. @API 문서
8. @개발 환경 설정
9. @보안 및 확장성 고려 사항
10. @트러블슈팅
11. @프로젝트 구조
12. @테스트 및 품질 관리
13. @성능 최적화 및 모니터링
14. @배포 및 운영 전략
15. @개발자 노트
16. @라이선스

---

## 1. 프로젝트 개요

**프로젝트명:** 스토블릭스 (Stoblyx)  
**개발 기간:** 1개월 (2025년 2월 ~ 2025년 3월)

### 기술 스택

- **Backend:** Java 17, Spring Boot 3.3.9
- **Database:** MySQL 8.0.41
- **Cache:** Redis 7.0.15
- **Security:** JWT, Spring Security
- **AI Integration:** KoBART (텍스트 요약), Pika Labs (AI 숏폼 영상 생성), Google Cloud TTS (음성 변환)
- **Deployment:** Koyeb + GitHub Actions
- **모니터링:** Prometheus, Grafana

---

## 2. Why Stoblyx?

### 문제 인식

- MZ세대의 독서율 감소 및 숏폼 콘텐츠 소비 증가
- 책 속의 의미 있는 문장이 잊혀지는 현실

### 해결 방안

- AI 기술로 문장을 현대적 콘텐츠로 재해석
- 사용자 참여 유도를 위한 게이미피케이션 적용

### 기대 효과

- 독서 문화 활성화 및 세대 간 문화 격차 해소
- 지식 공유 플랫폼으로 성장

---

## 3. Stoblyx만의 차별점

### 1. AI 기반 문구 → 숏폼 영상 변환

- 키워드 기반 문구 추출 및 자동 영상 생성
- 영상 요소: 책 표지, 문장, 배경 이미지, 자막, 감성 기반 BGM 적용
- 비동기 처리 및 폴백 전략으로 안정적인 서비스 제공

### 2. 검색어 기반 유저 추천

- 협업 필터링을 활용한 사용자 맞춤 추천 시스템
- 추천 기준: 최근 검색어, 좋아요/저장 데이터, 활동 패턴

### 3. 게이미피케이션 & 랭킹 시스템

| 랭크   | 조건                 |
|------|--------------------|
| 브론즈  | 기본 기능 사용 가능        |
| 실버   | 인기 문구 TOP 10 확인 가능 |
| 골드   | 100+ 좋아요 문구 저장 가능  |
| 플래티넘 | AI 추천 영상 제작 가능     |
| 다이아  | 콘텐츠 트렌드 피드 노출      |

#### 랭킹 산정 공식

```
점수 = (좋아요 × 2) + (저장수 × 3) + (댓글 × 1) - (신고수 × 5)
```

- **부정 행위 방지:** 동일 IP 다중 계정 차단 및 자동 계정 정지 정책 적용
- **랭킹 리셋 주기:** 매월 1일

---

## 4. 아키텍처 설계

### 시스템 구성 및 계층 설명

![architecture.png](src/docs/diagrams/architecture.png)

#### 설명

- **Adapter Layer:** 외부 요청 처리 (REST API), AI 서비스 호출, 메시징 시스템 통합
- **Application Layer:** 비즈니스 유스케이스 처리 및 트랜잭션 관리
- **Domain Layer:** 핵심 도메인 로직 및 엔티티 관리
- **비동기 처리:** RabbitMQ를 통한 작업 큐 처리로 확장성과 안정성 확보

### 시스템 흐름도

![flowchart.png](src/docs/diagrams/flowchart.png)

---

## 5. 주요 기능

### 회원 시스템

- JWT 기반 인증/인가
- Access/Refresh Token 분리 및 Redis 세션 관리
- BCrypt를 통한 비밀번호 안전 저장
- 세션 고정 공격 방지를 위한 Redis 설정 적용

### 문구 및 AI 추천 기능

- 문구 검색 및 AI 기반 영상 추천
- AI 요약 실패 시 폴백: "첫 문장 + 마지막 문장 조합" 제공
- Redis 캐싱 및 TTL 적용으로 빠른 응답 처리

### 커뮤니티 기능

- 좋아요, 댓글, 문구 저장 및 트렌드 피드 제공
- 신고 기능 및 자동 임시 정지 기능 포함

---

## 6. 데이터베이스 설계

### 주요 테이블 및 관계

#### 테이블 개요

![erd.png](src/docs/diagrams/erd.png)

- **User:** 사용자 정보 및 활동 관리
- **UserInterest:** 사용자 관심사 저장 (1:1)
- **Book:** 책 정보 저장 및 문구와 연결 (1:N)
- **Quote:** 문구 정보 (사용자, 책과 연결) (N:1)
- **Video:** 문구 기반 영상 정보 (1:1)
- **Comment:** 문구 및 사용자 기반 댓글 관리 (N:1)
- **Like:** 사용자의 문구 좋아요 관리 (N:1)
- **SavedQuotes:** 사용자 문구 저장 관리 (N:1)
- **Summary:** 책 요약 정보 (1:N)

#### 관계 설명

- **User ↔ Quote:** 사용자별 작성 문구 관리 (1:N)
- **User ↔ UserInterest:** 사용자 관심사 저장 (1:1)
- **Quote ↔ Video:** 문구별 영상 (1:1)
- **Quote ↔ Comment:** 문구에 대한 댓글 (1:N)
- **Quote ↔ Like:** 문구 좋아요 기록 (1:N)
- **Quote ↔ SavedQuotes:** 문구 저장 관리 (1:N)
- **Book ↔ Quote:** 책에 속한 문구 (1:N)
- **Book ↔ Summary:** 책 요약 정보 (1:N)

---

## 7. API 문서

### 공통 사항

- **Base URL:** `/api/v1`
- **인증:** Bearer Token 방식 (JWT)
- **권한 요구사항:** 일부 엔드포인트는 인증 필요 (표기됨)
- **에러 코드 및 메시지 예시:**

```json
{
  "status": 403,
  "error": "Forbidden",
  "message": "접근 권한이 없습니다.",
  "timestamp": "2025-02-23T10:15:30Z"
}
```

### 시퀀스 다이어그램

![sequence.png](src/docs/diagrams/sequence.png)

### AI 추천 영상 생성 API

**엔드포인트:** `/api/v1/quotes/{id}/generate-video`  
**메서드:** POST  
**권한:** 인증 필요

#### 요청 예시

```json
{
  "style": "minimalist",
  "bgmType": "calm"
}
```

#### 응답 예시 (201 Created)

```json
{
  "videoId": "a1b2c3d4",
  "processingTime": 45,
  "previewUrl": "https://cdn.stoblyx.com/previews/a1b2c3d4.mp4"
}
```

---

## 8. 개발 환경 설정

### .env.example

```properties
# DB 설정
DB_URL=jdbc:mysql://localhost:3306/stoblyx
DB_USER=root
DB_PASS=encrypted_password
# JWT 설정
JWT_SECRET=your_256bit_secret
JWT_EXPIRATION=86400000  # 24시간
```

---

## 9. 보안 및 확장성 고려 사항

### 보안 강화 방안

- **입력값 검증:** Jakarta Bean Validation 적용
- **XSS 방지:** Lucy-XSS-Filter 사용
- **CSRF 보호:** Stateless 환경 대비 JWT 검증 강화
- **SQL Injection 방지:** JPA Parameter Binding 강제화
- **Redis 세션 보호:** 세션 고정 공격 방지를 위한 토큰 검증 추가

---

## 10. 트러블슈팅

### 문제: AI 영상 생성 API 타임아웃 (30초 초과)

#### 해결책

```java

@Async
@Retryable(maxAttempts = 3, backoff = @Backoff(delay = 2000))
public CompletableFuture<Video> generateVideoAsync(Quote quote) {
    return aiService.generateVideo(quote)
        .exceptionally(ex -> backupGenerator.createBasicVideo(quote));
}
```

- **폴백 전략:** 기본 템플릿 영상 생성
- **재시도 정책:** 최대 3회 재시도, 지수 백오프 적용

---

## 11. 프로젝트 구조

계층형 아키텍처 + 도메인 중심 설계

### **계층형 아키텍처 + 도메인 중심 설계를 선택한 이유**

#### 1. **유지보수성과 확장성을 고려한 구조**

- **도메인 중심 설계(Domain-Driven Design, DDD)**를 기반으로 계층을 나누면, 변경이 발생해도 특정 계층만 수정하면 되므로 **유지보수성이 향상**된다.
- 새로운 도메인 기능을 추가할 때 기존 코드와 분리된 **독립적인 도메인 로직**을 유지할 수 있어 확장성이 뛰어나다.

#### 2. **비즈니스 로직과 인프라 코드의 분리**

- 도메인 로직과 데이터 접근 코드가 혼재되면 **비즈니스 로직이 인프라 세부 사항에 의존**하게 되어 유지보수가 어렵다.
- 계층형 아키텍처를 적용하면 **Application Layer에서 비즈니스 로직을 집중적으로 관리**하고, 인프라스트럭처(데이터베이스, 메시징 시스템, 캐시 등)와 분리할 수 있다.

#### 3. **헥사고날 아키텍처(Hexagonal Architecture)와의 연계**

- 계층형 아키텍처를 기반으로 하면 향후 헥사고날 아키텍처와 같은 **포트-어댑터 구조**로 확장하기 용이하다.
- 특히 **Adapter Layer를 통해 AI 서비스, 외부 API, 메시징 시스템과의 연결을 단순화**할 수 있어, 외부 시스템 변경이 발생하더라도 내부 도메인 로직에는 영향을 최소화할 수 있다.

#### 4. **비즈니스 로직의 명확한 경계 설정**

- **Application Layer**에서는 비즈니스 유스케이스를 처리하고, **Domain Layer**에서는 순수한 도메인 모델과 로직을 유지함으로써 **책임 분리(SRP, Single
  Responsibility Principle)를 철저히 적용**할 수 있다.
- 이로 인해 **테스트 코드 작성이 쉬워지고**, 도메인 모델을 독립적으로 검증할 수 있다.

#### 5. **비동기 및 메시지 기반 확장성을 고려**

- 비즈니스 로직을 **Application Layer에서 관리하면서**, 비동기 처리가 필요한 작업은 **Infrastructure Layer에서 메시징 시스템(RabbitMQ)과 연계**하도록 설계할 수 있다.
- 이렇게 하면 특정 기능(예: AI 영상 생성)이 처리되는 동안 **메인 애플리케이션의 응답 성능을 유지**하면서도 확장성을 확보할 수 있다.

---

### **결론**

스토블릭스는 **AI 기반 영상 생성 및 추천 시스템을 포함하는 확장 가능한 서비스**로 성장해야 하므로,

- **도메인 중심 설계(DDD)를 기반으로 계층을 나누어 유지보수성과 확장성을 확보**
- **Adapter Layer를 통해 외부 시스템과의 결합도를 낮추고, 메시징 시스템을 활용해 비동기 처리 지원**
- **비즈니스 로직과 인프라 코드를 분리하여 테스트 가능성과 가독성을 높임**

이러한 이유로 **계층형 아키텍처 + 도메인 중심 설계**를 선택하였습니다.

---

## 12. 성능 최적화 및 모니터링

### 캐시 및 데이터 처리

| 전략           | 구현 방식                          | 적용 대상     |
|--------------|--------------------------------|-----------|
| Lazy Loading | `FetchType.LAZY` 설정            | 사용자-문구 관계 |
| Cache-Aside  | Redis `@Cacheable` + TTL(1시간)  | 인기 문구 조회  |
| Batch Insert | `hibernate.jdbc.batch_size=50` | 대량 댓글 입력  |

### 모니터링 도구 사용

- **Prometheus:** 서버 및 애플리케이션 메트릭 수집
- **Grafana:** 대시보드 시각화 및 경고 설정
- **Health Checks:** `/actuator/health`, Liveness/Readiness Probe 설정

---

## 13. 테스트 및 품질 관리

### 테스트 피라미드 및 커버리지 목표

```plaintext
        [E2E] 10%
      /       \
  [통합] 20%   \
  /             \
[단위] 70% ──────┘
```

- **목표:** 코드 커버리지 85% 이상
- **부하 테스트:** Gatling으로 TPS 및 응답 시간 측정
- **보안 취약점 테스트:** OWASP ZAP 적용

---

## 14. 배포 및 운영 전략

### Koyeb 배포 단계

1. GitHub 리포지토리 연결
2. Health Check: `/actuator/health` 및 Probe 설정
3. 자동 스케일링: CPU 75% 초과 시 +2 인스턴스
4. **롤백 전략:** 최신 안정 버전으로 즉시 롤백 지원

---

## 14. 개발자 노트

- **도전 과제:** AI API 통합 시 타임아웃 최소화
- **배운 점:** 헥사고날 아키텍처의 유지보수성 및 확장성 확보
- **추후 계획:** 마이크로서비스 전환 및 기능 확장

---

## 15. 라이선스

이 프로젝트는 개인 포트폴리오 목적으로만 사용 가능하며, 상업적 이용은 금지됩니다.  
CC BY-NC-ND 4.0 라이선스 적용  
© 2025 Stoblyx. All rights reserved.
