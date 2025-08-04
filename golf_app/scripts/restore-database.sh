#!/bin/bash

# Database restore script for Golf App
# This script restores a PostgreSQL database from a backup

set -e

# Load environment variables
if [ -f .env ]; then
    export $(cat .env | grep -v '^#' | xargs)
fi

# Configuration
DB_NAME=${POSTGRES_DB:-golfdb}
DB_USER=${POSTGRES_USER:-postgres}
DB_PASSWORD=${POSTGRES_PASSWORD}
DB_HOST=${DB_HOST:-localhost}
DB_PORT=${POSTGRES_PORT:-5432}
BACKUP_DIR="./database/backups"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${YELLOW}🏌️ Golf App Database Restore Script${NC}"
echo "================================================"

# Check if backup file is provided
if [ -z "$1" ]; then
    echo -e "${YELLOW}Available backup files:${NC}"
    ls -la "$BACKUP_DIR"/ | grep -E "\.(sql|gz)$" || echo "No backup files found"
    echo ""
    echo -e "${RED}Usage: $0 <backup_file>${NC}"
    echo "Example: $0 database/backups/golfdb_backup_20240101_120000.sql"
    echo "Example: $0 database/backups/golfdb_backup_20240101_120000.sql.gz"
    exit 1
fi

BACKUP_FILE="$1"

# Check if backup file exists
if [ ! -f "$BACKUP_FILE" ]; then
    echo -e "${RED}❌ Backup file not found: $BACKUP_FILE${NC}"
    exit 1
fi

# Check if database is accessible
echo -e "${YELLOW}Checking database connection...${NC}"
if ! docker-compose exec db pg_isready -U "$DB_USER" -h localhost > /dev/null 2>&1; then
    echo -e "${RED}❌ Database is not accessible. Make sure the database container is running.${NC}"
    exit 1
fi

echo -e "${GREEN}✅ Database is accessible${NC}"

# Confirm restoration
echo -e "${YELLOW}⚠️  WARNING: This will OVERWRITE the current database!${NC}"
echo "Database: $DB_NAME"
echo "User: $DB_USER"
echo "Host: $DB_HOST"
echo "Port: $DB_PORT"
echo "Backup file: $BACKUP_FILE"
echo ""
read -p "Are you sure you want to continue? (y/N): " -n 1 -r
echo ""
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    echo -e "${YELLOW}Restoration cancelled${NC}"
    exit 0
fi

# Prepare backup file
TEMP_FILE=""
if [[ "$BACKUP_FILE" == *.gz ]]; then
    echo -e "${YELLOW}Decompressing backup file...${NC}"
    TEMP_FILE="/tmp/$(basename "$BACKUP_FILE" .gz)"
    gunzip -c "$BACKUP_FILE" > "$TEMP_FILE"
    RESTORE_FILE="$TEMP_FILE"
else
    RESTORE_FILE="$BACKUP_FILE"
fi

echo -e "${YELLOW}Restoring database...${NC}"

# Drop existing connections and recreate database
echo -e "${YELLOW}Terminating existing connections...${NC}"
docker-compose exec -T db psql -U "$DB_USER" -h localhost -c "
    SELECT pg_terminate_backend(pid) 
    FROM pg_stat_activity 
    WHERE datname = '$DB_NAME' AND pid <> pg_backend_pid();
" postgres || true

echo -e "${YELLOW}Dropping and recreating database...${NC}"
docker-compose exec -T db psql -U "$DB_USER" -h localhost -c "DROP DATABASE IF EXISTS $DB_NAME;" postgres
docker-compose exec -T db psql -U "$DB_USER" -h localhost -c "CREATE DATABASE $DB_NAME;" postgres

# Restore from backup
echo -e "${YELLOW}Restoring from backup...${NC}"
if docker-compose exec -T db psql -U "$DB_USER" -h localhost -d "$DB_NAME" < "$RESTORE_FILE"; then
    echo -e "${GREEN}✅ Database restored successfully from: $BACKUP_FILE${NC}"
else
    echo -e "${RED}❌ Failed to restore database${NC}"
    exit 1
fi

# Clean up temporary file
if [ -n "$TEMP_FILE" ]; then
    rm -f "$TEMP_FILE"
fi

echo -e "${GREEN}🎉 Database restoration completed successfully!${NC}"