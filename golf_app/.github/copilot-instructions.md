<!-- Use this file to provide workspace-specific custom instructions to Copilot. For more details, visit https://code.visualstudio.com/docs/copilot/copilot-customization#_use-a-githubcopilotinstructionsmd-file -->

This project is a golf swing analysis app with a Java Spring Boot backend and a Vue.js frontend. The backend handles CSV uploads, parses both Garmin R10 and Awesome Golf launch monitor data, and stores it in a local PostgreSQL database. The frontend visualizes the data and allows drill-down into individual shots.

## Data Sources
- **Garmin R10**: Basic shot metrics including ball speed, club head speed, carry/total distance, etc.
- **Awesome Golf**: Extended metrics including smash factor, roll distance, peak height, descent angle, shot classification, etc.

## Development Philosophy
- Prioritize readability over raw optimization in all code and design choices. Assume an intern may need to understand and support this project.
- When making changes to the project structure, dependencies, build process, or run commands, ensure the `README.md` is updated accordingly with clear instructions.
- Ensure data parsing is robust for different date formats and field configurations from different launch monitor sources.

## Architecture & Development Setup
- **Development Environment**: Use `docker-compose.dev.yml` for live reloading during development
- **Frontend**: Vue.js with Vite, enhanced with interactive canvas-based visualizations
- **Backend**: Spring Boot with DevTools for hot reloading
- **Visualization Strategy**: Use HTML5 Canvas for interactive shot trajectory and dispersion charts with zoom/pan capabilities

## Code Patterns & Preferences
- **Shot Visualizations**: Implement using canvas with realistic physics calculations for ball flight paths
- **Flight Path Calculations**: Use actual shot data (launch direction, spin axis, lateral deviation) to calculate curved flight paths, not straight lines
- **Interactive Features**: Include zoom controls (0.5x to 3x), pan functionality, and view mode toggles (side/top view)
- **Distance Markers**: Always include 50-yard distance markers in visualizations for scale reference
- **File Organization**: Keep chart components in `frontend/src/components/charts/` directory
- **Canvas Drawing**: Use separate functions for different views (drawSideView, drawTopView) with proper scaling
- **Mouse Interaction**: Check for shot selection along entire flight path, not just landing points

## Development Workflow
- **Live Reloading**: Use `./dev.sh` script for quick development startup
- **Docker Strategy**: Separate Dockerfiles for dev (.dev) and production environments
- **Volume Mounts**: Mount source directories for live reloading without rebuilding containers
- **Port Configuration**: Frontend dev server on 5173, backend on 8080, database on 5432

## UI/UX Guidelines
- **Visualization Enhancement**: Prioritize interactive, detailed shot analysis over basic charts
- **User Controls**: Provide intuitive zoom, pan, and view toggle controls for complex visualizations
- **Visual Feedback**: Use realistic backgrounds (sky gradients, grass textures) for golf-themed visualizations
- **Responsive Design**: Ensure visualizations work across different screen sizes
- **Progressive Disclosure**: Start with overview visualizations, then allow drilling down to individual shots
- **Interactive Highlighting**: Implement mouse hover highlighting across related UI elements (tables, charts, visualizations)
- **Sticky/Minimized UI**: Use scroll-based minimization for key visualizations with hover enlargement
- **Full Screen Modes**: Provide full screen visualization modes with data tables for detailed analysis

## Meta-Instructions
- Periodically review and update this instructions file if consistent patterns or preferences emerge during our conversations.
- When implementing new visualization features, follow the established canvas-based pattern with proper scaling and user controls.
- Always update README.md and DEVELOPMENT.md when adding new development workflows or dependencies.
