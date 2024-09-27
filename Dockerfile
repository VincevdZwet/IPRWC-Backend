FROM openjdk:17-jdk-alpine

WORKDIR /usr/src/app

COPY . /usr/src/app

RUN ./mvnw clean package -DskipTests

EXPOSE 8000

ENTRYPOINT ["java", "-jar", "target/iprwc-backend-0.0.1-SNAPSHOT.jar"]