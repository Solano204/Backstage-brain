#!/bin/bash

echo "=== COMPLETE DOCKER CLEANUP ==="
echo ""

echo "1. Stopping all containers..."
docker-compose down -v

echo ""
echo "2. Removing backstage-postgres container..."
docker rm -f backstage-postgres 2>/dev/null || true

echo ""
echo "3. Removing backstage volume..."
docker volume rm backstage_backstage-data 2>/dev/null || true

echo ""
echo "4. Removing backstage network..."
docker network rm backstage_default 2>/dev/null || true

echo ""
echo "5. Pruning all unused Docker resources..."
docker system prune -f

echo ""
echo "6. Starting fresh containers..."
docker-compose up -d

echo ""
echo "7. Waiting for PostgreSQL to fully initialize (30 seconds)..."
sleep 30

echo ""
echo "8. Checking PostgreSQL logs..."
docker logs backstage-postgres | tail -20

echo ""
echo "9. Verifying pg_hba.conf configuration..."
docker exec backstage-postgres cat /var/lib/postgresql/data/pg_hba.conf

echo ""
echo "10. Testing connection from Node.js..."
node test-connection.js

echo ""
echo "=== DONE ==="