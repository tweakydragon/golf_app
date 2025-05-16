<!-- Use this file to provide workspace-specific custom instructions to Copilot. For more details, visit https://code.visualstudio.com/docs/copilot/copilot-customization#_use-a-githubcopilotinstructionsmd-file -->

This project is a golf swing analysis app with a Java Spring Boot backend and a Vue.js frontend. The backend handles CSV uploads, parses both Garmin R10 and Awesome Golf launch monitor data, and stores it in a local PostgreSQL database. The frontend visualizes the data and allows drill-down into individual shots.

## Data Sources
- **Garmin R10**: Basic shot metrics including ball speed, club head speed, carry/total distance, etc.
- **Awesome Golf**: Extended metrics including smash factor, roll distance, peak height, descent angle, shot classification, etc.

## Development Philosophy
- Prioritize readability over raw optimization in all code and design choices. Assume an intern may need to understand and support this project.
- When making changes to the project structure, dependencies, build process, or run commands, ensure the `README.md` is updated accordingly with clear instructions.
- Ensure data parsing is robust for different date formats and field configurations from different launch monitor sources.

## Meta-Instructions
- Periodically review and update this instructions file if consistent patterns or preferences emerge during our conversations.
