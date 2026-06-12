FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY cgb-common/pom.xml cgb-common/
COPY cgb-user-service/pom.xml cgb-user-service/
RUN mvn -B dependency:go-offline -pl cgb-common,cgb-user-service -am
COPY cgb-common/ cgb-common/
COPY cgb-user-service/ cgb-user-service/
RUN mvn -B package -pl cgb-user-service -am -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/cgb-user-service/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
