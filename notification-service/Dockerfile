FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml .
COPY lib/events-roomBooking-contract.jar /tmp/events-roomBooking-contract.jar
RUN if [ -f /tmp/events-roomBooking-contract.jar ]; then \
        mvn install:install-file \
            -Dfile=/tmp/events-roomBooking-contract.jar \
            -DgroupId=org.example \
            -DartifactId=events-roomBooking-contract \
            -Dversion=1.0-SNAPSHOT \
            -Dpackaging=jar || true; \
    fi
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "/app/app.jar"]