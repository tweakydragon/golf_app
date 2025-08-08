#!/bin/bash

# Development script for Golf App with live reloading
echo "🏌️ Starting Golf App Development Environment with Live Reloading..."

# Function to cleanup on exit
cleanup() {
    echo "🛑 Shutting down development environment..."
    docker-compose -f docker-compose.dev.yml down
    exit 0
}

# Set up signal handlers
trap cleanup SIGINT SIGTERM

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker is not running. Please start Docker and try again."
    exit 1
fi

# Build and start development environment
echo "🔨 Building development containers..."
docker-compose -f docker-compose.dev.yml build

echo "🚀 Starting development services..."
docker-compose -f docker-compose.dev.yml up

# This will keep the script running until interrupted
wait