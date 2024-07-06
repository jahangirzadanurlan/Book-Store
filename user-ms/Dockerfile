FROM openjdk:17-oracle
EXPOSE 8080
ARG JAR_FILE=build/libs/user-ms-0.0.1-SNAPSHOT.jar
ADD ${JAR_FILE} user.jar
ENTRYPOINT ["java", "-jar","/user.jar"]