FROM openjdk:8-alpine

COPY target/uberjar/invistron.jar /invistron/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/invistron/app.jar"]
