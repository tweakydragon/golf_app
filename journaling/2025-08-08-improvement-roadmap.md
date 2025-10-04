# Improvement Roadmap (2025-08-08)

## Overview

Prioritized technical and architectural improvements derived from codebase review.

## 1. Parsing Architecture

- Replace manual split logic with Apache Commons CSV.
- Normalize headers once; immutable mapping header -> field setter.
- Separate Garmin vs Awesome Golf descriptor.
- SafeNumberParser (null on invalid, no exceptions per field).
- Remove HTML escaping from parsing layer.
- Introduce ParseResult (shots, totalRows, skippedRows, fieldErrorCounts, earliestShotTime, warnings).
- Golden fixture tests for both sources.

## 2. Domain Modeling

- Enums: ClubType, DataSource.
- Potential embeddable for advanced metrics.
- DTO layer for outward API; avoid exposing JPA entities.
- Compute derived metrics instead of persisting duplicates when feasible.

## 3. Validation & Plausibility

- Plausibility checker service (carry > total, negative spin, etc.).
- Aggregate counts; info summary, debug details.
- Keep per-field failures non-fatal.

## 4. Transaction & Service Boundaries

- Split responsibilities: Parsing, Import (transactional), Statistics.
- Easier isolated testing.

## 5. Security / Input Handling

- Remove sanitizeInput HTML escaping; validate length only.
- Enforce multipart size limits.
- Add explicit CORS config.
- Rate limit upload endpoint (Bucket4j).

## 6. Error & Logging Strategy

- Row-level debug, aggregated info line.
- Structured logging key=value for ingestion.

## 7. Testing Depth

- Unit tests: number parser, header normalization, mappings.
- Integration: Import flow with Testcontainers Postgres.
- Golden CSV assertions on stats and field population.
- Contract tests for REST endpoints.

## 8. Frontend Type Safety & State Mgmt

- Introduce TypeScript incrementally (types/, stores/ with Pinia).
- Central metricsConfig map (label, unit, precision, formatter).
- Refactor charts to consume config.

## 9. Accessibility & Progressive Enhancement

- Textual summaries & aria labels for charts.
- Tabular fallback toggle.

## 10. Performance / UX

- Chart.js update vs destroy/recreate; debounce watchers.
- Lazy load heavy chart components.

## 11. Data Normalization & Units

- Standardize distance unit (yards) internally.
- Explicit conversion helpers; document in METRICS.md.

## 12. Database Schema Evolution

- Consider secondary table / JSONB for advanced metrics.
- Index session_id, club, shot_number.

## 13. API Design

- Version endpoints /api/v1.
- DTO responses + aggregated stats endpoint.
- Caching headers (ETag / Last-Modified).

## 14. Observability

- Micrometer counters (shots_imported_total, parse_errors_total, sessions_imported_total).
- Histogram for carry distance.

## 15. Documentation & Onboarding

- PARSING.md (header variants, mapping strategy).
- METRICS.md (definitions/units/formulas/source).
- README updates with endpoints & import workflow.

## 16. Dev Ergonomics

- ESLint + TS config once TS introduced.
- Pre-commit hooks (lint + test).

## 17. CI / Quality Gates

- GitHub Actions: backend build/test, frontend test/coverage, cache deps.

## 18. Future Extensibility

- SourceDescriptor abstraction for new launch monitors.

---

Initial execution focus: Items 1–3 (parsing foundation & Garmin refactor) then item 4 (ParseResult logging).

## Progress Log (Updates)

### Completed (Backend Parsing & Foundations)

- Implemented Apache Commons CSV Garmin parser with header normalization map.
- Implemented Awesome Golf parser (two-line header) with positional mapping and earliest timestamp capture.
- Added ParseResult (shots, totalRows, skippedRows, fieldErrorCounts, earliestShotTimestamp).
- Added SafeNumberParser & HeaderNormalizer utilities.
- Replaced legacy string-splitting parsing code; removed HTML escaping in CsvService.
- Introduced DataSource enum; Session.sourceType migrated to enum.
- Added DTO layer (ShotDTO, SessionDTO, DtoMapper) and updated search endpoint to return DTOs.
- Added plausibility filtering (ShotPlausibilityChecker) with filtered count logging.
- Added unit tests for Garmin/Awesome Golf parsers, HeaderNormalizer, SafeNumberParser.
- Added Awesome Golf & Garmin sample CSV fixtures.
- Introduced H2 in-memory test profile & reinstated Spring context smoke test.

### Logging Enhancements

- Aggregated import log lines now include filtered count.
- Field error counts exposed at debug level only.

### Pending / Next Steps

- Expand plausibility rules (carry <= total + tolerance, descentAngle positive, smash realistic range).
- Instrument Micrometer metrics (shots_imported_total, parse_field_errors_total, shots_filtered_total).
- Add integration test using @SpringBootTest with import endpoints (once metrics & plausibility stable).
- Create PARSING.md and METRICS.md docs.
- Introduce TypeScript layer & metric config map on frontend (roadmap item 8).
- Implement CORS + rate limiting (Bucket4j) (security hardening).

### Considerations / Open Items

- Decide approach for advanced metrics storage (JSONB vs columns) before adding new sources.
- Evaluate migrating AwesomeGolfCsvParser to Commons CSV if embedded commas/quotes appear in future exports.

Last updated: 2025-08-08
