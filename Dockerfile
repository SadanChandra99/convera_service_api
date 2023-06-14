FROM openjdk:17-oracle
EXPOSE 8081
ARG JAR_FILE=target/service-api-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} service-api-0.0.1-SNAPSHOT.jar

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar service-api-0.0.1-SNAPSHOT.jar"]
