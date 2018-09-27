FROM java:8
EXPOSE 8080

ADD ./src/main/resources/application.properties ./application.properties
ADD ./target/objectstorage-0.0.1-SNAPSHOT.jar /webapi/objectstorage-0.0.1-SNAPSHOT.jar
WORKDIR /webapi
VOLUME /webapi/bucket
CMD ["java", "-jar", "./objectstorage-0.0.1-SNAPSHOT.jar"]
