#!/bin/bash

echo "=== Fixing pg_hba.conf directly in container ==="

docker exec backstage-postgres sh -c "cat > /var/lib/postgresql/data/pg_hba.conf << 'EOF'
# TYPE  DATABASE        USER            ADDRESS                 METHOD

# Local connections
local   all             all                                     trust

# IPv4 local connections
host    all             all             127.0.0.1/32            md5

# IPv4 connections from anywhere
host    all             all             0.0.0.0/0               md5

# IPv6 local connections
host    all             all             ::1/128                 md5

# IPv6 connections from anywhere
host    all             all             ::/0                    md5
EOF
"

echo ""
echo "=== Verifying new pg_hba.conf content ==="
docker exec backstage-postgres sh -c "cat /var/lib/postgresql/data/pg_hba.conf"

echo ""
echo "=== Reloading PostgreSQL ==="
docker exec backstage-postgres psql -U postgres -c "SELECT pg_reload_conf();"

echo ""
echo "Waiting 5 seconds..."
sleep 5

echo ""
echo "=== Testing connection ==="
node test-connection.js