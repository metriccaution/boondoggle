FROM maven:3-openjdk-11 as build
WORKDIR /app
COPY pom.xml pom.xml
COPY ./bg-poi ./bg-poi
COPY ./bg-compression ./bg-compression
COPY ./bg-web ./bg-web
COPY ./bg-main ./bg-main
RUN mvn clean install

FROM openjdk:8
WORKDIR /app
COPY --from=build /app/bg-web/target/boondoggle-web.jar .
CMD ["java", "-jar", "boondoggle-web.jar"]
