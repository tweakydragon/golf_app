# Golf App Docker & DevOps Infrastructure

This document provides comprehensive information about the Docker infrastructure and deployment strategies for the Golf App.

## 🏗️ Architecture Overview

The Golf App uses a containerized microservices architecture with the following components:

- **Frontend**: Vue.js application served by Nginx
- **Backend**: Spring Boot application with Java 21
- **Database**: PostgreSQL 16 with automated backups
- **Cache**: Redis for session storage and caching
- **Monitoring**: Prometheus + Grafana for metrics and alerting
- **Email**: MailHog for development email testing

## 📁 Project Structure

```
golf_app/
├── backend/
│   ├── Dockerfile              # Production backend image
│   ├── Dockerfile.dev          # Development backend image
│   └── src/                    # Spring Boot application source
├── frontend/
│   ├── Dockerfile              # Production frontend image
│   ├── Dockerfile.dev          # Development frontend image
│   ├── nginx.conf              # Nginx configuration
│   └── src/                    # Vue.js application source
├── database/
│   ├── init/                   # Database initialization scripts
│   └── backups/                # Database backup storage
├── monitoring/
│   ├── prometheus.yml          # Prometheus configuration
│   └── grafana/                # Grafana dashboards and config
├── scripts/
│   ├── deploy.sh               # Deployment script
│   ├── backup-database.sh      # Database backup script
│   └── restore-database.sh     # Database restore script
├── .github/workflows/
│   └── ci-cd.yml               # GitHub Actions CI/CD pipeline
├── docker-compose.yml          # Basic production setup
├── docker-compose.dev.yml      # Development environment
├── docker-compose.prod.yml     # Full production setup
├── .env.template               # Environment variables template
└── .dockerignore               # Docker build context exclusions
```

## 🚀 Quick Start

### Development Environment

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd golf_app
   ```

2. **Start development environment**
   ```bash
   ./scripts/deploy.sh development
   ```

3. **Access the application**
   - Frontend: http://localhost:5173
   - Backend API: http://localhost:8080
   - API Documentation: http://localhost:8080/swagger-ui.html
   - Database: localhost:5432
   - MailHog UI: http://localhost:8025

### Production Environment

1. **Configure environment variables**
   ```bash
   cp .env.template .env
   # Edit .env with your production values
   ```

2. **Deploy to production**
   ```bash
   ./scripts/deploy.sh production --backup
   ```

3. **Access the application**
   - Frontend: http://localhost:80
   - Backend API: http://localhost:8080
   - Monitoring: http://localhost:3000 (Grafana)
   - Metrics: http://localhost:9090 (Prometheus)

## 🐳 Docker Images

### Frontend Image Features

- **Multi-stage build**: Optimized for production
- **Security**: Non-root user, minimal attack surface
- **Performance**: Nginx with gzip compression
- **Caching**: Static asset optimization
- **Health checks**: Automated health monitoring

### Backend Image Features

- **Multi-stage build**: Separate build and runtime environments
- **JVM optimization**: Container-aware JVM settings
- **Security**: Non-root user, minimal base image
- **Monitoring**: Built-in health checks and metrics
- **Performance**: G1 garbage collector, optimized memory usage

### Database Setup

- **Version**: PostgreSQL 16 Alpine
- **Persistence**: Named volumes for data persistence
- **Initialization**: Automated schema creation
- **Backups**: Automated backup and restore scripts
- **Health checks**: Connection monitoring

## 🔧 Configuration

### Environment Variables

#### Database Configuration
```env
POSTGRES_DB=golfdb
POSTGRES_USER=postgres
POSTGRES_PASSWORD=your_secure_password
POSTGRES_PORT=5432
```

#### Application Configuration
```env
BACKEND_PORT=8080
FRONTEND_PORT=80
SPRING_PROFILES_ACTIVE=production
JAVA_OPTS=-Xmx512m -Xms256m
```

#### Monitoring Configuration
```env
PROMETHEUS_PORT=9090
GRAFANA_PORT=3000
GRAFANA_ADMIN_PASSWORD=your_secure_password
```

### Security Configuration

- **Non-root users**: All containers run as non-root users
- **Minimal base images**: Alpine Linux for smaller attack surface
- **Resource limits**: CPU and memory constraints
- **Network isolation**: Separate networks for different environments
- **Secret management**: Environment variables for sensitive data

## 🔄 CI/CD Pipeline

### GitHub Actions Workflow

The CI/CD pipeline includes:

1. **Testing Phase**
   - Backend unit tests with PostgreSQL
   - Frontend unit tests with coverage
   - Security scanning with Trivy

2. **Build Phase**
   - Multi-architecture Docker builds
   - Image optimization and caching
   - Container registry publishing

3. **Deploy Phase**
   - Staging deployment (develop branch)
   - Production deployment (main branch)
   - Automated health checks

### Pipeline Configuration

```yaml
# .github/workflows/ci-cd.yml
name: Golf App CI/CD Pipeline
on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]
```

## 📊 Monitoring & Observability

### Prometheus Metrics

- **Application metrics**: Custom business metrics
- **JVM metrics**: Memory, GC, threads
- **Database metrics**: Connection pool, query performance
- **System metrics**: CPU, memory, disk usage

### Grafana Dashboards

- **Application performance**: Response times, throughput
- **Infrastructure monitoring**: Resource utilization
- **Business metrics**: User activities, data insights
- **Alert management**: Automated alerting rules

### Health Checks

All services include comprehensive health checks:

- **Database**: Connection and query tests
- **Backend**: Spring Boot Actuator endpoints
- **Frontend**: HTTP response validation
- **Cache**: Redis ping tests

## 🔒 Security Best Practices

### Container Security

- **Non-root execution**: All processes run as non-privileged users
- **Minimal images**: Alpine Linux base images
- **Layer optimization**: Efficient Docker layer caching
- **Secret management**: Environment-based configuration

### Network Security

- **Network isolation**: Separate networks for different environments
- **Port exposure**: Only necessary ports exposed
- **Internal communication**: Services communicate via internal networks
- **SSL/TLS**: HTTPS termination at load balancer

### Data Security

- **Database encryption**: At-rest and in-transit encryption
- **Backup security**: Encrypted database backups
- **Access control**: Role-based database access
- **Audit logging**: Comprehensive activity logging

## 🔧 Maintenance

### Database Backups

#### Automated Backups
```bash
# Create backup
./scripts/backup-database.sh

# Restore from backup
./scripts/restore-database.sh database/backups/golfdb_backup_20240101_120000.sql.gz
```

#### Backup Schedule
- **Development**: Manual backups
- **Production**: Daily automated backups
- **Retention**: 7 days for development, 30 days for production

### Log Management

- **Centralized logging**: JSON format for structured logging
- **Log rotation**: Automatic log file rotation
- **Log retention**: 10MB max size, 3 files per service
- **Log aggregation**: ELK stack integration ready

### Updates and Maintenance

#### Regular Updates
```bash
# Update development environment
./scripts/deploy.sh development --build

# Update production with backup
./scripts/deploy.sh production --backup --build
```

#### Security Updates
- **Base image updates**: Monthly security patches
- **Dependency updates**: Automated dependency scanning
- **Vulnerability scanning**: Continuous security monitoring

## 🚨 Troubleshooting

### Common Issues

#### Container Won't Start
```bash
# Check container logs
docker-compose logs <service-name>

# Check container status
docker-compose ps

# Restart specific service
docker-compose restart <service-name>
```

#### Database Connection Issues
```bash
# Check database health
docker-compose exec db pg_isready -U postgres

# Check database logs
docker-compose logs db

# Connect to database
docker-compose exec db psql -U postgres -d golfdb
```

#### Performance Issues
```bash
# Check resource usage
docker stats

# Check service health
curl http://localhost:8080/actuator/health

# Check database performance
docker-compose exec db psql -U postgres -d golfdb -c "SELECT * FROM pg_stat_activity;"
```

### Monitoring Alerts

#### High CPU Usage
- **Threshold**: > 80% for 5 minutes
- **Action**: Scale horizontally or investigate performance bottlenecks

#### Memory Issues
- **Threshold**: > 90% memory usage
- **Action**: Increase memory limits or optimize application

#### Database Performance
- **Threshold**: Query time > 1 second
- **Action**: Optimize queries or add indexes

## 📈 Performance Optimization

### Frontend Optimization

- **Asset compression**: Gzip compression enabled
- **Caching**: Browser and CDN caching headers
- **Bundle optimization**: Code splitting and lazy loading
- **Image optimization**: WebP format and responsive images

### Backend Optimization

- **JVM tuning**: Optimized garbage collection
- **Connection pooling**: Database connection optimization
- **Caching**: Redis integration for session and data caching
- **Async processing**: Non-blocking I/O operations

### Database Optimization

- **Indexing**: Optimized database indexes
- **Query optimization**: Efficient SQL queries
- **Connection pooling**: HikariCP configuration
- **Partitioning**: Table partitioning for large datasets

## 🔗 Additional Resources

- [Docker Documentation](https://docs.docker.com/)
- [Spring Boot Docker Guide](https://spring.io/guides/topicals/spring-boot-docker/)
- [Vue.js Production Deployment](https://vuejs.org/guide/best-practices/production-deployment.html)
- [PostgreSQL Docker Hub](https://hub.docker.com/_/postgres)
- [Prometheus Configuration](https://prometheus.io/docs/prometheus/latest/configuration/configuration/)
- [Grafana Documentation](https://grafana.com/docs/)

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.