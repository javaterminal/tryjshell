#!/usr/bin/env bash
.mvnw clean install -DskipTests -T 1.5C
#Builds jdk17 image for arm64 platform
docker build -t rahmanusta/jdk17.arm64 . -f jdk17.arm64.Dockerfile

docker build -t rahmanusta/tryjshell.arm . -f Arm.Dockerfile
docker build -t rahmanusta/tryjshell .