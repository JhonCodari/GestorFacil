#!/bin/bash

# Limpa containers, volumes e imagens antigas
set -e
docker-compose down --volumes --remove-orphans
docker image prune -a -f
docker volume prune -f

