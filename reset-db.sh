#!/usr/bin/env bash
# Reset the local Postgres database: wipe all data and let Flyway recreate the
# schema and seed data (V99__seed_dev_data.sql) from scratch, as if the
# database were being created for the first time.
set -euo pipefail

cd "$(dirname "${BASH_SOURCE[0]}")"

COMPOSE="docker compose"

if [ ! -f .env ]; then
  echo "Error: .env not found in $(pwd). Copy/create it with DB_USER, DB_PASSWORD, DB_NAME before resetting." >&2
  exit 1
fi

# Resolve the volume backing THIS project's postgres container by inspecting its
# mount, rather than filtering by the generic com.docker.compose.volume=postgres_data
# label. That label isn't scoped to this project, so on a machine with other,
# unrelated compose projects that also happen to name a volume "postgres_data" it
# would match all of them at once and fail (or remove the wrong volume).
CONTAINER_ID="$($COMPOSE ps -aq postgres)"
VOLUME=""
if [ -n "$CONTAINER_ID" ]; then
  VOLUME="$(docker inspect "$CONTAINER_ID" --format '{{range .Mounts}}{{if eq .Destination "/var/lib/postgresql"}}{{.Name}}{{end}}{{end}}')"
fi

echo "Stopping postgres..."
$COMPOSE stop postgres
$COMPOSE rm -f postgres

if [ -n "$VOLUME" ]; then
  echo "Removing volume $VOLUME..."
  docker volume rm "$VOLUME"
else
  echo "No existing postgres volume found, skipping."
fi

echo "Starting postgres (fresh volume)..."
$COMPOSE up -d postgres

echo "Waiting for postgres to become healthy..."
until [ "$($COMPOSE ps -q postgres | xargs docker inspect -f '{{.State.Health.Status}}')" = "healthy" ]; do
  sleep 1
done

if [ -n "$($COMPOSE ps -q backend)" ]; then
  echo "Restarting backend to re-run Flyway migrations..."
  $COMPOSE restart backend
fi

$COMPOSE ps postgres

echo
echo "Database reset. If the backend is running outside Docker (e.g. via ./mvnw spring-boot:run),"
echo "restart it now so Flyway re-applies migrations (schema + seed data from V99__seed_dev_data.sql)"
echo "against the fresh database."
