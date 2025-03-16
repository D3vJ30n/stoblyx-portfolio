FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY . .
RUN chmod +x ./gradlew && ./gradlew build
EXPOSE 8080
ENV SPRING_PROFILES_ACTIVE=mysql
ENV JAVA_OPTS="-Xmx300m -Xms128m"
CMD ["java", "-jar", "build/libs/stoblyx-portfolio-0.0.1-SNAPSHOT.jar"] 