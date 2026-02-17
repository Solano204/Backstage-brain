#!/bin/bash

echo "=== Setting PostgreSQL password explicitly ==="
docker exec backstage-postgres psql -U postgres -c "ALTER USER postgres WITH PASSWORD 'backstage';"

echo ""
echo "=== Checking pg_hba.conf content ==="
docker exec backstage-postgres cat /var/lib/postgresql/data/pg_hba.conf | grep -v "^#" | grep -v "^$"

echo ""
echo "=== Reloading PostgreSQL configuration ==="
docker exec backstage-postgres psql -U postgres -c "SELECT pg_reload_conf();"

echo ""
echo "Waiting 3 seconds for configuration to take effect..."
sleep 3

echo ""
echo "=== Testing Node.js connection ==="
node test-connection.js