# Build stage
FROM maven:3-openjdk-17 AS build

# 相対パスでコピー
COPY ./pom.xml ./pom.xml
COPY ./src ./src

# Maven ビルド
RUN mvn clean package -DskipTests

# Runtime stage
FROM openjdk:17-alpine

# ビルド成果物をコピー
COPY --from=build /target/simulateAssetFormationWithNISA-0.0.1-SNAPSHOT.jar /app/demo.jar

WORKDIR /app
EXPOSE 8888
ENTRYPOINT ["java", "-jar", "demo.jar"]
