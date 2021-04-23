FROM docker-hub.kian.digital/java:8-jdk

EXPOSE 8888

ADD ./target/config-server-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java","-jar","/app.jar"]
