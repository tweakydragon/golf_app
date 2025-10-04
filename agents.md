# agents.md

Guidance for Codex-based agents working in this repository. Adapted from `CLAUDE.md` with additional notes on Codex CLI tooling and subagent collaboration.

## Project Overview
- **Purpose**: Full-stack golf session analysis platform that ingests CSV data from Garmin R10 and Awesome Golf launch monitors and surfaces analytics + visualizations.
- **Frontend**: Vue 3 (Composition API) + Vite, Bootstrap 5, Chart.js, custom Canvas rendering for shot visuals.
- **Backend**: Spring Boot (Java 17) with JPA/Hibernate, REST endpoints, CSV ingestion services.
- **Data**: PostgreSQL schema with rich shot/session entities; seed data provided for local dev.
- **Containers**: Docker Compose configurations for dev/prod orchestration.

## Codex Operating Basics
- **Shell access**: Use PowerShell commands via `shell` tool; wrap Unix-style commands in `bash -lc` only when WSL utilities are present. Prefer Windows-native commands where possible.
- **Filesystem**: Workspace-write sandbox limited to repo root; respect existing uncommitted files.
- **Planning**: For non-trivial tasks, call the planning tool early and keep it updated as subtasks finish.
- **Testing**: Default to running relevant npm/mvn scripts after changes. Note that Vitest currently fails unless a canvas mock supplies `fillText`; document any inherited failures.
- **Network**: Restricted; avoid install scripts that fetch remote artifacts without approval.

## Subagent Playbook
Codex CLI supports multi-step reasoning using planning + targeted subtasks. When the scope expands, treat each major concern as a “subagent” with its own checklist.

1. **Architecture Scout** – audits repo layout before edits (`ls`, `tree`, `rg`).
2. **Implementation Agent** – performs focused code changes; keep diffs scoped.
3. **Verifier** – runs tests/lints and reports anomalies; if failures pre-exist, note evidence from logs.
4. **Documentarian** – updates journals or docs (e.g., `journaling/` entries, README adjustments).

Coordinate these roles sequentially, updating the plan between hand-offs. When collaboration is required (e.g., backend + frontend touchpoints), capture assumptions for the next subagent at the end of each step.

## Development Commands
### Quick Start
```bash
./dev.sh                     # spins up full stack with live reload
go to http://localhost:5173   # frontend
go to http://localhost:8080   # backend API
```

### Frontend
```bash
cd frontend
npm install
npm run dev        # Vite dev server (5173)
npm run build
npm run preview
npm run test:run   # Vitest (requires canvas mocks; see notes)
```

### Backend
```bash
cd backend
./mvnw spring-boot:run
./mvnw test
./mvnw clean package
```

### Docker
```bash
docker-compose -f docker-compose.dev.yml up --build
# production
docker-compose up --build
docker-compose -f docker-compose.dev.yml down
```

## Architecture Snapshot
- `frontend/src/components/` – primary views (Home, SessionView, UploadCSV) + complex modals.
- `frontend/src/components/charts/` – specialized chart wrappers around Chart.js; prefer `BaseChart.vue` for new work.
- `frontend/src/components/SessionOverviewVisual.vue` – large Canvas renderer for shot paths.
- `backend/src/main/java/com/example/demo/` – controllers, services, parsers, DTO mapping, models.
- `database/` + `sample_data/` – SQL initialization + CSV fixtures.

## Data Model Highlights
- `Session` entity holds metadata, links to `Shot` entities.
- `Shot` includes ~40 metrics: carry/total distance, launch/attack angles, spin axis, dispersion info.
- `DataSource` enum differentiates Garmin vs. Awesome Golf payloads.

## Workflow Guidelines
1. Launch `./dev.sh` or targeted npm/mvn commands depending on scope.
2. Lean on hot reload in both front and back ends for rapid iteration.
3. Keep PostgreSQL container running for end-to-end validation; sample data loads automatically.
4. Before large refactors, snapshot existing behavior (screenshots, API responses) if possible.

## Style & Patterns
### Frontend
- Prefer Composition API + `<script setup>`.
- Use Bootstrap utility classes for layout; keep global tokens in `style.css`.
- Canvas visuals should provide hover/selection feedback and honor current scale.
- Chart components should consume `BaseChart.vue` and centralized palette helpers.

### Backend
- Follow standard Spring Boot layering (controller -> service -> repository).
- CSV parsing lives in `parsing/` package; ensure new formats update DTOs and services.
- Maintain transactional boundaries in services when mutating sessions/shots.

## Visualization Principles
- Maintain realistic trajectory curves and distance markers (50-yard intervals in overhead view).
- Provide both overhead and side views; highlight selected shots across UI surfaces.
- Support progressive disclosure: high-level summaries first, detailed metrics on demand.

## Environment Notes
- **Development**: Vite (5173), Spring Boot (8080), PostgreSQL (5432). Docker volumes enable live reload.
- **Production**: Nginx serves frontend build; backend packaged as Spring Boot JAR; persistent Postgres storage.

## Common Tasks
- **New Chart**: scaffold under `components/charts/`, extend `BaseChart.vue`, wire into `SessionView`.
- **CSV Extension**: adjust parsers + DTOs + JPA entities; backfill tests and sample data.
- **Database Migration**: update JPA models and SQL init scripts; restart containers.
- **Documentation**: update `README.md`, add dated entries in `journaling/` describing changes.

## Testing Expectations
- **Backend**: `./mvnw test` (JUnit 5). Expand coverage for new parsing logic.
- **Frontend**: `npm run test:run`. Provide canvas mocks to avoid `fillText` errors; compare against existing noise in `SessionView` tests.
- Always report command output summaries, especially when failures predate your changes.

## Communication Standards
- Write responses like a helpful teammate: concise by default, but include rationale and next steps.
- Surface assumptions and potential regression risks; suggest verification commands when unable to run them.
- When using subagents/plan updates, note ownership transitions so future Codex runs can resume easily.
