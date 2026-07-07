# ============================================================
#  Contexto de build = RAIZ del repo (multi-modulo). Ejecutar desde la raiz:
#    docker build -f SoftCorp-MicroServiceGateway/Dockerfile -t softcorp/gateway .
# ============================================================
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY . .
RUN mvn -B -pl SoftCorp-MicroServiceGateway -am clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine AS runtime
WORKDIR /app
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
COPY --from=build /workspace/SoftCorp-MicroServiceGateway/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
