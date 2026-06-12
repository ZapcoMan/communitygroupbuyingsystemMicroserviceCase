FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY cgb-common/pom.xml cgb-common/
COPY cgb-product-service/pom.xml cgb-product-service/
RUN mvn -B dependency:go-offline -pl cgb-common,cgb-product-service -am
COPY cgb-common/ cgb-common/
COPY cgb-product-service/ cgb-product-service/
RUN mvn -B package -pl cgb-product-service -am -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/cgb-product-service/target/*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]
