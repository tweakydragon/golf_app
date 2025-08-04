#!/bin/bash

# Deployment script for Golf App
# Supports both development and production deployments

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"
DEFAULT_ENV="development"
ENVIRONMENT="${1:-$DEFAULT_ENV}"

# Function to display usage
show_usage() {
    echo -e "${BLUE}🏌️ Golf App Deployment Script${NC}"
    echo "=================================="
    echo ""
    echo "Usage: $0 [environment] [options]"
    echo ""
    echo "Environments:"
    echo "  development  - Deploy development environment (default)"
    echo "  production   - Deploy production environment"
    echo "  staging      - Deploy staging environment"
    echo ""
    echo "Options:"
    echo "  --build      - Force rebuild of Docker images"
    echo "  --backup     - Create database backup before deployment"
    echo "  --no-cache   - Build without using cache"
    echo "  --help       - Show this help message"
    echo ""
    echo "Examples:"
    echo "  $0                           # Deploy development"
    echo "  $0 production --build       # Deploy production with rebuild"
    echo "  $0 staging --backup         # Deploy staging with backup"
}

# Function to check prerequisites
check_prerequisites() {
    echo -e "${YELLOW}Checking prerequisites...${NC}"
    
    # Check if Docker is running
    if ! docker info > /dev/null 2>&1; then
        echo -e "${RED}❌ Docker is not running. Please start Docker and try again.${NC}"
        exit 1
    fi
    
    # Check if Docker Compose is available
    if ! command -v docker-compose > /dev/null 2>&1; then
        echo -e "${RED}❌ Docker Compose is not installed.${NC}"
        exit 1
    fi
    
    # Check if environment file exists for production
    if [ "$ENVIRONMENT" = "production" ] && [ ! -f "$PROJECT_DIR/.env" ]; then
        echo -e "${RED}❌ Production environment requires .env file.${NC}"
        echo -e "${YELLOW}Please copy .env.template to .env and configure it.${NC}"
        exit 1
    fi
    
    echo -e "${GREEN}✅ Prerequisites check passed${NC}"
}

# Function to create database backup
create_backup() {
    echo -e "${YELLOW}Creating database backup...${NC}"
    if [ -f "$PROJECT_DIR/scripts/backup-database.sh" ]; then
        cd "$PROJECT_DIR"
        ./scripts/backup-database.sh
    else
        echo -e "${YELLOW}⚠️  Backup script not found, skipping backup${NC}"
    fi
}

# Function to deploy development environment
deploy_development() {
    echo -e "${BLUE}🚀 Deploying Development Environment${NC}"
    cd "$PROJECT_DIR"
    
    # Stop existing services
    echo -e "${YELLOW}Stopping existing services...${NC}"
    docker-compose -f docker-compose.dev.yml down || true
    
    # Build and start services
    if [[ "$*" == *"--build"* ]] || [[ "$*" == *"--no-cache"* ]]; then
        echo -e "${YELLOW}Building images...${NC}"
        BUILD_ARGS=""
        if [[ "$*" == *"--no-cache"* ]]; then
            BUILD_ARGS="--no-cache"
        fi
        docker-compose -f docker-compose.dev.yml build $BUILD_ARGS
    fi
    
    echo -e "${YELLOW}Starting development services...${NC}"
    docker-compose -f docker-compose.dev.yml up -d
    
    # Wait for services to be ready
    echo -e "${YELLOW}Waiting for services to be ready...${NC}"
    sleep 10
    
    # Show service status
    docker-compose -f docker-compose.dev.yml ps
    
    echo -e "${GREEN}✅ Development environment deployed successfully!${NC}"
    echo -e "${BLUE}Frontend: http://localhost:5173${NC}"
    echo -e "${BLUE}Backend: http://localhost:8080${NC}"
    echo -e "${BLUE}API Documentation: http://localhost:8080/swagger-ui.html${NC}"
}

# Function to deploy production environment
deploy_production() {
    echo -e "${BLUE}🚀 Deploying Production Environment${NC}"
    cd "$PROJECT_DIR"
    
    # Load environment variables
    if [ -f .env ]; then
        export $(cat .env | grep -v '^#' | xargs)
    fi
    
    # Stop existing services
    echo -e "${YELLOW}Stopping existing services...${NC}"
    docker-compose -f docker-compose.prod.yml down || true
    
    # Build and start services
    if [[ "$*" == *"--build"* ]] || [[ "$*" == *"--no-cache"* ]]; then
        echo -e "${YELLOW}Building images...${NC}"
        BUILD_ARGS=""
        if [[ "$*" == *"--no-cache"* ]]; then
            BUILD_ARGS="--no-cache"
        fi
        docker-compose -f docker-compose.prod.yml build $BUILD_ARGS
    fi
    
    echo -e "${YELLOW}Starting production services...${NC}"
    docker-compose -f docker-compose.prod.yml up -d
    
    # Wait for services to be ready
    echo -e "${YELLOW}Waiting for services to be ready...${NC}"
    sleep 30
    
    # Show service status
    docker-compose -f docker-compose.prod.yml ps
    
    # Health check
    echo -e "${YELLOW}Running health checks...${NC}"
    sleep 10
    
    # Check backend health
    if curl -f http://localhost:${BACKEND_PORT:-8080}/actuator/health > /dev/null 2>&1; then
        echo -e "${GREEN}✅ Backend health check passed${NC}"
    else
        echo -e "${RED}❌ Backend health check failed${NC}"
    fi
    
    # Check frontend
    if curl -f http://localhost:${FRONTEND_PORT:-80} > /dev/null 2>&1; then
        echo -e "${GREEN}✅ Frontend health check passed${NC}"
    else
        echo -e "${RED}❌ Frontend health check failed${NC}"
    fi
    
    echo -e "${GREEN}✅ Production environment deployed successfully!${NC}"
    echo -e "${BLUE}Frontend: http://localhost:${FRONTEND_PORT:-80}${NC}"
    echo -e "${BLUE}Backend: http://localhost:${BACKEND_PORT:-8080}${NC}"
    echo -e "${BLUE}API Documentation: http://localhost:${BACKEND_PORT:-8080}/swagger-ui.html${NC}"
    echo -e "${BLUE}Monitoring: http://localhost:${GRAFANA_PORT:-3000}${NC}"
}

# Function to deploy staging environment
deploy_staging() {
    echo -e "${BLUE}🚀 Deploying Staging Environment${NC}"
    # For now, use development setup with different ports
    deploy_development
}

# Main deployment logic
main() {
    # Check for help flag
    if [[ "$*" == *"--help"* ]]; then
        show_usage
        exit 0
    fi
    
    echo -e "${BLUE}🏌️ Golf App Deployment Script${NC}"
    echo "=================================="
    echo "Environment: $ENVIRONMENT"
    echo "Project Directory: $PROJECT_DIR"
    echo ""
    
    # Check prerequisites
    check_prerequisites
    
    # Create backup if requested
    if [[ "$*" == *"--backup"* ]]; then
        create_backup
    fi
    
    # Deploy based on environment
    case $ENVIRONMENT in
        development|dev)
            deploy_development "$@"
            ;;
        production|prod)
            deploy_production "$@"
            ;;
        staging|stage)
            deploy_staging "$@"
            ;;
        *)
            echo -e "${RED}❌ Unknown environment: $ENVIRONMENT${NC}"
            show_usage
            exit 1
            ;;
    esac
    
    echo -e "${GREEN}🎉 Deployment completed successfully!${NC}"
}

# Run main function
main "$@"