# Development Guide

This guide covers how to set up and run the Golf App in development mode with live reloading.

## Development Environment Features

- **Frontend Live Reloading**: Vue.js with Vite hot module replacement (HMR)
- **Backend Live Reloading**: Spring Boot DevTools with automatic restart
- **Database**: PostgreSQL with persistent data during development
- **File Watching**: Automatic code change detection in containers
- **CORS Configuration**: Properly configured for frontend-backend communication

## Quick Start

### Option 1: Using the Development Script (Recommended)

```bash
# Make the script executable (if not already done)
chmod +x dev.sh

# Start the development environment
./dev.sh
```

### Option 2: Manual Docker Compose

```bash
# Start development environment
docker-compose -f docker-compose.dev.yml up --build

# Or run in detached mode
docker-compose -f docker-compose.dev.yml up --build -d
```

## Development URLs

- **Frontend (Vue.js)**: http://localhost:5173
- **Backend API**: http://localhost:8080
- **Database**: localhost:5432
- **LiveReload Port**: 35729 (for backend hot reloading)

## File Structure for Development

```
golf_app/
├── docker-compose.dev.yml    # Development Docker Compose
├── dev.sh                    # Development startup script
├── frontend/
│   ├── Dockerfile.dev        # Frontend development container
│   ├── vite.config.js        # Updated with dev server config
│   └── src/                  # Frontend source (live-reloaded)
├── backend/
│   ├── Dockerfile.dev        # Backend development container
│   ├── pom.xml               # Updated with DevTools dependency
│   └── src/                  # Backend source (live-reloaded)
└── database/
    └── init/                 # Database initialization scripts
```

## Live Reloading Details

### Frontend (Vue.js + Vite)
- **File Watching**: Vite watches all files in `/src` directory
- **Hot Module Replacement**: Instant updates without page refresh
- **Polling**: Enabled for Docker container compatibility
- **Port**: 5173 (Vite development server)

### Backend (Spring Boot)
- **Spring Boot DevTools**: Automatically restarts when Java classes change
- **LiveReload**: Browser extension support on port 35729
- **File Watching**: Monitors `/src/main/java` for changes
- **Hot Swapping**: Some changes don't require full restart

### Database
- **Persistent Data**: Uses Docker volume `pgdata`
- **Initialization**: Runs scripts from `/database/init` on first start
- **Connection**: Available at localhost:5432

## Development Workflow

1. **Start Environment**:
   ```bash
   ./dev.sh
   ```

2. **Make Changes**:
   - Edit files in `frontend/src/` → Instant browser update
   - Edit files in `backend/src/` → Automatic application restart
   - Database changes → Restart containers if needed

3. **View Logs**:
   ```bash
   # View all logs
   docker-compose -f docker-compose.dev.yml logs -f
   
   # View specific service logs
   docker-compose -f docker-compose.dev.yml logs -f frontend
   docker-compose -f docker-compose.dev.yml logs -f backend
   ```

4. **Stop Environment**:
   - Press `Ctrl+C` if using `./dev.sh`
   - Or: `docker-compose -f docker-compose.dev.yml down`

## Troubleshooting

### Frontend Issues

**Port 5173 already in use:**
```bash
# Kill any process using port 5173
lsof -ti:5173 | xargs kill -9
```

**Hot reloading not working:**
- Ensure file watching is enabled in Vite config
- Check that source files are properly mounted in container
- Verify `CHOKIDAR_USEPOLLING=true` environment variable

### Backend Issues

**Port 8080 already in use:**
```bash
# Kill any process using port 8080
lsof -ti:8080 | xargs kill -9
```

**DevTools not restarting:**
- Check that Spring Boot DevTools is included in dependencies
- Verify source files are mounted in container
- Look for compilation errors in logs

### Database Issues

**Connection refused:**
- Wait for database health check to pass
- Check that database container is running
- Verify connection string in backend configuration

### General Docker Issues

**Containers not updating:**
```bash
# Rebuild containers
docker-compose -f docker-compose.dev.yml build --no-cache

# Clean up old containers and images
docker-compose -f docker-compose.dev.yml down
docker system prune -f
```

**Permission issues on Linux:**
```bash
# Fix file permissions
sudo chown -R $USER:$USER .
```

## Performance Tips

1. **Use `.dockerignore`**: Exclude `node_modules`, `target`, etc.
2. **Limit File Watching**: Only mount necessary source directories
3. **Use Docker Layer Caching**: Keep dependency installation separate from source code
4. **Monitor Resource Usage**: Use `docker stats` to check container performance

## Switching Between Environments

### Development → Production
```bash
# Stop development
docker-compose -f docker-compose.dev.yml down

# Start production
docker-compose up --build
```

### Production → Development
```bash
# Stop production
docker-compose down

# Start development
./dev.sh
```

## Environment Variables

### Frontend Development
- `CHOKIDAR_USEPOLLING=true`: Enable file watching in Docker
- `WATCHPACK_POLLING=true`: Enable webpack polling

### Backend Development
- `SPRING_PROFILES_ACTIVE=dev`: Activate development profile
- `SPRING_DEVTOOLS_RESTART_ENABLED=true`: Enable auto-restart
- `SPRING_DEVTOOLS_LIVERELOAD_ENABLED=true`: Enable LiveReload

## IDE Integration

### VS Code
1. Install Docker extension
2. Install Vue.js extensions
3. Install Spring Boot extensions
4. Configure workspace to watch files in containers

### IntelliJ IDEA
1. Configure Docker integration
2. Set up Spring Boot run configuration
3. Enable auto-import for Maven dependencies
4. Configure file synchronization

## Next Steps

- Add hot reloading for CSS/SCSS changes
- Implement test file watching and auto-running
- Add code linting and formatting on save
- Set up debugging ports for both frontend and backend