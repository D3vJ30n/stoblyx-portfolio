package com.j30n.stoblyx.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Gauge;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.health.Status;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 알림 설정 클래스
 */
@Slf4j
@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class AlertConfig {

    private final HealthEndpoint healthEndpoint;
    private final MeterRegistry meterRegistry;
    private final RestTemplate restTemplate = new RestTemplate();
    
    private final AtomicInteger healthStatus = new AtomicInteger(1);
    private final AtomicInteger memoryAlert = new AtomicInteger(0);
    
    @Value("${monitoring.memory.threshold:80}")
    private int memoryThreshold;
    
    @Value("${monitoring.alert.enabled:true}")
    private boolean alertEnabled;
    
    @Value("${monitoring.alert.slack.webhook:}")
    private String slackWebhookUrl;
    
    @Value("${monitoring.alert.slack.channel:#alerts}")
    private String slackChannel;
    
    @Value("${spring.application.name:stoblyx}")
    private String applicationName;
    
    @Value("${spring.profiles.active:development}")
    private String activeProfile;
    
    private String hostName;
    private String hostAddress;
    
    private static final String SLACK_COLOR_KEY = "color";
    
    /**
     * 초기화 메서드
     * 메트릭 게이지 등록
     */
    @PostConstruct
    public void init() {
        // 호스트 정보 초기화
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            hostName = localHost.getHostName();
            hostAddress = localHost.getHostAddress();
        } catch (Exception e) {
            hostName = "unknown";
            hostAddress = "unknown";
            log.warn("호스트 정보를 가져오는 중 오류 발생", e);
        }
        
        // 헬스 상태 게이지 등록
        Gauge.builder("system.health", healthStatus, AtomicInteger::get)
                .description("시스템 헬스 상태 (1: UP, 0: DOWN)")
                .register(meterRegistry);
        
        // 메모리 경고 게이지 등록
        Gauge.builder("system.memory.alert", memoryAlert, AtomicInteger::get)
                .description("메모리 사용량 경고 (1: 경고, 0: 정상)")
                .register(meterRegistry);
        
        log.info("모니터링 알림 시스템 초기화 완료 (메모리 임계값: {}%, 알림 활성화: {}, 호스트: {})", 
                memoryThreshold, alertEnabled, hostName);
        
        // 시스템 시작 알림
        sendAlert("시스템 시작", String.format("애플리케이션 %s이(가) %s 환경에서 시작되었습니다.", 
                applicationName, activeProfile));
    }
    
    /**
     * 헬스 체크 스케줄러
     * 1분마다 실행
     */
    @Scheduled(fixedRate = 60000)
    public void checkHealth() {
        // 테스트 환경에서는 헬스 체크 무시
        if ("test".equals(activeProfile)) {
            log.debug("테스트 환경에서는 헬스 체크를 무시합니다.");
            healthStatus.set(1); // UP 상태로 설정
            return;
        }
        
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
                sendAlert("🔴 시스템 다운", String.format("시스템 상태가 DOWN으로 변경되었습니다.%n상태: %s%n호스트: %s (%s)", 
                        status.getCode(), hostName, hostAddress));
            } else if (!wasUp && isUp) {
                // DOWN -> UP 변경
                log.info("시스템 헬스 체크 복구: {}", status.getCode());
                sendAlert("🟢 시스템 복구", String.format("시스템 상태가 UP으로 복구되었습니다.%n호스트: %s (%s)", 
                        hostName, hostAddress));
            }
        } catch (Exception e) {
            log.error("헬스 체크 중 오류 발생", e);
            healthStatus.set(0);
            sendAlert("🔴 헬스 체크 오류", String.format("헬스 체크 중 오류가 발생했습니다.%n오류: %s%n호스트: %s (%s)", 
                    e.getMessage(), hostName, hostAddress));
        }
    }
    
    /**
     * 메모리 사용량 체크 스케줄러
     * 5분마다 실행
     */
    @Scheduled(fixedRate = 300000)
    public void checkMemoryUsage() {
        // 테스트 환경에서는 메모리 체크 무시
        if ("test".equals(activeProfile)) {
            log.debug("테스트 환경에서는 메모리 체크를 무시합니다.");
            memoryAlert.set(0); // 정상 상태로 설정
            return;
        }
        
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
                sendAlert("🟠 메모리 사용량 경고", String.format("""
                        메모리 사용량이 임계값을 초과했습니다.%n
                        사용량: %d%% (임계값: %d%%)
                        사용 중: %d MB / 최대: %d MB
                        호스트: %s (%s)""", 
                        usagePercentage, memoryThreshold, 
                        used / (1024 * 1024), max / (1024 * 1024),
                        hostName, hostAddress));
            } else if (wasAlert && !isAlert) {
                // 경고 -> 정상 변경
                log.info("메모리 사용량 정상: {}% (임계값: {}%)", usagePercentage, memoryThreshold);
                sendAlert("🟢 메모리 사용량 정상", String.format("메모리 사용량이 정상 수준으로 돌아왔습니다.%n" +
                        "사용량: %d%% (임계값: %d%%)\n" +
                        "호스트: %s (%s)", 
                        usagePercentage, memoryThreshold, hostName, hostAddress));
            }
            
            // 메모리 사용량 로깅 (10분마다)
            if (System.currentTimeMillis() % 600000 < 300000) {
                log.info("메모리 사용량: {}% ({}MB / {}MB)", 
                        usagePercentage, used / (1024 * 1024), max / (1024 * 1024));
            }
        } catch (Exception e) {
            log.error("메모리 사용량 체크 중 오류 발생", e);
        }
    }
    
    /**
     * 알림 전송 메서드
     * 
     * @param title 알림 제목
     * @param message 알림 메시지
     */
    private void sendAlert(String title, String message) {
        // 알림이 비활성화된 경우 로그만 남김
        if (!alertEnabled) {
            log.info("[알림 비활성화] {}: {}", title, message);
            return;
        }
        
        // 로그 기록
        log.info("[알림] {}: {}", title, message);
        
        // Slack 알림 전송
        sendSlackAlert(title, message);
    }
    
    /**
     * Slack 알림 전송 메서드
     * 
     * @param title 알림 제목
     * @param message 알림 메시지
     */
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
                attachment.put(SLACK_COLOR_KEY, "danger"); // 빨간색
            } else if (title.contains("경고")) {
                attachment.put(SLACK_COLOR_KEY, "warning"); // 노란색
            } else if (title.contains("복구") || title.contains("정상")) {
                attachment.put(SLACK_COLOR_KEY, "good"); // 초록색
            } else {
                attachment.put(SLACK_COLOR_KEY, "#0099ff"); // 파란색 (정보)
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
} 