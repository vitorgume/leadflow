# syntax=docker/dockerfile:1
# ====== BUILD ======
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /workspace

# Controle de testes:
# RUN_TESTS=true  -> roda mvn verify (executa testes)
# RUN_TESTS=false -> mvn -DskipTests package (compila testes, não executa)
# RUN_IT=true/false -> só vale quando RUN_TESTS=true (controla ITs via Failsafe)
ARG RUN_TESTS=false
ARG RUN_IT=true

# Baixa dependências e plugins em cache (sem compilar)
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -q -DskipTests dependency:go-offline

COPY src ./src
RUN --mount=type=cache,target=/root/.m2 \
    if [ "$RUN_TESTS" = "true" ]; then \
      if [ "$RUN_IT" = "true" ]; then \
        mvn -B verify; \
      else \
        mvn -B -DskipITs verify; \
      fi; \
    else \
      mvn -B -DskipTests package; \
    fi

# ====== RUNTIME ======
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

ENV JAVA_OPTS="-XX:+UseG1GC -XX:MaxRAMPercentage=75 -XX:+HeapDumpOnOutOfMemoryError -Djava.security.egd=file:/dev/./urandom"
COPY --from=build /workspace/target/*.jar /app/app.jar
EXPOSE 8081
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]
