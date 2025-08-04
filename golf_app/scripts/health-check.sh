#!/bin/bash

# Health check script for Golf App services
# This script checks the health of all running services

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
BACKEND_URL="http://localhost:8080"
FRONTEND_URL="http://localhost:5173"
PROD_FRONTEND_URL="http://localhost:80"
DB_HOST="localhost"
DB_PORT="5432"
REDIS_HOST="localhost"
REDIS_PORT="6379"

echo -e "${BLUE}🏌️ Golf App Health Check${NC}"
echo "=========================="

# Function to check HTTP endpoint
check_http() {
    local url=$1
    local service_name=$2
    local timeout=${3:-10}
    
    echo -n "Checking $service_name... "
    
    if curl -f -s --max-time $timeout "$url" > /dev/null 2>&1; then
        echo -e "${GREEN}✅ Healthy${NC}"
        return 0
    else
        echo -e "${RED}❌ Unhealthy${NC}"
        return 1
    fi
}

# Function to check TCP port
check_port() {
    local host=$1
    local port=$2
    local service_name=$3
    
    echo -n "Checking $service_name... "
    
    if nc -z -w5 "$host" "$port" 2>/dev/null; then
        echo -e "${GREEN}✅ Healthy${NC}"
        return 0
    else
        echo -e "${RED}❌ Unhealthy${NC}"
        return 1
    fi
}

# Function to check database
check_database() {
    echo -n "Checking PostgreSQL... "
    
    if docker-compose exec -T db pg_isready -U postgres > /dev/null 2>&1; then
        echo -e "${GREEN}✅ Healthy${NC}"
        return 0
    else
        echo -e "${RED}❌ Unhealthy${NC}"
        return 1
    fi
}

# Function to check Redis
check_redis() {
    echo -n "Checking Redis... "
    
    if docker-compose exec -T redis redis-cli ping > /dev/null 2>&1; then
        echo -e "${GREEN}✅ Healthy${NC}"
        return 0
    else
        echo -e "${RED}❌ Unhealthy${NC}"
        return 1
    fi
}

# Detect environment
ENVIRONMENT="development"
if docker-compose -f docker-compose.prod.yml ps | grep -q "Up"; then
    ENVIRONMENT="production"
fi

echo "Environment: $ENVIRONMENT"
echo ""

# Initialize counters
HEALTHY_COUNT=0
TOTAL_COUNT=0

# Check services based on environment
if [ "$ENVIRONMENT" = "production" ]; then
    # Production health checks
    echo -e "${YELLOW}Checking production services...${NC}"
    
    # Frontend (Nginx)
    check_http "$PROD_FRONTEND_URL" "Frontend (Nginx)" && ((HEALTHY_COUNT++))
    ((TOTAL_COUNT++))
    
    # Backend (Spring Boot)
    check_http "$BACKEND_URL/actuator/health" "Backend (Spring Boot)" && ((HEALTHY_COUNT++))
    ((TOTAL_COUNT++))
    
    # Database
    check_database && ((HEALTHY_COUNT++))
    ((TOTAL_COUNT++))
    
    # Redis
    check_redis && ((HEALTHY_COUNT++))
    ((TOTAL_COUNT++))
    
    # Prometheus
    check_http "http://localhost:9090/-/healthy" "Prometheus" && ((HEALTHY_COUNT++))
    ((TOTAL_COUNT++))
    
    # Grafana
    check_http "http://localhost:3000/api/health" "Grafana" && ((HEALTHY_COUNT++))
    ((TOTAL_COUNT++))
    
else
    # Development health checks
    echo -e "${YELLOW}Checking development services...${NC}"
    
    # Frontend (Vite)
    check_http "$FRONTEND_URL" "Frontend (Vite)" && ((HEALTHY_COUNT++))
    ((TOTAL_COUNT++))
    
    # Backend (Spring Boot)
    check_http "$BACKEND_URL/actuator/health" "Backend (Spring Boot)" && ((HEALTHY_COUNT++))
    ((TOTAL_COUNT++))
    
    # Database
    check_database && ((HEALTHY_COUNT++))
    ((TOTAL_COUNT++))
    
    # Redis
    check_redis && ((HEALTHY_COUNT++))
    ((TOTAL_COUNT++))
    
    # MailHog
    check_http "http://localhost:8025" "MailHog" && ((HEALTHY_COUNT++))
    ((TOTAL_COUNT++))
fi

echo ""
echo "=========================="

# Summary
if [ $HEALTHY_COUNT -eq $TOTAL_COUNT ]; then
    echo -e "${GREEN}🎉 All services are healthy! ($HEALTHY_COUNT/$TOTAL_COUNT)${NC}"
    exit 0
else
    echo -e "${RED}⚠️  Some services are unhealthy! ($HEALTHY_COUNT/$TOTAL_COUNT)${NC}"
    echo ""
    echo -e "${YELLOW}Troubleshooting tips:${NC}"
    echo "1. Check if all containers are running: docker-compose ps"
    echo "2. Check container logs: docker-compose logs <service-name>"
    echo "3. Restart unhealthy services: docker-compose restart <service-name>"
    echo "4. Check resource usage: docker stats"
    exit 1
fi