FROM openjdk:8-jre-slim
MAINTAINER Rahman Usta
ENV shell="/bin/bash -i"
COPY target/cloudterminal.jar /opt/cloudterminal/
CMD ["java","-jar","/opt/cloudterminal/cloudterminal.jar"]
EXPOSE 8080
