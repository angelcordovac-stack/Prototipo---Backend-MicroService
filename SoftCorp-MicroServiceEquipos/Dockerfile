# ============================================================
#  Contexto de build = RAIZ del repo (multi-modulo). Ejecutar desde la raiz:
#    docker build -f SoftCorp-MicroServiceEquipos/Dockerfile -t softcorp/equipos .
# ============================================================
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY . .
RUN mvn -B -pl SoftCorp-MicroServiceEquipos -am clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine AS runtime
WORKDIR /app
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
COPY --from=build /workspace/SoftCorp-MicroServiceEquipos/target/*.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
