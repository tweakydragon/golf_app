-- Database Configuration Optimizations for Golf Application
-- Created: 2025-07-05
-- Purpose: Optimize PostgreSQL configuration for golf analytics workloads

-- =============================================================================
-- ENABLE REQUIRED EXTENSIONS
-- =============================================================================

-- Enable pg_stat_statements for query performance monitoring
CREATE EXTENSION IF NOT EXISTS pg_stat_statements;

-- Enable pg_trgm for improved text search performance
CREATE EXTENSION IF NOT EXISTS pg_trgm;

-- Enable btree_gin for composite indexes
CREATE EXTENSION IF NOT EXISTS btree_gin;

-- =============================================================================
-- CONFIGURATION RECOMMENDATIONS
-- These are PostgreSQL settings that should be applied at the database level
-- Add these to postgresql.conf or apply via ALTER SYSTEM commands
-- =============================================================================

-- Memory Configuration (for golf analytics workloads)
-- shared_buffers = 256MB (25% of available RAM for dedicated server)
-- effective_cache_size = 1GB (75% of available RAM)
-- work_mem = 16MB (for complex analytics queries)
-- maintenance_work_mem = 256MB (for index maintenance)

-- Query Planner Configuration
-- random_page_cost = 1.1 (SSD storage)
-- effective_io_concurrency = 200 (for SSD)
-- max_worker_processes = 8
-- max_parallel_workers_per_gather = 4
-- max_parallel_workers = 8

-- WAL Configuration (for write performance)
-- wal_buffers = 16MB
-- checkpoint_completion_target = 0.9
-- checkpoint_timeout = 10min
-- max_wal_size = 2GB

-- Connection and Lock Configuration
-- max_connections = 100 (adjust based on connection pool size)
-- lock_timeout = 30s
-- statement_timeout = 60s (for analytics queries)

-- =============================================================================
-- APPLICATION-SPECIFIC DATABASE SETTINGS
-- =============================================================================

-- Set application-specific parameters
ALTER DATABASE golfdb SET application_name = 'golf-analytics-app';
ALTER DATABASE golfdb SET log_statement = 'mod';  -- Log modifications
ALTER DATABASE golfdb SET log_min_duration_statement = 1000;  -- Log slow queries > 1s
ALTER DATABASE golfdb SET shared_preload_libraries = 'pg_stat_statements';

-- Golf application specific settings
ALTER DATABASE golfdb SET default_statistics_target = 500;  -- Better query plans for analytics
ALTER DATABASE golfdb SET constraint_exclusion = partition;  -- For future partitioning
ALTER DATABASE golfdb SET enable_partitionwise_join = on;
ALTER DATABASE golfdb SET enable_partitionwise_aggregate = on;

-- =============================================================================
-- OPTIMIZE EXISTING DATA
-- =============================================================================

-- Update statistics on all tables for better query planning
ANALYZE session;
ANALYZE shot;

-- Reset query planner statistics
SELECT pg_stat_reset();
SELECT pg_stat_statements_reset();

-- =============================================================================
-- CREATE OPTIMIZED VIEWS FOR COMMON QUERIES
-- =============================================================================

-- Session summary view (frequently used for listing)
CREATE OR REPLACE VIEW session_summary AS
SELECT 
    s.id,
    s.title,
    s.upload_date,
    s.session_date,
    s.location,
    s.source_type,
    COUNT(sh.id) as shot_count,
    ROUND(AVG(sh.carry_distance), 1) as avg_carry_distance,
    ROUND(AVG(sh.ball_speed), 1) as avg_ball_speed,
    COUNT(DISTINCT sh.club) as unique_clubs
FROM session s
LEFT JOIN shot sh ON s.id = sh.session_id
GROUP BY s.id, s.title, s.upload_date, s.session_date, s.location, s.source_type;

-- Club performance view (for analytics)
CREATE OR REPLACE VIEW club_performance AS
SELECT 
    sh.club,
    COUNT(sh.id) as total_shots,
    ROUND(AVG(sh.carry_distance), 1) as avg_carry_distance,
    ROUND(STDDEV(sh.carry_distance), 1) as std_carry_distance,
    ROUND(MIN(sh.carry_distance), 1) as min_carry_distance,
    ROUND(MAX(sh.carry_distance), 1) as max_carry_distance,
    ROUND(AVG(sh.ball_speed), 1) as avg_ball_speed,
    ROUND(AVG(sh.club_head_speed), 1) as avg_club_head_speed,
    ROUND(AVG(sh.launch_angle), 1) as avg_launch_angle,
    ROUND(AVG(sh.spin_rate), 0) as avg_spin_rate,
    ROUND(AVG(sh.smash), 2) as avg_smash_factor
FROM shot sh
WHERE sh.club IS NOT NULL 
  AND sh.carry_distance IS NOT NULL
GROUP BY sh.club
HAVING COUNT(sh.id) >= 5  -- Only clubs with at least 5 shots
ORDER BY sh.club;

-- Session analytics view (for dashboard)
CREATE OR REPLACE VIEW session_analytics AS
SELECT 
    s.id as session_id,
    s.title,
    s.upload_date,
    s.source_type,
    COUNT(sh.id) as total_shots,
    ROUND(AVG(sh.carry_distance), 1) as avg_carry_distance,
    ROUND(MAX(sh.carry_distance), 1) as longest_drive,
    ROUND(AVG(sh.ball_speed), 1) as avg_ball_speed,
    ROUND(MAX(sh.ball_speed), 1) as max_ball_speed,
    COUNT(DISTINCT sh.club) as clubs_used,
    ROUND(STDDEV(sh.carry_distance), 1) as distance_consistency,
    ROUND(
        COUNT(CASE WHEN ABS(sh.deviation) <= 20 THEN 1 END) * 100.0 / 
        NULLIF(COUNT(CASE WHEN sh.deviation IS NOT NULL THEN 1 END), 0), 
        1
    ) as fairway_accuracy_percent
FROM session s
LEFT JOIN shot sh ON s.id = sh.session_id
GROUP BY s.id, s.title, s.upload_date, s.source_type
ORDER BY s.upload_date DESC;

-- Performance tracking view
CREATE OR REPLACE VIEW performance_trends AS
SELECT 
    DATE_TRUNC('week', s.session_date) as week_start,
    COUNT(DISTINCT s.id) as sessions_count,
    COUNT(sh.id) as total_shots,
    ROUND(AVG(sh.carry_distance), 1) as avg_distance,
    ROUND(AVG(sh.ball_speed), 1) as avg_ball_speed,
    COUNT(DISTINCT sh.club) as unique_clubs
FROM session s
JOIN shot sh ON s.id = sh.session_id
WHERE s.session_date >= CURRENT_DATE - INTERVAL '6 months'
  AND sh.carry_distance IS NOT NULL
GROUP BY DATE_TRUNC('week', s.session_date)
ORDER BY week_start DESC;

-- =============================================================================
-- CREATE INDEXES ON VIEWS (WHERE APPLICABLE)
-- =============================================================================

-- Note: PostgreSQL doesn't support indexes on regular views, but we can create
-- materialized views for frequently accessed analytics if needed in the future

-- =============================================================================
-- PERFORMANCE MONITORING FUNCTION
-- =============================================================================

-- Function to get table performance summary
CREATE OR REPLACE FUNCTION get_table_performance_summary()
RETURNS TABLE (
    table_name TEXT,
    row_count BIGINT,
    table_size TEXT,
    index_size TEXT,
    total_size TEXT,
    seq_scan BIGINT,
    seq_tup_read BIGINT,
    idx_scan BIGINT,
    idx_tup_fetch BIGINT
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        t.schemaname || '.' || t.tablename as table_name,
        t.n_live_tup as row_count,
        pg_size_pretty(pg_relation_size(c.oid)) as table_size,
        pg_size_pretty(pg_indexes_size(c.oid)) as index_size,
        pg_size_pretty(pg_total_relation_size(c.oid)) as total_size,
        t.seq_scan,
        t.seq_tup_read,
        t.idx_scan,
        t.idx_tup_fetch
    FROM pg_stat_user_tables t
    JOIN pg_class c ON c.relname = t.tablename
    WHERE t.schemaname = 'public'
    ORDER BY pg_total_relation_size(c.oid) DESC;
END;
$$ LANGUAGE plpgsql;

-- =============================================================================
-- TRIGGER FOR AUTOMATIC STATISTICS UPDATES
-- =============================================================================

-- Function to update statistics after bulk operations
CREATE OR REPLACE FUNCTION update_statistics_after_bulk_ops()
RETURNS TRIGGER AS $$
BEGIN
    -- Only run ANALYZE if significant changes (every 1000 rows)
    IF (TG_OP = 'INSERT' AND NEW.id % 1000 = 0) OR
       (TG_OP = 'DELETE' AND OLD.id % 1000 = 0) THEN
        EXECUTE 'ANALYZE ' || TG_TABLE_NAME;
    END IF;
    
    RETURN COALESCE(NEW, OLD);
END;
$$ LANGUAGE plpgsql;

-- Create triggers for automatic statistics updates
DROP TRIGGER IF EXISTS update_session_stats ON session;
CREATE TRIGGER update_session_stats
    AFTER INSERT OR DELETE ON session
    FOR EACH ROW
    EXECUTE FUNCTION update_statistics_after_bulk_ops();

DROP TRIGGER IF EXISTS update_shot_stats ON shot;
CREATE TRIGGER update_shot_stats
    AFTER INSERT OR DELETE ON shot
    FOR EACH ROW
    EXECUTE FUNCTION update_statistics_after_bulk_ops();

-- =============================================================================
-- COMMENTS AND DOCUMENTATION
-- =============================================================================

COMMENT ON VIEW session_summary IS 'Optimized view for session listing with key metrics';
COMMENT ON VIEW club_performance IS 'Aggregated club performance statistics across all sessions';
COMMENT ON VIEW session_analytics IS 'Comprehensive session analytics for dashboard display';
COMMENT ON VIEW performance_trends IS 'Weekly performance trends for progress tracking';
COMMENT ON FUNCTION get_table_performance_summary() IS 'Returns performance summary for all golf app tables';

-- =============================================================================
-- COMPLETION LOG
-- =============================================================================

-- Log the completion of optimization
INSERT INTO pg_stat_user_tables (schemaname, tablename) 
SELECT 'public', 'optimization_log' 
WHERE NOT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'optimization_log');

CREATE TABLE IF NOT EXISTS optimization_log (
    id SERIAL PRIMARY KEY,
    optimization_type VARCHAR(100),
    applied_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    description TEXT
);

INSERT INTO optimization_log (optimization_type, description) 
VALUES ('database_performance_optimization', 'Applied comprehensive database performance optimizations for golf analytics application');