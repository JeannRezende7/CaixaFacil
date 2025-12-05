#!/bin/bash

cd "$(dirname "$0")/frontend"

if [ ! -d "node_modules" ]; then
  echo "Instalando dependências..."
  npm install
fi

echo "Executando frontend..."
npm run dev
