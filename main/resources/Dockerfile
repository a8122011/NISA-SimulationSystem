FROM maven:3-openjdk-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -Dmaven.test.skip=true

# ベースイメージとしてOpenJDK 17のslimバージョンを使用
FROM openjdk:21-jdk-slim

# 作成したアプリケーションのJarファイルをイメージ内にコピー
COPY --from=build /target/simulateAssetFormationWithNISA-0.0.1-SNAPSHOT.jar /app.demo.jar

EXPOSE 8888

# アプリケーションを実行
ENTRYPOINT ["java", "-jar", "/app.demo.jar"]
