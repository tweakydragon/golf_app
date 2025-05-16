# Golf Swing Analysis App

This project is a full-stack application for analyzing golf swing data. It features:

- **Backend:** Java Spring Boot API for uploading and parsing CSV data from both Garmin R10 and Awesome Golf launch monitors, storing it in a local PostgreSQL database, and serving REST endpoints.
- **Frontend:** Vue.js (Vite) app for uploading CSVs, visualizing aggregate and per-shot data with infographics, and drilling down into individual shots.

## Getting Started

### Frontend
1. `cd frontend`
2. `npm install`
3. `npm run dev`

### Backend
1. `cd backend`
2. `./mvnw spring-boot:run` (or use the VS Code task "Start Backend (Spring Boot)")

### Database
- If using Docker: `docker-compose up db` (or `docker-compose up -d db` to run in detached mode)
- If running PostgreSQL locally (not via Docker Compose): Ensure PostgreSQL is running. The backend is configured to connect to `jdbc:postgresql://localhost:5432/golfdb` by default.

### Full Stack (Docker Compose)
1. Ensure Docker is running.
2. From the project root, run: `docker-compose up --build`
3. This will start three containers:
   - PostgreSQL database (port 5432)
   - Spring Boot backend (port 8080)
   - Vue.js frontend served by Nginx (port 80)
4. Access the application at http://localhost

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

## Data Analysis & Visualization
Each session includes:
- Overall session statistics
- Club-by-club breakdown of performance
- Individual shot details
- Advanced metrics and analytics

### Shot Visualizations
The application provides comprehensive visual representations of your shots:
- **Top-Down View**: View all shots plotted on a virtual driving range from above, showing the dispersion pattern and distance relative to target. This view utilizes Chart.js to create an accurate scatter plot of your shots.
- **Side-On View**: See the trajectory of each shot from a side perspective, showing the launch angle, apex height, and distance. The parabolic flight path is calculated based on shot metrics including launch angle and apex height.
- **Interactive Features**:
  - Filter visualizations by club to focus on specific parts of your game
  - Interactive tooltips provide detailed metrics for each shot when hovering
  - Color coding based on club type for easy visual differentiation
- **Data Handling**: The visualization system is robust and handles missing or incomplete data by using intelligent defaults based on club type and available metrics.

---

## Development
- Frontend: Vue.js (Vite) with Chart.js for data visualization
- Backend: Java Spring Boot
- Database: PostgreSQL

### Data Visualization System
The shot data visualizations are built using:
- **Chart.js**: Core charting library that provides responsive, interactive charts
- **Vue Component Architecture**: Separate components for different visualization types (TopDownView, SideView)
- **Reactive Data Binding**: Visualizations automatically update when filtering or when new data is available
- **Mathematical Models**: Flight trajectories are calculated using physics-based parabolic models derived from launch monitor metrics

#### Adding New Visualizations
To add a new visualization type:
1. Create a new Vue component in the `frontend/src/components/` directory
2. Import and use Chart.js or D3.js as needed
3. Add the component to SessionView.vue and pass the necessary props
4. Update CSS styling in the component's `<style>` section to ensure proper display

### Test Data
The application comes with sample test data that is automatically loaded into the database on startup when using Docker Compose. This data includes:
- A sample practice session with shots from various clubs
- Test data representing typical metrics from a Garmin R10 launch monitor

You can modify the test data by editing the files in the `database/init/` directory.

### Sample Data Files
Sample data files for testing are located in the `sample_data/` directory:
- `ags-shots-2025-05-14.csv` - An example Awesome Golf CSV export with various driver shots

These sample files can be used to test the CSV upload functionality without requiring actual launch monitor data.
