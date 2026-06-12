FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY cgb-common/pom.xml cgb-common/
COPY cgb-gateway/pom.xml cgb-gateway/
RUN mvn -B dependency:go-offline -pl cgb-common,cgb-gateway -am
COPY cgb-common/ cgb-common/
COPY cgb-gateway/ cgb-gateway/
RUN mvn -B package -pl cgb-gateway -am -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/cgb-gateway/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
