FROM openjdk:17
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} prog_tech-1.0-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/prog_tech-1.0-SNAPSHOT.jar"]