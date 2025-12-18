# Multi-service build via build arg SERVICE (module directory name)
ARG SERVICE

FROM maven:3.9.9-eclipse-temurin-17 AS build
ARG SERVICE
WORKDIR /src
COPY . .
RUN mvn -q -pl ${SERVICE} -am package -DskipTests && \
    mkdir -p /out && \
    cp ${SERVICE}/target/*SNAPSHOT.jar /out/app.jar

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /out/app.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
