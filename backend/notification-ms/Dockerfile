FROM adoptopenjdk/openjdk11
EXPOSE 8082
ARG JAR_FILE=build/libs/notification-ms-0.0.1-SNAPSHOT.jar
ADD ${JAR_FILE} notification.jar
ENTRYPOINT ["java", "-jar","/notification.jar"]