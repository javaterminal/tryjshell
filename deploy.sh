mvn clean install
docker stop tryjshell || true
docker rm tryjshell || true
docker rmi rahmanusta/tryjshell || true
docker build -t rahmanusta/tryjshell .
docker run -i -d --restart unless-stopped -p 8080:8080 --name tryjshell rahmanusta/tryjshell