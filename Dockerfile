# Custom image for the service
# Rather than be directly part of the build, this Dockerfile is for creating the base image jib later uses.
# Run the following when updates to the Java version are needed for this project:
#     docker build -t 514329541303.dkr.ecr.us-east-1.amazonaws.com/scube-document-service-base:1.0.0 .
#     docker push 514329541303.dkr.ecr.us-east-1.amazonaws.com/scube-document-service-base:1.0.0
FROM eclipse-temurin:21-jre-alpine

# Create the /app directory before downloading the file
RUN mkdir -p /app

# Download the opentelemetry-javaagent.jar directly from GitHub
RUN wget -O /app/opentelemetry-javaagent.jar https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v1.33.6/opentelemetry-javaagent.jar