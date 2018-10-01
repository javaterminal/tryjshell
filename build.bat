call mvn clean install
call docker build -t rahmanusta/openjdk12 . -f Dockerfile.jdk12
call docker build -t rahmanusta/tryjshell .