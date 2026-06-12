FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY cgb-common/pom.xml cgb-common/
COPY cgb-order-service/pom.xml cgb-order-service/
RUN mvn -B dependency:go-offline -pl cgb-common,cgb-order-service -am
COPY cgb-common/ cgb-common/
COPY cgb-order-service/ cgb-order-service/
RUN mvn -B package -pl cgb-order-service -am -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/cgb-order-service/target/*.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]
