FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY cgb-common/pom.xml cgb-common/
COPY cgb-groupbuy-service/pom.xml cgb-groupbuy-service/
RUN mvn -B dependency:go-offline -pl cgb-common,cgb-groupbuy-service -am
COPY cgb-common/ cgb-common/
COPY cgb-groupbuy-service/ cgb-groupbuy-service/
RUN mvn -B package -pl cgb-groupbuy-service -am -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
RUN addgroup -S cgb && adduser -S cgb -G cgb
COPY --from=build /app/cgb-groupbuy-service/target/*.jar app.jar
RUN chown cgb:cgb app.jar
USER cgb
EXPOSE 8084
HEALTHCHECK --interval=30s --timeout=10s --retries=3 CMD wget -qO- http://localhost:8084/actuator/health || exit 1
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-XX:+UseG1GC", "-jar", "app.jar"]
