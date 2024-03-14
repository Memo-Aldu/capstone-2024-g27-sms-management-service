FROM maven:latest AS build
WORKDIR /app
ARG CONTAINER_PORT
COPY pom.xml /app
RUN mvn dependency:resolve
COPY . /app
RUN mvn clean
RUN mvn package -DskipTests -X

FROM openjdk:21-jdk-slim
COPY --from=build /app/target/sms-management-service.jar sms-management-service.jar
EXPOSE ${CONTAINER_PORT}
ENTRYPOINT ["java","-jar","sms-management-service.jar"]