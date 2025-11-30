FROM maven:3-openjdk-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -Dmaven.test.skip=true
FROM eclipse-temurin:21-jre-alpine
COPY --from=build /target/simulateAssetFormationWithNISA-0.0.1-SNAPSHOT.jar /app/demo.jar
EXPOSE 8888
ENTRYPOINT ["java","-jar", "/app/demo.jar"]
