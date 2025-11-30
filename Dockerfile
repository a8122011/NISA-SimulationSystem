# Build stage
FROM maven:3-openjdk-17 AS build

# 作業ディレクトリを作成
WORKDIR /app

# 相対パスでコピー
COPY ./pom.xml .
COPY src ./src

# Maven ビルド
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jdk

# 作業ディレクトリ
WORKDIR /app

# ビルド成果物をコピー
COPY --from=build /app/target/simulateAssetFormationWithNISA-0.0.1-SNAPSHOT.jar demo.jar

EXPOSE 8888

# アプリ起動
ENTRYPOINT ["java", "-jar", "demo.jar"]
