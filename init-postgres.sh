#!/bin/bash
set -e

# This script runs during PostgreSQL initialization

echo "Configuring PostgreSQL to accept password authentication from all hosts..."

# Wait for PostgreSQL to be ready
until pg_isready -U postgres; do
  echo "Waiting for PostgreSQL to be ready..."
  sleep 1
done

# Update pg_hba.conf to allow password authentication from anywhere
cat > "${PGDATA}/pg_hba.conf" << 'EOF'
# TYPE  DATABASE        USER            ADDRESS                 METHOD

# "local" is for Unix domain socket connections only
local   all             all                                     trust
# IPv4 local connections:
host    all             all             127.0.0.1/32            md5
# IPv4 connections from Docker network and host
host    all             all             0.0.0.0/0               md5
# IPv6 local connections:
host    all             all             ::1/128                 md5
# IPv6 connections
host    all             all             ::/0                    md5
EOF

# Reload PostgreSQL configuration
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    SELECT pg_reload_conf();
EOSQL

echo "PostgreSQL configuration updated successfully!"