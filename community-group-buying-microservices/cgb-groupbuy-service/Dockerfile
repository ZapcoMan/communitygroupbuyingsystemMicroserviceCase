FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY cgb-common/pom.xml cgb-common/
COPY cgb-groupbuy-service/pom.xml cgb-groupbuy-service/
RUN mvn -B dependency:go-offline -pl cgb-common,cgb-groupbuy-service -am
COPY cgb-common/ cgb-common/
COPY cgb-groupbuy-service/ cgb-groupbuy-service/
RUN mvn -B package -pl cgb-groupbuy-service -am -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/cgb-groupbuy-service/target/*.jar app.jar
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "app.jar"]
