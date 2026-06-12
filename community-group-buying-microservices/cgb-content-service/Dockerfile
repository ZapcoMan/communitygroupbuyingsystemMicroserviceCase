FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY cgb-common/pom.xml cgb-common/
COPY cgb-content-service/pom.xml cgb-content-service/
RUN mvn -B dependency:go-offline -pl cgb-common,cgb-content-service -am
COPY cgb-common/ cgb-common/
COPY cgb-content-service/ cgb-content-service/
RUN mvn -B package -pl cgb-content-service -am -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/cgb-content-service/target/*.jar app.jar
EXPOSE 8085
ENTRYPOINT ["java", "-jar", "app.jar"]
