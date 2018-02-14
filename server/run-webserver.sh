#!/bin/bash
#This script runs the web server on Docker using Caddy for secure, automatic HTTPS certificates

docker run -it --rm -v $(pwd)/Caddyfile:/etc/Caddyfile -v $HOME/.caddy:/root/.caddy -v $(pwd)/web:/web -p 80:80 -p 443:443 abiosoft/caddy
