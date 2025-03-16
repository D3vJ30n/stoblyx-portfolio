FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY . .
RUN chmod +x ./gradlew && ./gradlew build
EXPOSE 8080
ENV SPRING_PROFILES_ACTIVE=mysql
ENV JAVA_OPTS="-Xmx300m -Xms128m"
ENV SPRING_DATASOURCE_URL=jdbc:mysql://mysql-domyeongjeon.alwaysdata.net:3306/domyeongjeon_stoblyx_sandbox_db?useSSL=false&serverTimezone=UTC
ENV SPRING_DATASOURCE_USERNAME=404306
ENV SPRING_DATASOURCE_PASSWORD=ehaud_0112
ENV SPRING_DATASOURCE_DRIVER_CLASS_NAME=com.mysql.cj.jdbc.Driver
CMD ["java", "-jar", "build/libs/stoblyx-portfolio-0.0.1-SNAPSHOT.jar"] 