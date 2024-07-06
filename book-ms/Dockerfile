FROM openjdk:17-oracle
EXPOSE 8081
ARG JAR_FILE=build/libs/book-ms-0.0.1-SNAPSHOT.jar
ADD ${JAR_FILE} book.jar
ENTRYPOINT ["java", "-jar","/book.jar"]