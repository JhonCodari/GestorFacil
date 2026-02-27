#!/bin/bash

# atualiza-projeto-docker.sh
mvn clean package
if [ $? -eq 0 ]; then
  docker-compose build app
  if [ $? -eq 0 ]; then
    docker-compose up -d --no-deps --build
  else
    echo "Erro ao executar: docker-compose build app"
    exit 2
  fi
else
  echo "Erro ao executar: mvn clean package"
  exit 1
fi