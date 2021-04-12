# FROM library/postgres:alpine
#EXPOSE 5432

# Building the docker image
# docker build -t fullstack_backend .

# Running the docker image
# docker run -p 8080:8080 fullstack_backend

# What do we need to do here to compile the java?
FROM azul/zulu-openjdk-alpin:11
#Rename this to whatever the .jar file is called. The first is the local path, the second is where it will live on the docker image so in this case it will be at the root level.
ADD target/docker-spring-boot.jar docker-spring-boot.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "docker-spring-boot.jar"]

