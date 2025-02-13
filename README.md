## Booksum - AI 기반 독서 커뮤니티 플랫폼

"기억에 남는 글귀, 당신의 인사이트를 공유하세요!"  
Booksum은 단순한 독서 기록을 넘어, 책을 매개로 사람과 사람을 연결하는 AI 기반 독서 커뮤니티를 제공합니다.  
사용자는 기억에 남는 글귀나 인용문을 검색하고, AI가 추천하는 관련 영상을 시청하며, 다른 독자와 독서 경험을 공유할 수 있습니다.

---

### 1. 프로젝트 개요

**프로젝트명:** Booksum (북썸)

**개발 기간:** 1개월 (2025년 2월 ~ 2025년 3월)

**주요 기술 스택**

- Backend: Spring Boot 3.4.2, Java 17
- Database: MySQL 8.0
- Cache: Redis 7.0
- Security: JWT, Spring Security
- AI Integration: OpenAI API
- Deployment: Koyeb

**아키텍처:** 헥사고날 아키텍처 (Hexagonal Architecture / Ports and Adapters)

### 아키텍처 원칙

1. **도메인 중심 설계**

    - 비즈니스 로직을 순수한 도메인 계층에 격리
    - 도메인 모델은 외부 의존성 없이 독립적으로 동작

2. **포트와 어댑터 분리**

    - 인바운드 포트: 애플리케이션 진입점 (REST API, 이벤트 핸들러)
    - 아웃바운드 포트: 외부 시스템 연동점 (데이터베이스, 외부 API)
    - 어댑터: 포트 인터페이스의 실제 구현체

### 아키텍처 다이어그램

![architecture](docs/images/architecture_V4.png)

### 시스템 흐름도

![flowchart](docs/images/sequenceDiagram.png)

---

### 2. Booksum만의 차별점

1. **AI 기반 지능형 검색 및 추천**

    - 자연어 처리 기반의 문맥 이해 검색
    - 사용자 독서 패턴 기반 개인화 추천
    - 실시간 트렌드 분석 및 인기 콘텐츠 추천

2. **멀티모달 콘텐츠 제공**

    - 텍스트 기반 문구 → 영상 자동 변환
    - AI 기반 감성 분석으로 최적 BGM 매칭
    - 사용자 제작 콘텐츠와 AI 생성 콘텐츠의 조화

3. **인터랙티브 커뮤니티**

    - 실시간 독서 토론 기능
    - 협업 필터링 기반 독자 매칭
    - 게이미피케이션 요소 (독서 업적, 레벨 시스템)

4. **확장 가능한 시스템 설계**
    - 마이크로서비스 전환 고려한 모듈식 설계
    - 이벤트 기반 아키텍처로 기능 확장 용이
    - 캐싱 전략으로 성능 최적화

### 핵심 디자인 패턴

1. **구조 패턴**

    - **Adapter Pattern**
        - 외부 시스템 통합 (도서 API, OpenAI)
        - 레거시 시스템 통합 대비
    - **Facade Pattern**
        - 복잡한 AI 처리 로직 캡슐화
        - 서브시스템 독립성 보장

2. **생성 패턴**

    - **Builder Pattern**
        - 복잡한 객체 생성 과정 추상화
        - DTO, 도메인 객체 생성 표준화
    - **Factory Method Pattern**
        - 객체 생성 로직 캡슐화
        - 도메인 객체 생성 제어

3. **행위 패턴**

    - **Strategy Pattern**
        - 알고리즘 교체 용이성 (추천, 검색)
        - 실행 중 전략 변경 가능
    - **Observer Pattern**
        - 이벤트 기반 상태 변경 처리
        - 실시간 알림 시스템 구현

4. **도메인 패턴**
    - **Aggregate Pattern**
        - 연관 객체 그룹화
        - 트랜잭션 일관성 보장
    - **Repository Pattern**
        - 데이터 접근 계층 추상화
        - 영속성 기술 독립성 확보

### 기술적 특징

1. **성능 최적화**

    - 다층 캐싱 전략 (Local, Redis)
    - 비동기 처리 (CompletableFuture)
    - 데이터베이스 인덱싱 최적화

2. **보안 강화**

    - JWT 기반 인증/인가
    - API 요청 Rate Limiting
    - XSS, CSRF 방어

3. **모니터링 및 운영**
    - 분산 로깅 (ELK Stack)
    - 성능 메트릭 수집 (Prometheus)
    - 실시간 알림 시스템

---

### 3. 주요 기능

#### 회원가입 및 로그인

- **사용자 인증 및 권한 관리**

    - JWT 기반 인증 시스템
    - Access Token과 Refresh Token 분리 운영
    - Redis 기반의 토큰 관리

- **보안 정책**
    - Bcrypt를 통한 비밀번호 암호화
    - API Rate Limiting (Redis 기반)
    - JWT 블랙리스트 관리 (로그아웃, 토큰 무효화)

#### 책 속 문구 검색 & AI 추천

- **검색 시스템**

    - Elasticsearch 기반 전문 검색
    - 문맥 기반 유사도 검색
    - 멀티필드 검색 지원 (제목, 저자, 내용)

- **캐싱 전략**
    - Redis 다층 캐싱
    - 캐시 무효화 정책

#### AI 요약 및 영상 추천 기능

- **비동기 처리 시스템**

- **메시지 큐 기반 처리**
    - RabbitMQ를 통한 비동기 작업 처리
    - 재시도 정책 구현

#### 독서 컬렉션 & 커뮤니티

- **실시간 상호작용**

    - WebSocket 기반 실시간 알림
    - SSE를 통한 실시간 업데이트

- **데이터 일관성**
    - 낙관적 락을 통한 동시성 제어
    - 트랜잭션 관리

#### 이달의 인기 문구 및 이벤트 알림

- **집계 처리**

    - 배치 처리를 통한 통계 집계
    - Redis 기반 실시간 카운터

- **알림 시스템**
    - 이메일, 푸시 알림 통합
    - 알림 설정 개인화

---

### 4. 데이터베이스 설계

#### 설계 원칙

1. **정규화 원칙**

    - 3NF까지 정규화 적용
    - 성능을 고려한 선택적 반정규화

2. **인덱스 전략**

    - 복합 인덱스 활용
    - 커버링 인덱스 구현

3. **파티셔닝 전략**
    - 시간 기반 파티셔닝 (로그, 이력 테이블)
    - 범위 파티셔닝 (대용량 데이터)

### ERD 다이어그램

![erd](docs/images/dbdiagram_V7.png)

### 성능 최적화

1. **쿼리 최적화**

    - 인덱스 활용 최적화
    - N+1 문제 해결을 위한 fetch join 사용

2. **캐시 전략**

    - 엔티티 캐시
    - 쿼리 캐시

3. **트랜잭션 관리**
    - 트랜잭션 격리 수준 최적화
    - 데드락 방지 전략

---

### 5. API 문서

#### RESTful API 설계 원칙

1. **리소스 중심 설계**

    - URI는 리소스를 명사로 표현
    - 계층 구조를 통한 관계 표현
    - 예: `/api/v1/quotes`, `/api/v1/books`

2. **HTTP 메서드의 올바른 사용**

    - GET: 리소스 조회
    - POST: 리소스 생성
    - PUT: 리소스 전체 수정
    - PATCH: 리소스 부분 수정
    - DELETE: 리소스 삭제

3. **상태 코드의 명확한 사용**
    - 200: 성공
    - 201: 리소스 생성 성공
    - 400: 잘못된 요청
    - 401: 인증 실패
    - 403: 권한 없음
    - 404: 리소스 없음
    - 500: 서버 오류

### 공통 사항

#### API 버전 관리

- 모든 API는 `/api/v1`을 기본 경로로 사용
- 버전 변경 시 하위 호환성 유지
- 새로운 버전은 `/api/v2/...` 형식으로 제공

#### 인증

- Bearer Token 방식의 JWT 인증 사용
- Authorization 헤더에 토큰 포함

#### 엔티티 설계

### 주요 엔티티 설계

#### **1. User (사용자)**

사용자는 Booksum 플랫폼을 이용하는 회원을 의미합니다. 이메일과 비밀번호를 사용하여 인증되며, 문구 저장, 좋아요, 댓글 작성 등의 활동을 수행할 수 있습니다.

**주요 속성**

- `id` (UUID, PK) - 사용자 고유 식별자
- `email` (VARCHAR(255), NN) - 사용자 이메일 (고유 값)
- `password` (VARCHAR(255), NN) - 암호화된 비밀번호
- `created_at` (TIMESTAMP) - 계정 생성 날짜 및 시간
- `updated_at` (TIMESTAMP) - 계정 정보 업데이트 날짜 및 시간

---

#### **2. Book (책)**

Book 엔티티는 저장된 문구(Quote)가 속한 책의 정보를 관리합니다.

**주요 속성**

- `id` (UUID, PK) - 책 고유 식별자
- `title` (VARCHAR(255), NN) - 책 제목
- `author` (VARCHAR(255), NN) - 저자
- `genre` (VARCHAR(100)) - 책 장르
- `published_at` (DATE) - 출판일
- `created_at` (TIMESTAMP) - 데이터 생성 날짜 및 시간

---

#### **3. Quote (문구)**

사용자가 저장한 책 속 문구를 관리하는 엔티티입니다. 특정 책(Book)에 속하며, 사용자(User)에 의해 추가됩니다.

**주요 속성**

- `id` (UUID, PK) - 문구 고유 식별자
- `content` (TEXT, NN) - 저장된 문구 내용
- `book_id` (UUID, FK, NN) - 해당 문구가 속한 책의 ID
- `user_id` (UUID, FK, NN) - 문구를 등록한 사용자 ID
- `created_at` (TIMESTAMP) - 문구 등록 날짜 및 시간

---

#### **4. Comment (댓글)**

Comment 엔티티는 특정 문구(Quote)에 대한 사용자 의견을 저장하는 역할을 합니다.

**주요 속성**

- `id` (UUID, PK) - 댓글 고유 식별자
- `quote_id` (UUID, FK, NN) - 해당 댓글이 속한 문구 ID
- `user_id` (UUID, FK, NN) - 댓글을 작성한 사용자 ID
- `content` (TEXT, NN) - 댓글 내용
- `created_at` (TIMESTAMP) - 댓글 작성 날짜 및 시간

---

#### **5. Like (좋아요)**

사용자가 특정 문구(Quote)에 대해 좋아요를 남길 수 있도록 관리하는 엔티티입니다.

**주요 속성**

- `id` (UUID, PK) - 좋아요 고유 식별자
- `quote_id` (UUID, FK, NN) - 좋아요를 누른 문구 ID
- `user_id` (UUID, FK, NN) - 좋아요를 누른 사용자 ID
- `created_at` (TIMESTAMP) - 좋아요 등록 날짜 및 시간

---

#### **6. SavedQuotes (저장한 문구)**

사용자가 관심 있는 문구를 저장할 수 있도록 관리하는 엔티티입니다.

**주요 속성**

- `id` (UUID, PK) - 저장한 문구 고유 식별자
- `user_id` (UUID, FK, NN) - 문구를 저장한 사용자 ID
- `quote_id` (UUID, FK, NN) - 저장된 문구 ID
- `created_at` (TIMESTAMP) - 저장된 날짜 및 시간

---

#### **7. Video (영상)**

문구(Quote)와 관련된 영상 정보를 저장하는 엔티티입니다.

**주요 속성**

- `id` (UUID, PK) - 영상 고유 식별자
- `quote_id` (UUID, FK, NN) - 연관된 문구 ID
- `user_id` (UUID, FK) - 영상을 등록한 사용자 ID (필요 시)
- `video_url` (VARCHAR(255), NN) - 영상 URL
- `description` (TEXT) - 영상 설명
- `created_at` (TIMESTAMP) - 등록 날짜 및 시간

---

#### **8. Summary (요약)**

책(Book)의 내용을 요약한 정보를 저장하는 엔티티입니다.

**주요 속성**

- `id` (UUID, PK) - 요약 고유 식별자
- `book_id` (UUID, FK, NN) - 요약된 책 ID
- `user_id` (UUID, FK) - 요약을 생성한 사용자 ID (필요 시)
- `summary_text` (TEXT, NN) - 요약 내용
- `original_length` (INT, NN) - 원본 텍스트 길이
- `summary_length` (INT, NN) - 요약된 텍스트 길이
- `created_at` (TIMESTAMP) - 요약 등록 날짜 및 시간

---

#### **9. Ranking (랭킹)**

문구(Quote)의 인기도를 측정하기 위한 엔티티입니다.

**주요 속성**

- `id` (UUID, PK) - 랭킹 고유 식별자
- `quote_id` (UUID, FK, NN) - 랭킹을 집계할 문구 ID
- `like_count` (INT, NN) - 해당 문구의 좋아요 수
- `save_count` (INT, NN) - 해당 문구의 저장 횟수
- `comment_count` (INT, NN) - 해당 문구의 댓글 수
- `period` (VARCHAR(20), NN) - 집계 기간 (예: "weekly", "monthly")
- `created_at` (TIMESTAMP) - 데이터 등록 날짜 및 시간

---

### **API 엔드포인트 상세 설명**

---

#### **1. 인증 API**

#### **회원가입**

- **Endpoint**: `POST /api/v1/auth/register`

  사용자가 회원가입을 할 수 있도록 하는 API입니다. 사용자는 `username`, `email`, `password`를 입력하여 계정을 생성할 수 있습니다.
- **요청 (Request)**
    - `username` (문자열, 필수) - 사용자 이름
    - `email` (문자열, 필수) - 사용자 이메일
    - `password` (문자열, 필수) - 사용자 비밀번호
- **응답 (Response)**
    - `id` (UUID) - 생성된 사용자 고유 식별자
    - `username` (문자열) - 사용자 이름
    - `email` (문자열) - 사용자 이메일
    - `created_at` (타임스탬프) - 계정 생성 날짜 및 시간

#### **로그인**

- **Endpoint**: `POST /api/v1/auth/login`

  사용자가 로그인할 수 있도록 하는 API입니다. 로그인 성공 시 JWT 토큰이 반환됩니다.
- **요청 (Request)**
    - `email` (문자열, 필수) - 사용자 이메일
    - `password` (문자열, 필수) - 사용자 비밀번호
- **응답 (Response)**
    - `access_token` (문자열) - 인증된 사용자에게 부여되는 JWT 토큰
    - `expires_in` (정수) - 토큰 만료 시간

---

### **2. 문구(Quote) API**

#### **문구 목록 조회**

- **Endpoint**: `GET /api/v1/quotes`

  전체 문구 목록을 페이징하여 조회하는 API입니다. 사용자는 특정 페이지, 페이지 크기, 정렬 기준을 설정하여 요청할 수 있습니다.
- **요청 (Request)**
    - `page` (정수, 선택) - 페이지 번호 (기본값: 0)
    - `size` (정수, 선택) - 페이지 크기 (기본값: 20)
    - `sort` (문자열, 선택) - 정렬 기준 (예: `createdAt,desc`)
- **응답 (Response)**
    - `quotes` (배열) - 문구 목록
    - `totalElements` (정수) - 전체 문구 개수
    - `totalPages` (정수) - 전체 페이지 수

#### **문구 검색**

- **Endpoint**: `GET /api/v1/quotes/search`

  사용자가 특정 키워드를 입력하여 문구를 검색할 수 있도록 하는 API입니다.
- **요청 (Request)**
    - `keyword` (문자열, 필수) - 검색어
    - `page` (정수, 선택) - 페이지 번호
    - `size` (정수, 선택) - 페이지 크기
- **응답 (Response)**
    - `quotes` (배열) - 검색된 문구 목록
    - `totalElements` (정수) - 검색된 문구 개수

---

### **3. AI 추천 API**

#### **문구 기반 영상 추천**

- **Endpoint**: `GET /api/v1/quotes/{quoteId}/recommendations`

  특정 문구와 관련된 영상을 AI가 추천하여 제공하는 API입니다.
- **요청 (Request)**
    - `quoteId` (UUID, 필수) - 추천할 문구의 ID
- **응답 (Response)**
    - `videos` (배열) - 추천된 영상 목록
    - `video_url` (문자열) - 영상 URL
    - `description` (문자열) - 영상 설명

---

### **4. 커뮤니티 API**

#### **댓글 작성**

- **Endpoint**: `POST /api/v1/quotes/{quoteId}/comments`
  특정 문구에 대해 사용자가 댓글을 작성할 수 있도록 하는 API입니다.
- **요청 (Request)**
    - `quoteId` (UUID, 필수) - 댓글을 작성할 문구의 ID
    - `user_id` (UUID, 필수) - 댓글을 작성한 사용자 ID
    - `content` (문자열, 필수) - 댓글 내용
- **응답 (Response)**
    - `id` (UUID) - 생성된 댓글의 고유 식별자
    - `created_at` (타임스탬프) - 댓글 생성 날짜 및 시간

#### **좋아요 토글**

- **Endpoint**: `POST /api/v1/quotes/{quoteId}/likes`
  특정 문구에 대해 사용자가 좋아요를 누르거나 취소할 수 있도록 하는 API입니다.
- **요청 (Request)**
    - `quoteId` (UUID, 필수) - 좋아요를 누를 문구의 ID
    - `user_id` (UUID, 필수) - 좋아요를 누른 사용자 ID
- **응답 (Response)**
    - `status` (문자열) - "liked" 또는 "unliked" (좋아요 상태)

---

### **API 보안**

#### **1. Rate Limiting (요청 제한)**

    - 특정 시간 내에 동일한 IP 또는 사용자에 대해 과도한 API 요청을 방지하기 위한 제한 정책을 적용합니다.
    - 예: 1분에 100회 요청 제한 (초과 시 `429 Too Many Requests` 응답)

#### **2. 입력 값 검증**

    - 사용자의 입력 값이 올바른 형식인지 검증하여 SQL Injection, XSS 등의 보안 취약점을 방지합니다.
    - 예: 이메일 형식 검증, 비밀번호 길이 제한, 특수문자 허용 여부 설정

#### **3. CORS 설정**

    - 클라이언트에서 API에 접근할 수 있도록 Cross-Origin Resource Sharing(CORS) 정책을 설정합니다.
    - 예: 특정 도메인에서만 API 요청을 허용하거나, 모든 도메인에서 접근 가능하도록 설정 가능 (`Access-Control-Allow-Origin`)

---

### **6. 보안 및 확장성 고려 사항**

---

### **보안 구현**

#### **1. 인증 및 인가**

- Booksum은 **JWT(Json Web Token) 기반 인증 시스템**을 적용하여 사용자 인증 및 권한 관리를 수행합니다.
- 회원가입 및 로그인 시 **Access Token과 Refresh Token을 분리 운영**하며, Refresh Token은 서버 측(예: Redis)에서 관리하여 무단 접근을 방지합니다.
- 역할(Role) 기반 접근 제어(RBAC)를 적용하여 **일반 사용자(USER)와 관리자(ADMIN)의 권한을 구분**합니다.
- 인증이 필요한 API는 `@PreAuthorize` 또는 `@Secured` 애노테이션을 사용하여 보호합니다.

#### **2. JWT 토큰 관리**

- **JWT 서명(Signature) 검증**을 통해 위변조 여부를 확인하고, 유효하지 않은 토큰에 대해서는 `401 Unauthorized` 응답을 반환합니다.
- **Access Token의 유효 기간을 짧게 설정**하고, Refresh Token을 이용해 재발급하는 방식으로 보안성을 강화합니다.
- 사용자가 로그아웃할 경우 **Refresh Token을 무효화하는 기능**을 적용하여 탈취된 토큰의 악용을 방지합니다.
- Redis를 활용하여 **블랙리스트 처리된 JWT 토큰을 저장**하고, 만료된 토큰이 사용되지 않도록 관리합니다.

#### **3. 데이터 암호화**

- 사용자 비밀번호는 **BCrypt 해싱 알고리즘을 적용**하여 안전하게 저장합니다.
- **AES-256 또는 RSA 암호화를 적용**하여 사용자 개인정보(예: 이메일, 프로필 정보)를 보호합니다.
- 클라이언트와 서버 간의 모든 데이터 전송은 **HTTPS를 적용**하여 보안을 강화합니다.

#### **4. XSS(크로스사이트 스크립팅) 방지**

- Booksum은 **사용자 입력값 검증을 강화**하여 악성 스크립트 삽입을 방지합니다.
- XSS 공격을 방지하기 위해, **입력값 필터링과 Escape 처리**를 적용합니다.
- Spring Security의 **Content Security Policy(CSP) 설정**을 통해 불필요한 JavaScript 실행을 차단합니다.

---

### **확장성 설계**

#### **1. 캐시 계층 (Caching Layer)**

- Booksum은 **Redis를 캐싱 레이어로 활용**하여 API 응답 속도를 개선합니다.
- 주요 캐싱 대상은 다음과 같습니다.
    - **인기 문구 목록**: 자주 조회되는 문구 데이터를 캐싱하여 성능을 최적화합니다.
    - **사용자 검색 기록**: 빠른 검색 결과 제공을 위해 최근 검색어를 캐싱합니다.
    - **AI 추천 결과**: 추천 시스템의 연산 부하를 줄이기 위해 결과를 캐싱합니다.
    - **인증 토큰 저장**: Refresh Token을 Redis에서 관리하여 인증 프로세스를 개선합니다.
- **TTL(Time-To-Live) 정책을 적용하여 캐시 데이터를 자동 갱신**하도록 설정합니다.

#### **2. 비동기 처리 (Asynchronous Processing)**

- Booksum은 대용량 트래픽을 처리하기 위해 **비동기 이벤트 기반 아키텍처**를 적용하였습니다.
- Spring의 `@Async` 또는 `CompletableFuture`를 활용하여 **시간이 오래 걸리는 작업을 비동기 처리**합니다.
- 메시지 큐(Message Queue) 시스템(RabbitMQ 또는 Kafka)을 도입하여 **트래픽이 집중되는 기능을 분산 처리**합니다.
- 비동기 처리가 필요한 주요 기능은 다음과 같습니다.
    - **AI 기반 문구 추천**: AI가 분석한 추천 결과를 비동기적으로 생성 및 저장합니다.
    - **이메일 및 푸시 알림 전송**: 사용자 알림 시스템을 비동기 이벤트 기반으로 설계하였습니다.
    - **인기 문구 랭킹 업데이트**: 일정 주기마다 문구 랭킹을 계산하여 캐싱합니다.

---

### 7. 트러블슈팅

ex)

#### 1. JPA N+1 문제 해결

- **문제 상황**: 문구 목록 조회 시 연관된 사용자 정보를 가져오는 과정에서 N+1 쿼리 발생
- **해결 방안**

#### 2. 동시성 이슈

- **문제 상황**: 좋아요 카운트 업데이트 시 동시성 문제 발생
- **해결 방안**

#### 3. 대용량 트래픽 처리

- **문제 상황**: 인기 문구 조회 시 DB 부하 발생
- **해결 방안**

#### 4. 메모리 누수

- **문제 상황**: 이미지 처리 시 메모리 누수 발생
- **해결 방안**

---

### 8. 프로젝트 실행 방법

#### 개발 환경 설정

1. **필수 요구사항**

    - JDK 17
    - MySQL 8.0
    - Redis 7.0
    - Docker (선택사항)

2. **환경변수 설정**

#### 로컬 개발 환경 구성

1. **데이터베이스 설정**

   ```sql
   CREATE DATABASE booksum_data;
   CREATE USER 'booksum'@'localhost' IDENTIFIED BY 'your_password';
   GRANT ALL PRIVILEGES ON booksum_data.* TO 'booksum'@'localhost';
   ```

2. **Redis 설정**

   ```bash
   # Mac OS
   brew install redis
   brew services start redis

   # Ubuntu
   sudo apt-get install redis-server
   sudo systemctl start redis
   ```

#### 애플리케이션 실행

1. **소스코드 클론**

   ```bash
   git clone https://github.com/yourusername/booksum.git
   cd booksum
   ```

2. **프로젝트 빌드**

   ```bash
   ./gradlew clean build
   ```

3. **애플리케이션 실행**
   ```bash
   ./gradlew bootRun
   ```

#### Docker 환경 실행

```bash
# Docker Compose로 실행
docker-compose up -d

# 개별 컨테이너 실행
docker run -d --name mysql -p 3306:3306 -e MYSQL_ROOT_PASSWORD=root mysql:8.0
docker run -d --name redis -p 6379:6379 redis:7.0
docker run -d --name booksum -p 8080:8080 booksum:latest
```

#### 테스트

```bash
# 전체 테스트 실행
./gradlew test

# 특정 테스트 실행
./gradlew test --tests "com.booksum.api.QuoteControllerTest"
```

#### API 문서 확인

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- API 문서: `http://localhost:8080/v3/api-docs`

---

### 9. 프로젝트 구조

#### 헥사고날 아키텍처 기반 패키지 구조

```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── booksum/
│   │           ├── domain/                 # 도메인 계층
│   │           │   ├── model/              # 도메인 모델
│   │           │   │   ├── quote/
│   │           │   │   ├── user/
│   │           │   │   └── comment/
│   │           │   ├── port/              # 포트 인터페이스
│   │           │   │   ├── in/           # 인바운드 포트
│   │           │   │   └── out/          # 아웃바운드 포트
│   │           │   └── service/           # 도메인 서비스
│   │           ├── application/           # 애플리케이션 계층
│   │           │   ├── port/             # 포트 구현체
│   │           │   │   ├── in/
│   │           │   │   └── out/
│   │           │   └── service/           # 유스케이스 구현
│   │           ├── adapter/               # 어댑터 계층
│   │           │   ├── in/               # 인바운드 어댑터
│   │           │   │   ├── web/          # REST API
│   │           │   │   └── event/        # 이벤트 핸들러
│   │           │   └── out/              # 아웃바운드 어댑터
│   │           │       ├── persistence/   # DB 어댑터
│   │           │       └── external/      # 외부 API 어댑터
│   │           └── common/                # 공통 모듈
│   │               ├── config/
│   │               ├── exception/
│   │               └── util/
│   └── resources/
│       ├── application.yml
│       ├── application-local.yml
│       └── application-prod.yml
└── test/
    └── java/
        └── com/
            └── booksum/
                ├── domain/
                ├── application/
                └── adapter/
```

### **주요 컴포넌트 설명**

---

#### **1. 도메인 계층 (Domain Layer)**

- 도메인 계층은 **비즈니스 로직을 담당하는 핵심 계층**으로, 애플리케이션의 도메인 모델과 규칙을 정의합니다.
- **외부 기술과의 의존성을 최소화**하여 비즈니스 로직을 독립적으로 유지하고, 애플리케이션의 확장성을 고려하여 설계하였습니다.
- 주요 구성 요소는 다음과 같습니다.
    - **도메인 모델(Entity, Aggregate)**
        - `User`, `Book`, `Quote`, `Comment` 등의 핵심 엔티티를 정의합니다.
        - 연관된 도메인 객체를 하나의 Aggregate로 묶어, 데이터 일관성을 유지합니다.
    - **도메인 서비스 (Domain Service)**
        - 특정 도메인 모델 간의 복잡한 로직을 캡슐화하여 관리합니다.
    - **도메인 이벤트 (Domain Events)**
        - 특정 도메인 객체에서 발생하는 이벤트를 관리하고, 비동기적으로 처리할 수 있도록 설계하였습니다.

---

#### **2. 포트 인터페이스 (Port Interface)**

- 포트 인터페이스 계층은 **애플리케이션과 외부 시스템 간의 경계를 정의**하며, **헥사고날 아키텍처(Ports & Adapters)** 원칙을 따릅니다.
- 애플리케이션 내부 비즈니스 로직이 **외부 의존성(Database, API 등)과 강하게 결합되지 않도록 설계**하였습니다.
- 주요 구성 요소는 다음과 같습니다.
    - **인바운드 포트 (Inbound Port)**
        - 클라이언트가 애플리케이션에 접근할 수 있도록 API, 이벤트 핸들러 등의 인터페이스를 제공합니다.
        - `UserService`, `QuoteService` 등 **사용자와 직접 상호작용하는 서비스 인터페이스**를 정의합니다.
    - **아웃바운드 포트 (Outbound Port)**
        - 데이터베이스, 외부 API와의 통신을 추상화하여 비즈니스 로직이 특정 기술 스택에 의존하지 않도록 합니다.
        - `UserRepository`, `QuoteRepository` 등을 통해 데이터 저장소와의 상호작용을 정의합니다.
- 이 계층을 통해 **도메인 로직이 외부 환경 변화에 영향을 받지 않고 독립적으로 유지될 수 있도록 보장**합니다.

---

#### **3. 어댑터 구현 (Adapter Implementation)**

- 어댑터 계층은 **포트 인터페이스의 실제 구현을 담당**하며, 외부 시스템과 애플리케이션을 연결하는 역할을 합니다.
- 포트 인터페이스를 구현한 클래스를 통해 **데이터베이스, 외부 API, 메시지 큐 등과의 통합을 수행**합니다.
- 주요 구성 요소는 다음과 같습니다.
    - **인바운드 어댑터 (Inbound Adapter)**
        - 클라이언트 요청을 받아 애플리케이션 내부의 도메인 계층으로 전달합니다.
        - REST API 컨트롤러(`UserController`, `QuoteController` 등)를 포함합니다.
    - **아웃바운드 어댑터 (Outbound Adapter)**
        - 데이터베이스, 외부 API와 직접 통신하는 역할을 수행합니다.
        - `JpaUserRepository`, `JpaQuoteRepository` 등을 통해 JPA를 활용하여 데이터베이스와 연결합니다.
        - OpenAI API, 외부 도서 정보 API 등의 서드파티 API를 호출하는 어댑터를 구현합니다.

---

### 10. 라이선스

Copyright (c) 2025 Booksum.
All rights reserved.

이 프로젝트는 개인 포트폴리오 용도로 제작되었습니다.

---

### 11. 배포 환경

#### 배포 아키텍처

![Deployment Architecture](docs/images/deployment.png)

#### 배포 환경

- **배포 플랫폼**: Koyeb
- **데이터베이스**: MySQL
- **캐시**: Redis

#### 배포 프로세스

1. **GitHub Repository 연동**

- Koyeb 대시보드에서 GitHub Repository 선택
- `main` 브랜치 자동 배포 설정

2. **환경 변수 설정**

   ```bash
   SPRING_PROFILES_ACTIVE=prod
   DB_URL=${KOYEB_MYSQL_URL}
   DB_USERNAME=${KOYEB_MYSQL_USER}
   DB_PASSWORD=${KOYEB_MYSQL_PASSWORD}
   JWT_SECRET=${JWT_SECRET_KEY}
   ```

3. **자동 배포**

- `main` 브랜치에 push 시 자동 빌드 및 배포
- Koyeb 대시보드에서 빌드 상태 모니터링 가능

#### 배포 URL

ex) `https://booksum.koyeb.app`
