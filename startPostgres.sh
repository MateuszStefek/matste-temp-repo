#!/bin/bash

# Start PostgreSQL in a Docker container
# Press Ctrl+C to stop the container

CONTAINER_NAME="interviews-postgres"
DB_NAME="interviews"
DB_USER="postgres"
DB_PASSWORD="postgres"
DB_PORT="5432"

echo "Starting PostgreSQL container..."

# Run PostgreSQL container
# --rm: Remove container when it stops
# -i: Interactive mode to capture Ctrl+C
docker run --rm -i \
  --name "$CONTAINER_NAME" \
  -e POSTGRES_DB="$DB_NAME" \
  -e POSTGRES_USER="$DB_USER" \
  -e POSTGRES_PASSWORD="$DB_PASSWORD" \
  -p "$DB_PORT:5432" \
  postgres:17

echo "PostgreSQL container stopped."

