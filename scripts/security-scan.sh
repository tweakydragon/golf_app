#!/bin/bash

# Security scan script for Golf App
# This script runs various security scans on the Docker images and codebase

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}🔒 Golf App Security Scan${NC}"
echo "=========================="

# Function to check if command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Function to run Trivy scan
run_trivy_scan() {
    echo -e "${YELLOW}Running Trivy vulnerability scan...${NC}"
    
    if ! command_exists trivy; then
        echo -e "${YELLOW}Installing Trivy...${NC}"
        # Install Trivy based on OS
        if [[ "$OSTYPE" == "linux-gnu"* ]]; then
            sudo apt-get update
            sudo apt-get install wget apt-transport-https gnupg lsb-release
            wget -qO - https://aquasecurity.github.io/trivy-repo/deb/public.key | sudo apt-key add -
            echo "deb https://aquasecurity.github.io/trivy-repo/deb $(lsb_release -sc) main" | sudo tee -a /etc/apt/sources.list.d/trivy.list
            sudo apt-get update
            sudo apt-get install trivy
        elif [[ "$OSTYPE" == "darwin"* ]]; then
            brew install trivy
        else
            echo -e "${RED}❌ Unsupported OS. Please install Trivy manually.${NC}"
            return 1
        fi
    fi
    
    echo -e "${YELLOW}Scanning filesystem...${NC}"
    trivy fs . --severity HIGH,CRITICAL --format table
    
    echo -e "${YELLOW}Scanning Docker images...${NC}"
    
    # Build images if they don't exist
    if ! docker images | grep -q "golf_app.*frontend"; then
        echo -e "${YELLOW}Building frontend image for scanning...${NC}"
        docker build -t golf_app_frontend ./frontend
    fi
    
    if ! docker images | grep -q "golf_app.*backend"; then
        echo -e "${YELLOW}Building backend image for scanning...${NC}"
        docker build -t golf_app_backend ./backend
    fi
    
    # Scan images
    trivy image golf_app_frontend --severity HIGH,CRITICAL
    trivy image golf_app_backend --severity HIGH,CRITICAL
    
    echo -e "${GREEN}✅ Trivy scan completed${NC}"
}

# Function to run Docker security scan
run_docker_scan() {
    echo -e "${YELLOW}Running Docker security scan...${NC}"
    
    if command_exists docker; then
        # Check for Docker security best practices
        echo -e "${YELLOW}Checking Docker security best practices...${NC}"
        
        # Check if running as root
        if docker-compose exec -T backend whoami 2>/dev/null | grep -q root; then
            echo -e "${RED}❌ Backend container running as root${NC}"
        else
            echo -e "${GREEN}✅ Backend container not running as root${NC}"
        fi
        
        if docker-compose exec -T frontend whoami 2>/dev/null | grep -q root; then
            echo -e "${RED}❌ Frontend container running as root${NC}"
        else
            echo -e "${GREEN}✅ Frontend container not running as root${NC}"
        fi
        
        # Check for exposed ports
        echo -e "${YELLOW}Checking exposed ports...${NC}"
        docker-compose ps --format "table {{.Name}}\t{{.Ports}}"
        
        # Check for mounted volumes
        echo -e "${YELLOW}Checking mounted volumes...${NC}"
        docker-compose config --volumes
        
        echo -e "${GREEN}✅ Docker security scan completed${NC}"
    else
        echo -e "${RED}❌ Docker not found${NC}"
    fi
}

# Function to run dependency scan
run_dependency_scan() {
    echo -e "${YELLOW}Running dependency vulnerability scan...${NC}"
    
    # Backend dependencies (Maven)
    if [ -f "backend/pom.xml" ]; then
        echo -e "${YELLOW}Scanning backend dependencies...${NC}"
        cd backend
        if command_exists mvn; then
            mvn org.owasp:dependency-check-maven:check || true
        else
            echo -e "${YELLOW}⚠️  Maven not found, skipping backend dependency scan${NC}"
        fi
        cd ..
    fi
    
    # Frontend dependencies (npm)
    if [ -f "frontend/package.json" ]; then
        echo -e "${YELLOW}Scanning frontend dependencies...${NC}"
        cd frontend
        if command_exists npm; then
            npm audit --audit-level=moderate || true
        else
            echo -e "${YELLOW}⚠️  npm not found, skipping frontend dependency scan${NC}"
        fi
        cd ..
    fi
    
    echo -e "${GREEN}✅ Dependency scan completed${NC}"
}

# Function to run secrets scan
run_secrets_scan() {
    echo -e "${YELLOW}Running secrets scan...${NC}"
    
    # Check for common secrets patterns
    echo -e "${YELLOW}Checking for hardcoded secrets...${NC}"
    
    # Patterns to look for
    declare -a patterns=(
        "password\s*=\s*['\"][^'\"]*['\"]"
        "api_key\s*=\s*['\"][^'\"]*['\"]"
        "secret\s*=\s*['\"][^'\"]*['\"]"
        "token\s*=\s*['\"][^'\"]*['\"]"
        "key\s*=\s*['\"][^'\"]*['\"]"
    )
    
    SECRETS_FOUND=0
    
    for pattern in "${patterns[@]}"; do
        if grep -r -i -E "$pattern" --include="*.java" --include="*.js" --include="*.vue" --include="*.properties" --include="*.yml" --include="*.yaml" .; then
            SECRETS_FOUND=1
        fi
    done
    
    if [ $SECRETS_FOUND -eq 0 ]; then
        echo -e "${GREEN}✅ No hardcoded secrets found${NC}"
    else
        echo -e "${RED}❌ Potential hardcoded secrets found${NC}"
    fi
    
    # Check for .env files in version control
    if find . -name ".env" -not -path "./node_modules/*" -not -path "./.git/*" | grep -q ".env"; then
        echo -e "${RED}❌ .env files found in repository${NC}"
    else
        echo -e "${GREEN}✅ No .env files in repository${NC}"
    fi
    
    echo -e "${GREEN}✅ Secrets scan completed${NC}"
}

# Function to run configuration scan
run_config_scan() {
    echo -e "${YELLOW}Running configuration security scan...${NC}"
    
    # Check Docker Compose security
    echo -e "${YELLOW}Checking Docker Compose security...${NC}"
    
    # Check for privileged containers
    if grep -q "privileged.*true" docker-compose*.yml; then
        echo -e "${RED}❌ Privileged containers found${NC}"
    else
        echo -e "${GREEN}✅ No privileged containers${NC}"
    fi
    
    # Check for host network mode
    if grep -q "network_mode.*host" docker-compose*.yml; then
        echo -e "${RED}❌ Host network mode found${NC}"
    else
        echo -e "${GREEN}✅ No host network mode${NC}"
    fi
    
    # Check for bind mounts to sensitive directories
    if grep -q "/etc\|/var\|/usr" docker-compose*.yml; then
        echo -e "${YELLOW}⚠️  Bind mounts to system directories found${NC}"
    else
        echo -e "${GREEN}✅ No suspicious bind mounts${NC}"
    fi
    
    echo -e "${GREEN}✅ Configuration scan completed${NC}"
}

# Main execution
echo -e "${YELLOW}Starting comprehensive security scan...${NC}"
echo ""

# Run all scans
run_trivy_scan
echo ""
run_docker_scan
echo ""
run_dependency_scan
echo ""
run_secrets_scan
echo ""
run_config_scan

echo ""
echo "=========================="
echo -e "${GREEN}🎉 Security scan completed!${NC}"
echo ""
echo -e "${YELLOW}Next steps:${NC}"
echo "1. Review any vulnerabilities found above"
echo "2. Update dependencies with known vulnerabilities"
echo "3. Fix any configuration issues"
echo "4. Run this scan regularly as part of your CI/CD pipeline"