FROM docker-hub.kian.digital/java:8-jdk
EXPOSE 8080
ADD ./target/gateway-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]