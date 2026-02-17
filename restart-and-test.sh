#!/bin/bash

echo "=== Restarting PostgreSQL container to apply pg_hba.conf changes ==="
docker restart backstage-postgres

echo ""
echo "Waiting 15 seconds for PostgreSQL to fully restart..."
sleep 15

echo ""
echo "=== Checking if PostgreSQL is ready ==="
docker exec backstage-postgres pg_isready -U postgres

echo ""
echo "=== Testing connection ==="
node test-connection.js

echo ""
if [ $? -eq 0 ]; then
    echo "✓ SUCCESS! Now starting Backstage..."
    yarn workspace backend start
else
    echo "✗ Still failing. Let's check the logs..."
    docker logs backstage-postgres | tail -30
fi