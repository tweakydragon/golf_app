# Golf App Makefile
# Provides convenient commands for development and deployment

.PHONY: help dev prod staging build test clean backup restore health logs

# Colors for output
YELLOW := \033[1;33m
GREEN := \033[0;32m
RED := \033[0;31m
NC := \033[0m # No Color

# Default target
help: ## Show this help message
	@echo "$(YELLOW)🏌️ Golf App Development Commands$(NC)"
	@echo "=================================="
	@echo ""
	@echo "Available commands:"
	@echo ""
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "  $(GREEN)%-15s$(NC) %s\n", $$1, $$2}'
	@echo ""
	@echo "Examples:"
	@echo "  make dev          # Start development environment"
	@echo "  make prod         # Start production environment"
	@echo "  make test         # Run all tests"
	@echo "  make backup       # Create database backup"
	@echo "  make health       # Check service health"

# Development environment
dev: ## Start development environment
	@echo "$(YELLOW)🚀 Starting development environment...$(NC)"
	@./scripts/deploy.sh development

dev-build: ## Start development environment with rebuild
	@echo "$(YELLOW)🔨 Starting development environment with rebuild...$(NC)"
	@./scripts/deploy.sh development --build

dev-logs: ## Show development logs
	@echo "$(YELLOW)📋 Showing development logs...$(NC)"
	@docker-compose -f docker-compose.dev.yml logs -f

dev-stop: ## Stop development environment
	@echo "$(YELLOW)🛑 Stopping development environment...$(NC)"
	@docker-compose -f docker-compose.dev.yml down

dev-restart: ## Restart development environment
	@echo "$(YELLOW)🔄 Restarting development environment...$(NC)"
	@docker-compose -f docker-compose.dev.yml restart

# Production environment
prod: ## Start production environment
	@echo "$(YELLOW)🚀 Starting production environment...$(NC)"
	@./scripts/deploy.sh production

prod-build: ## Start production environment with rebuild
	@echo "$(YELLOW)🔨 Starting production environment with rebuild...$(NC)"
	@./scripts/deploy.sh production --build

prod-logs: ## Show production logs
	@echo "$(YELLOW)📋 Showing production logs...$(NC)"
	@docker-compose -f docker-compose.prod.yml logs -f

prod-stop: ## Stop production environment
	@echo "$(YELLOW)🛑 Stopping production environment...$(NC)"
	@docker-compose -f docker-compose.prod.yml down

prod-restart: ## Restart production environment
	@echo "$(YELLOW)🔄 Restarting production environment...$(NC)"
	@docker-compose -f docker-compose.prod.yml restart

# Staging environment
staging: ## Start staging environment
	@echo "$(YELLOW)🚀 Starting staging environment...$(NC)"
	@./scripts/deploy.sh staging

# Build commands
build: ## Build all Docker images
	@echo "$(YELLOW)🔨 Building all Docker images...$(NC)"
	@docker-compose -f docker-compose.dev.yml build
	@docker-compose -f docker-compose.prod.yml build

build-frontend: ## Build frontend image
	@echo "$(YELLOW)🔨 Building frontend image...$(NC)"
	@docker-compose -f docker-compose.dev.yml build frontend

build-backend: ## Build backend image
	@echo "$(YELLOW)🔨 Building backend image...$(NC)"
	@docker-compose -f docker-compose.dev.yml build backend

build-no-cache: ## Build all images without cache
	@echo "$(YELLOW)🔨 Building all images without cache...$(NC)"
	@docker-compose -f docker-compose.dev.yml build --no-cache
	@docker-compose -f docker-compose.prod.yml build --no-cache

# Testing commands
test: ## Run all tests
	@echo "$(YELLOW)🧪 Running all tests...$(NC)"
	@$(MAKE) test-backend
	@$(MAKE) test-frontend

test-backend: ## Run backend tests
	@echo "$(YELLOW)🧪 Running backend tests...$(NC)"
	@cd backend && ./mvnw test

test-frontend: ## Run frontend tests
	@echo "$(YELLOW)🧪 Running frontend tests...$(NC)"
	@cd frontend && npm test

test-coverage: ## Run tests with coverage
	@echo "$(YELLOW)📊 Running tests with coverage...$(NC)"
	@cd frontend && npm run test:coverage

# Database commands
backup: ## Create database backup
	@echo "$(YELLOW)💾 Creating database backup...$(NC)"
	@./scripts/backup-database.sh

restore: ## Restore database from backup (Usage: make restore FILE=backup.sql)
	@echo "$(YELLOW)🔄 Restoring database from backup...$(NC)"
	@if [ -z "$(FILE)" ]; then \
		echo "$(RED)❌ Please specify backup file: make restore FILE=backup.sql$(NC)"; \
		exit 1; \
	fi
	@./scripts/restore-database.sh $(FILE)

db-connect: ## Connect to database
	@echo "$(YELLOW)🗄️ Connecting to database...$(NC)"
	@docker-compose exec db psql -U postgres -d golfdb

db-logs: ## Show database logs
	@echo "$(YELLOW)📋 Showing database logs...$(NC)"
	@docker-compose logs db

# Health and monitoring
health: ## Check service health
	@echo "$(YELLOW)🏥 Checking service health...$(NC)"
	@./scripts/health-check.sh

status: ## Show service status
	@echo "$(YELLOW)📊 Service status:$(NC)"
	@docker-compose ps

logs: ## Show logs for all services
	@echo "$(YELLOW)📋 Showing logs for all services...$(NC)"
	@docker-compose logs

logs-f: ## Follow logs for all services
	@echo "$(YELLOW)📋 Following logs for all services...$(NC)"
	@docker-compose logs -f

# Maintenance commands
clean: ## Clean up Docker resources
	@echo "$(YELLOW)🧹 Cleaning up Docker resources...$(NC)"
	@docker system prune -f
	@docker volume prune -f

clean-all: ## Clean up all Docker resources (including images)
	@echo "$(YELLOW)🧹 Cleaning up all Docker resources...$(NC)"
	@docker system prune -af
	@docker volume prune -f

# GitHub Actions management
enable-actions: ## Enable GitHub Actions CI/CD pipeline
	@echo "$(YELLOW)🔄 Enabling GitHub Actions...$(NC)"
	@./scripts/enable-github-actions.sh

disable-actions: ## Disable GitHub Actions CI/CD pipeline
	@echo "$(YELLOW)⏸️  Disabling GitHub Actions...$(NC)"
	@./scripts/disable-github-actions.sh

restart: ## Restart all services
	@echo "$(YELLOW)🔄 Restarting all services...$(NC)"
	@docker-compose restart

stop: ## Stop all services
	@echo "$(YELLOW)🛑 Stopping all services...$(NC)"
	@docker-compose down

# Environment setup
env: ## Create .env file from template
	@echo "$(YELLOW)⚙️ Creating .env file from template...$(NC)"
	@if [ ! -f .env ]; then \
		cp .env.template .env; \
		echo "$(GREEN)✅ .env file created. Please edit it with your configuration.$(NC)"; \
	else \
		echo "$(RED)❌ .env file already exists.$(NC)"; \
	fi

# Security commands
scan: ## Run security scan
	@echo "$(YELLOW)🔍 Running security scan...$(NC)"
	@docker run --rm -v $(PWD):/app -w /app aquasec/trivy fs .

# Performance commands
stats: ## Show Docker resource usage
	@echo "$(YELLOW)📊 Docker resource usage:$(NC)"
	@docker stats --no-stream

# Documentation
docs: ## Open documentation
	@echo "$(YELLOW)📚 Opening documentation...$(NC)"
	@open DOCKER-DEPLOYMENT.md || xdg-open DOCKER-DEPLOYMENT.md || echo "Please open DOCKER-DEPLOYMENT.md manually"

# Development utilities
shell-backend: ## Shell into backend container
	@echo "$(YELLOW)🐚 Opening shell in backend container...$(NC)"
	@docker-compose exec backend sh

shell-frontend: ## Shell into frontend container
	@echo "$(YELLOW)🐚 Opening shell in frontend container...$(NC)"
	@docker-compose exec frontend sh

shell-db: ## Shell into database container
	@echo "$(YELLOW)🐚 Opening shell in database container...$(NC)"
	@docker-compose exec db sh

# Git hooks
pre-commit: ## Run pre-commit checks
	@echo "$(YELLOW)✅ Running pre-commit checks...$(NC)"
	@$(MAKE) test
	@$(MAKE) scan

# Quick commands
up: dev ## Alias for dev
down: stop ## Alias for stop
rebuild: build ## Alias for build