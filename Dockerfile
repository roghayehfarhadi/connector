FROM openjdk:11-jre-slim
ARG APM_VERSION=1.33.0
ARG APM_SERVER_URLS=http://0.0.0.0:8200
WORKDIR /opt/connector
COPY target/*.jar /opt/connector/app.jar
COPY src/main/resources/elastic-apm-agent-$APM_VERSION.jar /opt/connector/elastic-apm-agent-$APM_VERSION.jar
COPY startup.sh startup.sh

RUN chmod +x startup.sh

ENV SPRING_OUTPUT_ANSI_ENABLED=ALWAYS \
    APM_VERSION=$APM_VERSION \
    APM_SERVICE_NAME="connector" \
    APM_SERVER_URLS=$APM_SERVER_URLS \
    APM_SERVER_ENABLED=true
EXPOSE 8085
CMD './startup.sh'