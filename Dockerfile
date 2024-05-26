FROM openjdk:17

COPY ./build/libs/demo-0.0.1-SNAPSHOT.jar /app

WORKDIR /app

CMD ["java", "-jar", "demo-0.0.1-SNAPSHOT"]