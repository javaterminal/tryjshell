FROM openjdk:9-jdk-slim
MAINTAINER Rahman Usta
ENV shell="/usr/bin/jshell"
RUN apt-get update -y && apt-get install sudo vim iputils-ping -y
VOLUME /tmp
WORKDIR /opt/tryjshell/
COPY target/tryjshell.jar .
RUN useradd -ms /bin/bash tryjshell
RUN chmod -R  a-w / || true
RUN chmod -R  a+w /tmp || true
RUN chmod -R  a+w /home/tryjshell/ || true
RUN chmod -R  a+x /usr/bin/java || true
RUN chmod -R  a+x /usr/bin/jshell || true
USER tryjshell
CMD ["java","-jar","./tryjshell.jar"]