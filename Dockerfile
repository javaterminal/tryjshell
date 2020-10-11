FROM rahmanusta/openjdk15
MAINTAINER Rahman Usta
ENV shell="/opt/jdk-15/bin/jshell"
RUN apt-get update; \
    apt-get install -y --no-install-recommends vim
WORKDIR /opt/tryjshell/
COPY target/tryjshell.jar .
RUN useradd -ms /bin/bash tryjshell; \
    chmod -R  a+w /tmp || true; \
    chmod -R  a+w /home/tryjshell/ || true; \
    chmod -R  a+x /opt/jdk-15/bin/java || true; \
    chmod -R  a+x /opt/jdk-15/bin/jshell || true; \
    chmod -R  a+x+r /usr/bin/vim || true
USER tryjshell
CMD ["/opt/jdk-15/bin/java","-jar","./tryjshell.jar"]
