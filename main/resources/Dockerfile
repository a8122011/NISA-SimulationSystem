FROM maven:3-eclipse-temurin-21 AS build
WORKDIR /app          # 作業ディレクトリを /app に
COPY . /app　　　　　　# 今のフォルダのファイルを /app にコピー
RUN mvn clean package -Dmaven.test.skip=true

FROM eclipse-temurin:21-alpine
WORKDIR /app
COPY --from=build /app/target/simulateAssetFormationWithNISA-0.0.1-SNAPSHOT.jar demo.jar
EXPOSE 8888
ENTRYPOINT ["java","-jar","demo.jar"]
