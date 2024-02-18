FROM openjdk:21
COPY target/sms-management-service.jar sms-management-service.jar
EXPOSE 8081
ENTRYPOINT ["java", "-Dspring.profiles.active=dev","-jar","/sms-management-service.jar"]