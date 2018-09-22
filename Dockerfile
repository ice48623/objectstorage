FROM java:8
EXPOSE 8080
ADD target/objectstorage-0.0.1-SNAPSHOT.jar objectstorage-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","objectstorage-0.0.1-SNAPSHOT.jar"]
