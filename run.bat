docker run -it --rm rahmanusta/openjdk12

docker run -dit -p 8080:8080 --restart unless-stopped --name=tryjshell rahmanusta/tryjshell
docker run -dit -p 8083:8080 --restart unless-stopped --name=tryjshell rahmanusta/tryjshell

docker run -it -p 8080:8080 --rm rahmanusta/tryjshell