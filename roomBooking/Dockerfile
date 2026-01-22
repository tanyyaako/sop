FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY lib/events-roomBooking-contract.jar /tmp/events-roomBooking-contract.jar
COPY lib/hotelBooking-api.jar /tmp/hotelBooking-api.jar
RUN if [ -f /tmp/events-roomBooking-contract.jar ]; then \
        mvn install:install-file \
            -Dfile=/tmp/events-roomBooking-contract.jar \
            -DgroupId=org.example \
            -DartifactId=events-roomBooking-contract \
            -Dversion=1.0-SNAPSHOT \
            -Dpackaging=jar || true; \
    fi
RUN if [ -f /tmp/hotelBooking-api.jar ]; then \
        mvn install:install-file \
            -Dfile=/tmp/hotelBooking-api.jar \
            -DgroupId=org.example \
            -DartifactId=hotelBooking-api \
            -Dversion=0.0.1-SNAPSHOT \
            -Dpackaging=jar || true; \
    fi

COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

RUN addgroup -S javauser && adduser -S javauser -G javauser

COPY --from=build /app/target/*.jar app.jar

RUN chown javauser:javauser /app/app.jar
USER javauser

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]
