FROM openjdk:11-jre-slim
EXPOSE 8080
COPY target/modopticon-1.0-SNAPSHOT-jar-with-dependencies.jar /home/modopticon.jar
CMD ["java","-jar","/home/modopticon.jar"]
