FROM eclipse-temurin:17-jre-alpine

WORKDIR /app
EXPOSE 8080
# JAR 파일 복사
COPY app.jar /app/app.jar
# 실행 명령
ENTRYPOINT ["java", "-jar", "/app/app.jar"]