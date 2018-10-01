FROM rahmanusta/openjdk12
MAINTAINER Rahman Usta
ENV shell="/opt/jdk-12/bin/jshell"
RUN apt-get update -y && apt-get install sudo vim -y
#VOLUME /tmp
WORKDIR /opt/tryjshell/
COPY target/tryjshell.jar .
RUN useradd -ms /bin/bash tryjshell
#RUN chmod -R  a-w / || true
RUN chmod -R  a+w /tmp || true
RUN chmod -R  a+w /home/tryjshell/ || true
RUN chmod -R  a+x /opt/jdk-12/bin/java || true
RUN chmod -R  a+x /opt/jdk-12/bin/jshell || true
RUN chmod -R  a+x+r /usr/bin/vim || true
USER tryjshell
CMD ["/opt/jdk-12/bin/java","-jar","./tryjshell.jar"]
