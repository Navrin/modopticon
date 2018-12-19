FROM anapsix/alpine-java
EXPOSE 443 8080
COPY target/modopticon-1.0-SNAPSHOT-jar-with-dependencies.jar /home/modopticon.jar
CMD ["java","-jar","/home/modopticon.jar"]
