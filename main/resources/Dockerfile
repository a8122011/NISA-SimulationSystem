FROM maven:3-eclipse-temurin-21 AS build
WORKDIR /app
COPY . /app
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-alpine
WORKDIR /app
COPY --from=build /app/target/simulateAssetFormationWithNISA-0.0.1-SNAPSHOT.jar demo.jar
EXPOSE 8888
ENTRYPOINT ["java","-jar", "demo.jar"]
