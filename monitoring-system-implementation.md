# 모니터링 시스템 구축

## 1. 개요

애플리케이션의 안정적인 운영을 위해 모니터링 시스템을 구축했습니다. 이 시스템은 다음과 같은 기능을 제공합니다:

1. **Health Check 엔드포인트**: 시스템 상태 확인을 위한 API 제공
2. **메트릭 수집**: 애플리케이션 성능 및 상태 지표 수집
3. **알림 시스템**: 문제 발생 시 Slack 알림 제공

## 2. Health Check 엔드포인트 구현

### 2.1 기본 Health Check API

```java
@GetMapping
public ResponseEntity<ApiResponse<Map<String, Object>>> healthCheck() {
    try {
        Map<String, Object> healthData = new HashMap<>();
        healthData.put("status", "UP");
        healthData.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "시스템이 정상 작동 중입니다.", healthData));
    } catch (Exception e) {
        log.error("헬스 체크 중 오류 발생", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>("ERROR", "시스템 상태 확인 중 오류가 발생했습니다.", null));
    }
}
```

### 2.2 상세 Health Check API

```java
@GetMapping("/details")
public ResponseEntity<ApiResponse<Map<String, Object>>> detailedHealthCheck() {
    try {
        HealthComponent health = healthEndpoint.health();
        Status status = health.getStatus();

        Map<String, Object> healthData = new HashMap<>();
        healthData.put("status", status.getCode());
        healthData.put("timestamp", System.currentTimeMillis());
        healthData.put("details", health);

        boolean isUp = Status.UP.equals(status);
        HttpStatus httpStatus = isUp ? HttpStatus.OK : HttpStatus.SERVICE_UNAVAILABLE;
        String message = isUp ? "시스템이 정상 작동 중입니다." : "시스템에 문제가 발생했습니다.";

        return ResponseEntity.status(httpStatus)
                .body(new ApiResponse<>(isUp ? "SUCCESS" : "ERROR", message, healthData));
    } catch (Exception e) {
        log.error("상세 헬스 체크 중 오류 발생", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>("ERROR", "시스템 상태 확인 중 오류가 발생했습니다.", null));
    }
}
```

## 3. 메트릭 수집 구현

### 3.1 API 요청 모니터링

API 요청의 처리 시간을 측정하고 메트릭으로 기록하는 인터셉터를 구현했습니다.

```java
@Override
public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
    Long startTime = (Long) request.getAttribute(START_TIME_ATTRIBUTE);
    if (startTime != null) {
        long duration = System.currentTimeMillis() - startTime;
        String apiName = getApiName(request);

        // 메트릭 기록
        monitoringConfig.recordApiTiming(meterRegistry, apiName, duration);

        // 응답 시간이 1초를 초과하는 경우 로그 기록
        if (duration > 1000) {
            log.warn("Slow API call: {} took {}ms", apiName, duration);
        }
    }
}
```

### 3.2 시스템 메트릭 수집

JVM 메모리, GC, 프로세서 사용량 등의 시스템 메트릭을 수집합니다.

```java
@Bean
public MeterRegistryCustomizer<MeterRegistry> metricsCommonTags(Environment env) {
    return registry -> {
        // 애플리케이션 이름과 환경 정보를 태그로 추가
        registry.config()
                .commonTags("application", env.getProperty("spring.application.name", "stoblyx"))
                .commonTags("environment", env.getActiveProfiles().length > 0 ? env.getActiveProfiles()[0] : "default");

        // JVM 메모리 메트릭 등록
        new JvmMemoryMetrics().bindTo(registry);

        // JVM GC 메트릭 등록
        new JvmGcMetrics().bindTo(registry);

        // 프로세서 메트릭 등록
        new ProcessorMetrics().bindTo(registry);

        // 업타임 메트릭 등록
        new UptimeMetrics().bindTo(registry);
    };
}
```

## 4. 알림 시스템 구현

### 4.1 헬스 체크 알림

시스템 상태가 변경될 때 알림을 발송합니다.

```java
@Scheduled(fixedRate = 60000)
public void checkHealth() {
    try {
        Status status = healthEndpoint.health().getStatus();
        boolean isUp = Status.UP.equals(status);

        // 이전 상태와 현재 상태 비교
        boolean wasUp = healthStatus.get() == 1;

        // 상태 업데이트
        healthStatus.set(isUp ? 1 : 0);

        // 상태가 변경된 경우에만 알림 발송
        if (wasUp && !isUp) {
            // UP -> DOWN 변경
            log.error("시스템 헬스 체크 실패: {}", status.getCode());
            sendAlert("🔴 시스템 다운", String.format("시스템 상태가 DOWN으로 변경되었습니다.\n상태: %s\n호스트: %s (%s)",
                    status.getCode(), hostName, hostAddress));
        } else if (!wasUp && isUp) {
            // DOWN -> UP 변경
            log.info("시스템 헬스 체크 복구: {}", status.getCode());
            sendAlert("🟢 시스템 복구", String.format("시스템 상태가 UP으로 복구되었습니다.\n호스트: %s (%s)",
                    hostName, hostAddress));
        }
    } catch (Exception e) {
        log.error("헬스 체크 중 오류 발생", e);
        healthStatus.set(0);
        sendAlert("🔴 헬스 체크 오류", String.format("헬스 체크 중 오류가 발생했습니다.\n오류: %s\n호스트: %s (%s)",
                e.getMessage(), hostName, hostAddress));
    }
}
```

### 4.2 메모리 사용량 알림

메모리 사용량이 임계값을 초과하거나 정상으로 돌아올 때 알림을 발송합니다.

```java
@Scheduled(fixedRate = 300000)
public void checkMemoryUsage() {
    try {
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapMemoryUsage = memoryBean.getHeapMemoryUsage();

        long used = heapMemoryUsage.getUsed();
        long max = heapMemoryUsage.getMax();

        // 메모리 사용률 계산 (%)
        int usagePercentage = (int) ((used * 100) / max);

        // 이전 상태와 현재 상태 비교
        boolean wasAlert = memoryAlert.get() == 1;
        boolean isAlert = usagePercentage > memoryThreshold;

        // 상태 업데이트
        memoryAlert.set(isAlert ? 1 : 0);

        // 상태가 변경된 경우에만 알림 발송
        if (!wasAlert && isAlert) {
            // 정상 -> 경고 변경
            log.warn("메모리 사용량 경고: {}% (임계값: {}%)", usagePercentage, memoryThreshold);
            sendAlert("🟠 메모리 사용량 경고", String.format("메모리 사용량이 임계값을 초과했습니다.\n" +
                    "사용량: %d%% (임계값: %d%%)\n" +
                    "사용 중: %d MB / 최대: %d MB\n" +
                    "호스트: %s (%s)",
                    usagePercentage, memoryThreshold,
                    used / (1024 * 1024), max / (1024 * 1024),
                    hostName, hostAddress));
        } else if (wasAlert && !isAlert) {
            // 경고 -> 정상 변경
            log.info("메모리 사용량 정상: {}% (임계값: {}%)", usagePercentage, memoryThreshold);
            sendAlert("🟢 메모리 사용량 정상", String.format("메모리 사용량이 정상 수준으로 돌아왔습니다.\n" +
                    "사용량: %d%% (임계값: %d%%)\n" +
                    "호스트: %s (%s)",
                    usagePercentage, memoryThreshold, hostName, hostAddress));
        }
    } catch (Exception e) {
        log.error("메모리 사용량 체크 중 오류 발생", e);
    }
}
```

### 4.3 Slack 알림 구현

Slack 웹훅을 사용하여 알림을 발송합니다.

```java
private void sendSlackAlert(String title, String message) {
    // Slack 웹훅 URL이 설정되지 않은 경우 무시
    if (slackWebhookUrl == null || slackWebhookUrl.isEmpty()) {
        return;
    }

    try {
        // 현재 시간
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        // Slack 메시지 포맷
        Map<String, Object> slackMessage = new HashMap<>();
        slackMessage.put("channel", slackChannel);
        slackMessage.put("username", applicationName + " 모니터링");
        slackMessage.put("icon_emoji", ":robot_face:");

        // 첨부 파일 (attachment) 형식으로 메시지 구성
        Map<String, Object> attachment = new HashMap<>();
        attachment.put("title", title);
        attachment.put("text", message);
        attachment.put("footer", String.format("%s (%s 환경)", timestamp, activeProfile));

        // 알림 유형에 따른 색상 설정
        if (title.contains("다운") || title.contains("오류")) {
            attachment.put("color", "danger"); // 빨간색
        } else if (title.contains("경고")) {
            attachment.put("color", "warning"); // 노란색
        } else if (title.contains("복구") || title.contains("정상")) {
            attachment.put("color", "good"); // 초록색
        } else {
            attachment.put("color", "#0099ff"); // 파란색 (정보)
        }

        slackMessage.put("attachments", new Object[]{attachment});

        // HTTP 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // HTTP 요청 엔티티 생성
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(slackMessage, headers);

        // Slack 웹훅 URL로 POST 요청 전송
        restTemplate.postForEntity(slackWebhookUrl, entity, String.class);
    } catch (Exception e) {
        log.error("Slack 알림 전송 중 오류 발생", e);
    }
}
```

## 5. 설정

### 5.1 application.yml 설정

```yaml
# 모니터링 설정
monitoring:
  memory:
    threshold: ${MEMORY_THRESHOLD:80} # 메모리 사용량 경계값 (%)
  alert:
    enabled: ${ALERT_ENABLED:true} # 알림 활성화 여부
    slack:
      webhook: ${SLACK_WEBHOOK_URL:} # Slack 웹훅 URL
      channel: ${SLACK_CHANNEL:#alerts} # Slack 채널

# 액추에이터 설정
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus,env,loggers
      base-path: /api/actuator
  endpoint:
    health:
      show-details: always
    metrics:
      enabled: true
    prometheus:
      enabled: true
  prometheus:
    metrics:
      export:
        enabled: true
  metrics:
    distribution:
      percentiles-histogram:
        http.server.requests: true
      percentiles:
        http.server.requests: 0.5, 0.95, 0.99
      sla:
        http.server.requests: 50ms, 100ms, 200ms, 500ms
```

## 6. Slack 알림 설정 방법

### 6.1 Slack 웹훅 URL 생성

1. Slack 워크스페이스에서 새 앱 생성

   - https://api.slack.com/apps 접속
   - "Create New App" 클릭
   - "From scratch" 선택
   - 앱 이름과 워크스페이스 선택 후 "Create App" 클릭

2. 웹훅 활성화

   - 왼쪽 메뉴에서 "Incoming Webhooks" 선택
   - "Activate Incoming Webhooks" 토글 활성화
   - "Add New Webhook to Workspace" 클릭
   - 알림을 받을 채널 선택 후 "Allow" 클릭

3. 웹훅 URL 복사
   - 생성된 웹훅 URL 복사

### 6.2 환경 변수 설정

다음 환경 변수를 설정하여 Slack 알림을 활성화할 수 있습니다:

```
SLACK_WEBHOOK_URL=https://hooks.slack.com/services/TXXXXXXXX/BXXXXXXXX/XXXXXXXXXXXXXXXXXXXXXXXX
SLACK_CHANNEL=#alerts
ALERT_ENABLED=true
MEMORY_THRESHOLD=80
```

## 7. 알림 예시

### 7.1 시스템 시작 알림

![시스템 시작 알림](https://example.com/images/system-start-alert.png)

### 7.2 메모리 사용량 경고 알림

![메모리 사용량 경고 알림](https://example.com/images/memory-warning-alert.png)

### 7.3 시스템 다운 알림

![시스템 다운 알림](https://example.com/images/system-down-alert.png)

### 7.2 Actuator 엔드포인트 접근

```
GET /health
GET /metrics
GET /prometheus
```

## 8. 확장 방안

현재 구현된 모니터링 시스템은 기본적인 기능을 제공하며, 다음과 같은 방향으로 확장할 수 있습니다.

1. **외부 모니터링 도구 연동**

   - Prometheus + Grafana: 메트릭 수집 및 시각화
   - ELK Stack: 로그 수집 및 분석
   - Datadog, New Relic 등의 상용 APM 도구 연동

2. **알림 채널 다양화**

   - 이메일 알림
   - Microsoft Teams 알림
   - SMS 알림

3. **분산 추적 시스템 도입**

   - Spring Cloud Sleuth + Zipkin
   - OpenTelemetry

4. **커스텀 대시보드 개발**
   - 주요 비즈니스 지표 모니터링
   - 사용자 경험 모니터링 (페이지 로드 시간, 오류율 등)
