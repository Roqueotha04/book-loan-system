FROM eclipse-temurin:17-jdk-alpine AS build

WORKDIR /root

COPY ./pom.xml /root
COPY ./.mvn /root/.mvn
COPY ./mvnw /root

RUN chmod +x mvnw

RUN ./mvnw dependency:go-offline

COPY ./src /root/src
RUN ./mvnw clean install

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=build /root/target/loansystem-0.0.1-SNAPSHOT.jar /app/app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]