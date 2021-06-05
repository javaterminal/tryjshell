#docker run -it --rm rahmanusta/jdk17.arm64
#docker run -dit -p 8080:8080 --restart unless-stopped --name=tryjshell rahmanusta/tryjshell
#docker run -dit -p 80:8080 --restart unless-stopped --name=tryjshell rahmanusta/tryjshell.arm

# run local
docker run -it -p 8080:8080 --rm rahmanusta/tryjshell

#run local on arm64
docker run -it -p 8080:8080 --rm rahmanusta/tryjshell.arm

sudo docker run --name nginx-proxy -dit --restart unless-stopped \
    --publish 80:80 \
    --publish 443:443 \
    --volume /etc/nginx/certs \
    --volume /etc/nginx/vhost.d \
    --volume /usr/share/nginx/html \
    --volume /var/run/docker.sock:/tmp/docker.sock:ro \
    nginxproxy/nginx-proxy

sudo docker run --name nginx-proxy-letsencrypt -dit --restart unless-stopped \
    --volumes-from nginx-proxy \
    --volume /var/run/docker.sock:/var/run/docker.sock:ro \
    --env "DEFAULT_EMAIL=rahman.usta.88@gmail.com" \
    jrcs/letsencrypt-nginx-proxy-companion

sudo docker run --name tryjshell -dit --restart unless-stopped --expose 8080 \
    --env "VIRTUAL_HOST=www.tryjshell.org,tryjshell.org" \
    --env "VIRTUAL_PORT=8080" \
    --env "LETSENCRYPT_HOST=www.tryjshell.org,tryjshell.org" \
    --env "LETSENCRYPT_EMAIL=rahman.usta.88@gmail.com" \
    rahmanusta/tryjshell.arm