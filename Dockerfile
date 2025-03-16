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

CMD ["java", "-XX:+AlwaysPreTouch", "-Dlogging.config=classpath:logback-spring.xml", "-Dspring.jpa.open-in-view=false", "-jar", "build/libs/stoblyx-portfolio-0.0.1-SNAPSHOT.jar"] 