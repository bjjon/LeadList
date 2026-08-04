#!/usr/bin/env bash
# Deploy the VPS-optimized stack (postgres, backend, frontend, nginx).
# Explicitly pins -f docker-compose.yaml so a local docker-compose.override.yml
# (used for local dev, e.g. re-publishing the postgres port) is never picked up here.
set -euo pipefail

cd "$(dirname "${BASH_SOURCE[0]}")"

COMPOSE="docker compose -f docker-compose.yaml"

if [ ! -f .env ]; then
  echo "Error: .env not found in $(pwd). Copy/create it with DB_USER, DB_PASSWORD, DB_NAME, JWT_SECRET, JWT_EXPIRATION before deploying." >&2
  exit 1
fi

$COMPOSE pull --ignore-buildable
$COMPOSE up -d --build
$COMPOSE ps
