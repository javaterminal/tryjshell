FROM openjdk:8-jre-slim
MAINTAINER Rahman Usta
ENV shell="/bin/bash -i"
COPY target/cloudterm.jar /opt/cloudterm/
CMD ["java","-jar","/opt/cloudterm/cloudterm.jar"]
EXPOSE 8080
