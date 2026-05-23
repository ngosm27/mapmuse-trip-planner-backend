# syntax=docker/dockerfile:1

############################################
# Build stage
############################################
FROM eclipse-temurin:17-jdk-jammy AS builder

WORKDIR /app

# Copy Maven wrapper and config first
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Make mvnw executable
RUN chmod +x mvnw

# Cache dependencies
RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw dependency:go-offline -DskipTests

# Copy source
COPY src ./src

# Build app
RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw clean package -DskipTests

############################################
# Extract Spring Boot layers
############################################
FROM builder AS extract

WORKDIR /app

# Rename generated jar
RUN cp target/*.jar app.jar

# Extract layers
RUN java -Djarmode=layertools -jar app.jar extract

############################################
# Runtime stage
############################################
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Create non-root user
RUN useradd -r -u 1001 spring

USER spring

# Copy extracted layers
COPY --from=extract /app/dependencies/ ./
COPY --from=extract /app/spring-boot-loader/ ./
COPY --from=extract /app/snapshot-dependencies/ ./
COPY --from=extract /app/application/ ./

EXPOSE 8080

ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]