#!/bin/bash

# Database backup script for Golf App
# This script creates a backup of the PostgreSQL database

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
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
BACKUP_FILE="${BACKUP_DIR}/${DB_NAME}_backup_${TIMESTAMP}.sql"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${YELLOW}🏌️ Golf App Database Backup Script${NC}"
echo "================================================"

# Check if backup directory exists
if [ ! -d "$BACKUP_DIR" ]; then
    echo -e "${YELLOW}Creating backup directory: $BACKUP_DIR${NC}"
    mkdir -p "$BACKUP_DIR"
fi

# Check if database is accessible
echo -e "${YELLOW}Checking database connection...${NC}"
if ! docker-compose exec db pg_isready -U "$DB_USER" -h localhost > /dev/null 2>&1; then
    echo -e "${RED}❌ Database is not accessible. Make sure the database container is running.${NC}"
    exit 1
fi

echo -e "${GREEN}✅ Database is accessible${NC}"

# Create backup
echo -e "${YELLOW}Creating backup...${NC}"
echo "Database: $DB_NAME"
echo "User: $DB_USER"
echo "Host: $DB_HOST"
echo "Port: $DB_PORT"
echo "Backup file: $BACKUP_FILE"

if docker-compose exec -T db pg_dump -U "$DB_USER" -h localhost -d "$DB_NAME" > "$BACKUP_FILE"; then
    echo -e "${GREEN}✅ Backup created successfully: $BACKUP_FILE${NC}"
    
    # Get file size
    BACKUP_SIZE=$(du -h "$BACKUP_FILE" | cut -f1)
    echo "Backup size: $BACKUP_SIZE"
    
    # Compress backup
    echo -e "${YELLOW}Compressing backup...${NC}"
    if gzip "$BACKUP_FILE"; then
        echo -e "${GREEN}✅ Backup compressed: ${BACKUP_FILE}.gz${NC}"
        COMPRESSED_SIZE=$(du -h "${BACKUP_FILE}.gz" | cut -f1)
        echo "Compressed size: $COMPRESSED_SIZE"
    else
        echo -e "${YELLOW}⚠️  Failed to compress backup, but backup was created successfully${NC}"
    fi
    
    # Clean up old backups (keep last 7 days)
    echo -e "${YELLOW}Cleaning up old backups...${NC}"
    find "$BACKUP_DIR" -name "${DB_NAME}_backup_*.sql*" -type f -mtime +7 -delete
    echo -e "${GREEN}✅ Old backups cleaned up${NC}"
    
else
    echo -e "${RED}❌ Failed to create backup${NC}"
    exit 1
fi

echo -e "${GREEN}🎉 Backup process completed successfully!${NC}"