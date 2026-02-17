#!/bin/bash

echo "=== Verifying pg_hba.conf ==="
docker exec backstage-postgres cat /var/lib/postgresql/data/pg_hba.conf

echo ""
echo "=== Testing direct psql connection (should work) ==="
docker exec backstage-postgres psql -U postgres -d backstage -c "SELECT 1;"

echo ""
echo "=== Now testing Node.js connection ==="
node test-connection.js