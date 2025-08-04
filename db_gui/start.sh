#!/bin/bash

# Customer Database Management Lab - Startup Script
# This script helps you get the lab environment running quickly

set -e

echo "🚀 Starting Customer Database Management Lab..."
echo ""

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    print_error "Docker is not running. Please start Docker and try again."
    exit 1
fi

# Check if Docker Compose is available
if ! command -v docker-compose > /dev/null 2>&1; then
    if ! docker compose version > /dev/null 2>&1; then
        print_error "Docker Compose is not available. Please install Docker Compose and try again."
        exit 1
    else
        DOCKER_COMPOSE_CMD="docker compose"
    fi
else
    DOCKER_COMPOSE_CMD="docker-compose"
fi

print_status "Using Docker Compose command: $DOCKER_COMPOSE_CMD"

# Function to wait for service to be ready
wait_for_service() {
    local service_name=$1
    local url=$2
    local max_attempts=30
    local attempt=1
    
    print_status "Waiting for $service_name to be ready..."
    
    while [ $attempt -le $max_attempts ]; do
        if curl -f -s $url > /dev/null 2>&1; then
            print_success "$service_name is ready!"
            return 0
        fi
        
        echo -n "."
        sleep 2
        attempt=$((attempt + 1))
    done
    
    print_error "$service_name failed to start within expected time."
    return 1
}

# Parse command line arguments
COMMAND=${1:-"start"}

case $COMMAND in
    "start")
        print_status "Starting all services..."
        $DOCKER_COMPOSE_CMD up -d
        
        print_status "Services started. Waiting for them to be ready..."
        echo ""
        
        # Wait for database
        wait_for_service "PostgreSQL Database" "postgres://postgres:postgres@localhost:5432/customer_db" || true
        
        # Wait for backend
        if wait_for_service "Backend API" "http://localhost:8080/api/customers"; then
            print_success "Backend API is ready at http://localhost:8080"
        fi
        
        # Wait for frontend
        if wait_for_service "Frontend Application" "http://localhost:3000"; then
            print_success "Frontend Application is ready at http://localhost:3000"
        fi
        
        echo ""
        print_success "🎉 Lab environment is ready!"
        echo ""
        echo "📋 Service URLs:"
        echo "   Frontend:  http://localhost:3000"
        echo "   Backend:   http://localhost:8080"
        echo "   Database:  localhost:5432 (postgres/postgres)"
        echo ""
        echo "📊 Sample Data:"
        echo "   5 sample customers have been automatically created"
        echo "   You can view and edit them in the frontend application"
        echo ""
        echo "🔍 Monitoring:"
        echo "   View logs: ./start.sh logs"
        echo "   Stop services: ./start.sh stop"
        echo ""
        ;;
        
    "stop")
        print_status "Stopping all services..."
        $DOCKER_COMPOSE_CMD down
        print_success "All services stopped."
        ;;
        
    "restart")
        print_status "Restarting all services..."
        $DOCKER_COMPOSE_CMD down
        $DOCKER_COMPOSE_CMD up -d
        print_success "All services restarted."
        ;;
        
    "logs")
        if [ -n "$2" ]; then
            print_status "Showing logs for $2..."
            $DOCKER_COMPOSE_CMD logs -f $2
        else
            print_status "Showing logs for all services..."
            $DOCKER_COMPOSE_CMD logs -f
        fi
        ;;
        
    "status")
        print_status "Service status:"
        $DOCKER_COMPOSE_CMD ps
        ;;
        
    "clean")
        print_warning "This will remove all containers, volumes, and data. Are you sure? (y/N)"
        read -r response
        if [[ "$response" =~ ^([yY][eE][sS]|[yY])$ ]]; then
            print_status "Cleaning up all resources..."
            $DOCKER_COMPOSE_CMD down -v --remove-orphans
            docker system prune -f
            print_success "Cleanup completed."
        else
            print_status "Cleanup cancelled."
        fi
        ;;
        
    "help"|"-h"|"--help")
        echo "Customer Database Management Lab - Startup Script"
        echo ""
        echo "Usage: ./start.sh [COMMAND]"
        echo ""
        echo "Commands:"
        echo "  start     Start all services (default)"
        echo "  stop      Stop all services"
        echo "  restart   Restart all services"
        echo "  logs      Show logs for all services"
        echo "  logs <service>  Show logs for specific service"
        echo "  status    Show service status"
        echo "  clean     Remove all containers and data"
        echo "  help      Show this help message"
        echo ""
        echo "Examples:"
        echo "  ./start.sh start"
        echo "  ./start.sh logs backend"
        echo "  ./start.sh stop"
        ;;
        
    *)
        print_error "Unknown command: $COMMAND"
        echo "Use './start.sh help' for usage information."
        exit 1
        ;;
esac
