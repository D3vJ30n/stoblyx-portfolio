FROM eclipse-temurin:17-jdk
WORKDIR /app

# 모든 소스 파일 복사
COPY . .
RUN chmod +x ./gradlew

# 필요한 디렉토리 생성
RUN mkdir -p build/generated-snippets

# asciidoctor 태스크를 건너뛰고 bootJar만 실행
RUN ./gradlew bootJar -x asciidoctor -x test -x check --stacktrace --no-daemon

EXPOSE 8080
ENV SPRING_PROFILES_ACTIVE=mysql
ENV JAVA_OPTS="-Xmx400m -Xms200m -Dserver.tomcat.mbeanregistry.enabled=true -Dspring.liveBeansView.mbeanDomain=stoblyx -Dspring.boot.admin.client.instance.prefer-ip=true"
ENV SPRING_DATASOURCE_URL=jdbc:mysql://mysql-domyeongjeon.alwaysdata.net:3306/domyeongjeon_stoblyx_sandbox_db?useSSL=false&serverTimezone=UTC
ENV SPRING_DATASOURCE_USERNAME=404306
ENV SPRING_DATASOURCE_PASSWORD=ehaud_0112
ENV SPRING_DATASOURCE_DRIVER_CLASS_NAME=com.mysql.cj.jdbc.Driver
ENV LOGGING_LEVEL_ROOT=INFO
ENV LOGGING_LEVEL_ORG_SPRINGFRAMEWORK=WARN
ENV LOGGING_LEVEL_COM_J30N_STOBLYX=DEBUG
ENV SERVER_SHUTDOWN=graceful
ENV MANAGEMENT_ENDPOINT_HEALTH_SHOW_DETAILS=always
ENV MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE=health,info

# 명시적 서버 포트 설정
ENV SERVER_PORT=8080
# 액추에이터 설정 추가
ENV MANAGEMENT_SERVER_PORT=8080
ENV MANAGEMENT_ENDPOINTS_WEB_BASE_PATH=/actuator
# 애플리케이션 준비 상태 지연 설정
ENV SPRING_LIFECYCLE_TIMEOUT_PER_SHUTDOWN_PHASE=20s

# 헬스체크 설정 (30초 대기 후 5초마다 체크)
HEALTHCHECK --interval=5s --timeout=3s --start-period=30s --retries=3 CMD ["sh", "-c", "wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1"]

CMD ["java", "-XX:+AlwaysPreTouch", "-Dspring.jpa.open-in-view=false", "-jar", "build/libs/stoblyx-portfolio-0.0.1-SNAPSHOT.jar"] 