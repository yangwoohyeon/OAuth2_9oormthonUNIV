# Java 17이 필요하면 다음처럼 변경
FROM openjdk:21-jdk-slim

# JAR 파일 이름을 인자로 받음 (선택사항)
ARG JAR_FILE=build/libs/OAuth2_9oormthonUNIV-0.0.1-SNAPSHOT.jar

# 컨테이너 내부에 app.jar로 복사
COPY ${JAR_FILE} app.jar

# 8080 포트 개방
EXPOSE 8080

# Spring Boot 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
