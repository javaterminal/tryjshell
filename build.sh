#!/usr/bin/env bash
.mvnw clean install -DskipTests -T 1.5C
#docker build -t rahmanusta/openjdk12 . -f Dockerfile.jdk12
docker build -t rahmanusta/openjdk15 . -f Dockerfile.jdk15
docker build -t rahmanusta/tryjshell .