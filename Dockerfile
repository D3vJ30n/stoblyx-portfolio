FROM eclipse-temurin:17-jdk
WORKDIR /app

# 빌드에 필요한 파일만 먼저 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./
RUN chmod +x ./gradlew
RUN ./gradlew --version

# 소스 코드 복사
COPY src src

# 의존성만 먼저 다운로드
RUN ./gradlew dependencies --no-daemon

# 빌드 수행 (테스트 제외)
RUN ./gradlew clean bootJar --no-daemon -x test --info

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

CMD ["java", "-XX:+AlwaysPreTouch", "-Dlogging.config=classpath:logback-spring.xml", "-Dspring.jpa.open-in-view=false", "-jar", "build/libs/stoblyx-portfolio-0.0.1-SNAPSHOT.jar"] 