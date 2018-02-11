#!/bin/bash
docker run -it --rm -v $(pwd)/Caddyfile:/etc/Caddyfile -v $HOME/.caddy:/root/.caddy -v $(pwd)/web:/web -p 80:80 -p 443:443 abiosoft/caddy
