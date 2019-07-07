FROM openjdk:13-ea-27
MAINTAINER Rahman Usta
ENV shell="jshell"
RUN yum install -y vim
#VOLUME /tmp
WORKDIR /opt/tryjshell/
COPY target/tryjshell.jar .
RUN useradd -ms /bin/bash tryjshell
#RUN chmod -R  a-w / || true
RUN chmod -R  a+w /tmp || true
RUN chmod -R  a+w /home/tryjshell/ || true
RUN chmod -R  a+x $(which java) || true
RUN chmod -R  a+x $(which jshell) || true
RUN chmod -R  a+x+r /usr/bin/vim || true
USER tryjshell
CMD ["java","-jar","./tryjshell.jar"]
