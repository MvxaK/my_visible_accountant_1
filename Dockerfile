FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app

RUN apt-get update && apt-get install -y \
    tesseract-ocr \
    tesseract-ocr-rus \
    tesseract-ocr-eng \
    && apt-get clean

COPY --from=build /app/target/*.jar app.jar

RUN mkdir -p /tmp/extracter && chmod 777 /tmp/extracter

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]