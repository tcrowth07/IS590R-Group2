FROM azul/zulu-openjdk-alpine:11
#Rename this to whatever the .jar file is called. The first is the local path, the second is where it will live on the docker image so in this case it will be at the root level.
ADD target/demo-0.0.1-SNAPSHOT.jar demo-0.0.1-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "demo-0.0.1-SNAPSHOT.jar"]
