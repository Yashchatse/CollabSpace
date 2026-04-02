FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /app

# Copy parent pom first
COPY pom.xml ./pom.xml
COPY collabspace-eureka/pom.xml ./collabspace-eureka/pom.xml
COPY collabspace-eureka/src ./collabspace-eureka/src

# Install parent pom, then build only eureka
RUN mvn -f pom.xml install -N -DskipTests
RUN mvn -f collabspace-eureka/pom.xml clean package -DskipTests

FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/collabspace-eureka/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]