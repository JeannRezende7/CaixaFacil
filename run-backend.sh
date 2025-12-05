#!/bin/bash

cd "$(dirname "$0")/backend"

echo "Compilando e executando backend..."
./mvnw spring-boot:run
