# BUILD STAGE
FROM gradle:jdk21-ubi-minimal AS build

WORKDIR /app

# Copy everything first
COPY . .

# Make gradlew executable AFTER copy
RUN chmod +x gradlew

# Build
RUN ./gradlew clean build -x test --no-daemon

# RUN STAGE
FROM eclipse-temurin:21.0.7_6-jre-ubi9-minimal

COPY --from=build /app/build/libs/*.jar /app/app.jar

EXPOSE 8080

CMD ["java", "-jar", "/app/app.jar"]


