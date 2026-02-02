# 멀티 스테이지 빌드를 사용하여 최종 이미지 크기 최적화

# Stage 1: Maven을 사용한 빌드
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# pom.xml과 소스 코드 복사
COPY pom.xml .
COPY src ./src

# Maven 빌드 실행 (의존성 다운로드 및 패키징)
RUN mvn clean package -DskipTests

# Stage 2: 실행 환경
FROM eclipse-temurin:17-jre

WORKDIR /app

# 빌드된 JAR 파일 복사
COPY --from=build /app/target/*.jar app.jar

# 포트 노출 (application.yml의 server.port와 일치)
EXPOSE 8085

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
