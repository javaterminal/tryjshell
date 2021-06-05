FROM arm64v8/ubuntu:latest
MAINTAINER Rahman Usta
ENV LANG C.UTF-8
ADD jdk-17.AArch64/ /opt/jdk-17/
CMD ["/opt/jdk-17/bin/jshell"]