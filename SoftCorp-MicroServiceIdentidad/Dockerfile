# ============================================================
#  IMPORTANTE: el contexto de build es la RAIZ del repo (multi-modulo).
#  Construir asi desde la carpeta raiz del proyecto:
#    docker build -f SoftCorp-MicroServiceIdentidad/Dockerfile -t softcorp/identidad .
# ============================================================
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY . .
RUN mvn -B -pl SoftCorp-MicroServiceIdentidad -am clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine AS runtime
WORKDIR /app
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
COPY --from=build /workspace/SoftCorp-MicroServiceIdentidad/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
