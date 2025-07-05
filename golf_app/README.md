# Golf Swing Analysis App

This project is a full-stack application for analyzing golf swing data. It features:

- **Backend:** Java Spring Boot API for uploading and parsing CSV data from both Garmin R10 and Awesome Golf launch monitors, storing it in a local PostgreSQL database, and serving REST endpoints.
- **Frontend:** Vue.js (Vite) app for uploading CSVs, visualizing aggregate and per-shot data with infographics, and drilling down into individual shots.

## Getting Started

### 🚀 Quick Start (Development with Live Reloading)

```bash
# Start development environment with live reloading
./dev.sh
```

Access the application:
- **Frontend**: http://localhost:5173 (with hot reloading)
- **Backend API**: http://localhost:8080
- **Database**: localhost:5432

### 📋 Manual Setup Options

#### Development Environment
```bash
# Start with live reloading
docker-compose -f docker-compose.dev.yml up --build

# Access at:
# Frontend: http://localhost:5173
# Backend: http://localhost:8080
```

#### Production Environment
```bash
# Start production build
docker-compose up --build

# Access at: http://localhost
```

#### Local Development (without Docker)

**Frontend:**
1. `cd frontend`
2. `npm install`
3. `npm run dev`

**Backend:**
1. `cd backend`
2. `./mvnw spring-boot:run`

**Database:**
- Use Docker: `docker-compose up db`
- Or install PostgreSQL locally and ensure it's running on port 5432

---

## Features
- Upload CSV files from Garmin R10 and Awesome Golf launch monitors
- Store and retrieve shot data with complete metrics
- Visualize data with infographics
- Drill down into individual shots
- Compare performance across different sessions

## CSV Upload
The application allows users to upload their golf launch monitor CSV data files for analysis:

1. Navigate to the **Upload CSV** page from the main menu
2. Enter a title for the session (required)
3. Enter a location (optional)
4. Select the data source type (Garmin R10 or Awesome Golf)
5. Select your CSV file
6. Click "Upload & Analyze"

Once uploaded, your data will be permanently stored in the database and available for future analysis. 
The application will automatically parse your shot data including:

### Common Metrics (Both Sources)
- Ball speed
- Club head speed
- Launch angles
- Spin rates
- Carry and total distances

### Extended Metrics (Awesome Golf)
- Smash factor
- Roll distance
- Peak height
- Descent angle
- Shot classification
- And many more advanced club and ball metrics

## Data Analysis
Each session includes:
- Overall session statistics
- Club-by-club breakdown of performance
- Individual shot details
- Advanced metrics and analytics

---

## Development

### Technology Stack
- **Frontend**: Vue.js (Vite) with live reloading
- **Backend**: Java Spring Boot with DevTools
- **Database**: PostgreSQL
- **Containerization**: Docker & Docker Compose

### Development Features
- 🔥 **Live Reloading**: Instant updates for both frontend and backend
- 🎯 **Hot Module Replacement**: Vue.js components update without page refresh
- 🔄 **Auto Restart**: Spring Boot automatically restarts on Java changes
- 📊 **Enhanced Shot Visualizations**: Interactive flight path analysis with zoom and pan
- 🎮 **Easy Setup**: One command to start the entire development environment

### Development Setup
For detailed development instructions, see [DEVELOPMENT.md](./DEVELOPMENT.md)

```bash
# Quick start development environment
./dev.sh
```

### Test Data
The application comes with sample test data that is automatically loaded into the database on startup when using Docker Compose. This data includes:
- A sample practice session with shots from various clubs
- Test data representing typical metrics from a Garmin R10 launch monitor

You can modify the test data by editing the files in the `database/init/` directory.

### Sample Data Files
Sample data files for testing are located in the `sample_data/` directory:
- `ags-shots-2025-05-14.csv` - An example Awesome Golf CSV export with various driver shots

These sample files can be used to test the CSV upload functionality without requiring actual launch monitor data.
