FROM openjdk:11
VOLUME /tmp
EXPOSE 8082
RUN mkdir -p /app/
RUN mkdir -p /app/logs/
ADD target/VendingMachineAPI-1.0-SNAPSHOT-jar-with-dependencies.jar /app/app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
