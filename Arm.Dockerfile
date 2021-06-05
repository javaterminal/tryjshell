FROM rahmanusta/jdk17.arm64
MAINTAINER Rahman Usta
ENV shell="/opt/jdk-17/bin/jshell"
RUN apt-get update; \
    apt-get install -y --no-install-recommends vim
WORKDIR /opt/tryjshell/
COPY target/tryjshell.jar .
RUN useradd -ms /bin/bash tryjshell; \
    chmod -R  a+w /tmp || true; \
    chmod -R  a+w /home/tryjshell/ || true; \
    chmod -R  a+x /opt/jdk-17/bin/java || true; \
    chmod -R  a+x /opt/jdk-17/bin/jshell || true; \
    chmod -R  a+x+r /usr/bin/vim || true
USER tryjshell
CMD ["/opt/jdk-17/bin/java","-jar","./tryjshell.jar"]