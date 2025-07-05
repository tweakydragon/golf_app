# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a full-stack golf swing analysis application that processes CSV data from launch monitors (Garmin R10 and Awesome Golf) and provides detailed visualizations and analytics. The application uses:

- **Frontend**: Vue.js 3 with Vite, Bootstrap 5, Chart.js for visualizations
- **Backend**: Java Spring Boot with JPA/Hibernate, REST API
- **Database**: PostgreSQL with comprehensive shot data schema
- **Containerization**: Docker with separate dev/prod configurations

## Development Commands

### Quick Start
```bash
# Start entire development environment with live reloading
./dev.sh

# Access at:
# Frontend: http://localhost:5173
# Backend: http://localhost:8080
```

### Frontend Commands
```bash
cd frontend
npm install          # Install dependencies
npm run dev          # Start Vite dev server (port 5173)
npm run build        # Build for production
npm run preview      # Preview production build
```

### Backend Commands
```bash
cd backend
./mvnw spring-boot:run    # Run Spring Boot application
./mvnw test              # Run tests
./mvnw clean package     # Build JAR
```

### Docker Commands
```bash
# Development with live reloading
docker-compose -f docker-compose.dev.yml up --build

# Production deployment
docker-compose up --build

# Stop development environment
docker-compose -f docker-compose.dev.yml down
```

## Architecture & Code Organization

### Frontend Structure (`/frontend/src/`)
- `components/charts/` - 11 specialized chart components for golf analytics
- `components/` - Main Vue components (Home, SessionView, UploadCSV, ShotDetailModal)
- `router/` - Vue Router configuration
- Uses Canvas API for interactive shot visualizations with zoom/pan controls

### Backend Structure (`/backend/src/main/java/com/example/demo/`)
- `controller/` - REST API endpoints
- `model/` - JPA entities (Session, Shot)
- `repository/` - Data access layer
- `service/` - Business logic for CSV parsing and data processing

### Key Features
- **CSV Processing**: Robust parsing for both Garmin R10 and Awesome Golf formats
- **Interactive Visualizations**: Canvas-based shot trajectory and dispersion charts
- **Live Reloading**: Full development environment with hot reloading
- **Data Analytics**: 40+ shot metrics including advanced ball flight data

## Data Model

### Session Entity
- Stores session metadata (title, location, date, source type)
- One-to-many relationship with shots

### Shot Entity
- Comprehensive shot data (40+ fields)
- Supports both basic metrics (ball speed, distance) and advanced metrics (smash factor, spin axis)
- Handles different data formats from various launch monitors

## Development Workflow

1. **Environment Setup**: Always use `./dev.sh` for development
2. **File Watching**: Both frontend and backend have live reloading configured
3. **Database**: PostgreSQL with sample data loaded automatically
4. **Testing**: Basic Spring Boot test structure in place

## Code Style & Patterns

### Frontend
- Vue 3 Composition API preferred
- Canvas-based visualizations with proper scaling
- Interactive controls (zoom 0.5x-3x, pan, view toggles)
- Responsive design with Bootstrap 5
- File organization: Keep charts in `components/charts/`

### Backend
- Spring Boot conventions with proper REST endpoints
- JPA entity relationships for data modeling
- Robust CSV parsing with error handling
- DevTools enabled for development hot reloading

### Visualization Guidelines
- Use realistic physics for ball flight path calculations
- Include 50-yard distance markers for scale
- Implement mouse hover highlighting across related UI elements
- Support both side view and top view for shot analysis
- Progressive disclosure: overview → detailed analysis

## Environment Configuration

### Development
- Frontend: Vite dev server with HMR on port 5173
- Backend: Spring Boot with DevTools on port 8080
- Database: PostgreSQL on port 5432
- Docker volumes for live file watching

### Production
- Frontend: Nginx serving static build
- Backend: Spring Boot JAR
- Database: PostgreSQL with persistent storage

## Key Files
- `docker-compose.dev.yml` - Development environment
- `dev.sh` - Development startup script
- `frontend/vite.config.js` - Vite configuration with dev server settings
- `backend/pom.xml` - Maven dependencies including DevTools
- `database/init/` - Database schema and test data
- `sample_data/` - Sample CSV files for testing

## Common Development Tasks

### Adding New Chart Components
1. Create in `frontend/src/components/charts/`
2. Follow canvas-based pattern with proper scaling
3. Include interactive controls (zoom, pan, hover)
4. Add to main chart collection in SessionView

### Extending CSV Support
1. Update parsing logic in backend service layer
2. Add new fields to Shot entity if needed
3. Test with sample data files
4. Update frontend visualizations to use new data

### Database Changes
1. Modify JPA entities in backend model
2. Update initialization scripts in `database/init/`
3. Restart development environment to apply changes

## Testing

### Backend
- Spring Boot tests using JUnit 5
- Run with `./mvnw test`
- Basic context loading test in place

### Frontend
- No specific test framework configured yet
- Manual testing through development environment

## Sample Data

Sample CSV files are provided in `sample_data/` directory for testing upload functionality without requiring actual launch monitor data.

## Development Guidelines

- **Think deeply**: Consider the full context and implications of changes before implementing
- **Responsive design**: Always consider screen sizes from mobile to desktop when making UI changes
  - Test layouts on different viewport sizes
  - Use Bootstrap's responsive utilities and grid system
  - Ensure card displays work well on small screens
  - Consider touch interactions for mobile users
- **Code readability and documentation**: Write code as if handing it off to an intern
  - Add comments explaining the purpose and function of code blocks
  - Use descriptive variable and method names
  - Structure code for clarity over cleverness
  - Document complex business logic and API integrations
  - Explain "why" not just "what" in comments

## Documentation Guidelines

- Keep the README file updated with:
  - Comprehensive project descriptions
  - Detailed explanations of sub-components
  - Function descriptions within each component
  - A section highlighting required software and installation sources

## Communication Guidelines

- In responses and explanations, be verbose. Imagine you are a college professor explaining the response to a student
  - Provide comprehensive, detailed explanations
  - Break down complex concepts into digestible parts
  - Use analogies and real-world examples to illustrate points
  - Encourage critical thinking and deeper understanding