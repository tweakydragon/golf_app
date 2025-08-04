-- Database Optimization: Create Performance Indexes for Golf Application
-- Created: 2025-07-05
-- Purpose: Optimize query performance for common access patterns

-- =============================================================================
-- SESSION TABLE INDEXES
-- =============================================================================

-- Index for upload date ordering (most common query pattern)
CREATE INDEX IF NOT EXISTS idx_session_upload_date 
ON session (upload_date DESC);

-- Index for session date ordering and filtering
CREATE INDEX IF NOT EXISTS idx_session_session_date 
ON session (session_date DESC);

-- Index for title search (case-insensitive)
CREATE INDEX IF NOT EXISTS idx_session_title_lower 
ON session (LOWER(title));

-- Index for location search (case-insensitive)
CREATE INDEX IF NOT EXISTS idx_session_location_lower 
ON session (LOWER(location));

-- Index for source type filtering
CREATE INDEX IF NOT EXISTS idx_session_source_type 
ON session (source_type);

-- Composite index for date range queries with source type
CREATE INDEX IF NOT EXISTS idx_session_source_date_composite 
ON session (source_type, session_date DESC, upload_date DESC);

-- Composite index for search and ordering
CREATE INDEX IF NOT EXISTS idx_session_search_composite 
ON session (source_type, LOWER(title), upload_date DESC);

-- =============================================================================
-- SHOT TABLE INDEXES
-- =============================================================================

-- Primary foreign key index for session relationship
CREATE INDEX IF NOT EXISTS idx_shot_session_id 
ON shot (session_id);

-- Composite index for session shots ordered by shot number (most common pattern)
CREATE INDEX IF NOT EXISTS idx_shot_session_shot_number 
ON shot (session_id, shot_number);

-- Index for club filtering
CREATE INDEX IF NOT EXISTS idx_shot_club 
ON shot (club);

-- Composite index for session and club filtering
CREATE INDEX IF NOT EXISTS idx_shot_session_club 
ON shot (session_id, club, shot_number);

-- Index for carry distance filtering and sorting
CREATE INDEX IF NOT EXISTS idx_shot_carry_distance 
ON shot (carry_distance DESC) WHERE carry_distance IS NOT NULL;

-- Index for total distance filtering and sorting
CREATE INDEX IF NOT EXISTS idx_shot_total_distance 
ON shot (total_distance DESC) WHERE total_distance IS NOT NULL;

-- Index for ball speed filtering and sorting
CREATE INDEX IF NOT EXISTS idx_shot_ball_speed 
ON shot (ball_speed DESC) WHERE ball_speed IS NOT NULL;

-- Index for club head speed filtering
CREATE INDEX IF NOT EXISTS idx_shot_club_head_speed 
ON shot (club_head_speed DESC) WHERE club_head_speed IS NOT NULL;

-- Index for launch angle filtering
CREATE INDEX IF NOT EXISTS idx_shot_launch_angle 
ON shot (launch_angle) WHERE launch_angle IS NOT NULL;

-- Index for spin rate filtering
CREATE INDEX IF NOT EXISTS idx_shot_spin_rate 
ON shot (spin_rate) WHERE spin_rate IS NOT NULL;

-- Index for shot time filtering and sorting
CREATE INDEX IF NOT EXISTS idx_shot_shot_time 
ON shot (shot_time DESC) WHERE shot_time IS NOT NULL;

-- Index for shot classification filtering
CREATE INDEX IF NOT EXISTS idx_shot_classification 
ON shot (shot_classification) WHERE shot_classification IS NOT NULL;

-- Composite index for analytics queries (club statistics)
CREATE INDEX IF NOT EXISTS idx_shot_club_metrics 
ON shot (club, carry_distance, ball_speed, club_head_speed) 
WHERE club IS NOT NULL AND carry_distance IS NOT NULL;

-- Composite index for session analytics with key metrics
CREATE INDEX IF NOT EXISTS idx_shot_session_analytics 
ON shot (session_id, club, carry_distance, total_distance, ball_speed) 
WHERE carry_distance IS NOT NULL;

-- Composite index for range filtering on multiple metrics
CREATE INDEX IF NOT EXISTS idx_shot_performance_metrics 
ON shot (session_id, ball_speed, carry_distance, launch_angle, spin_rate) 
WHERE ball_speed IS NOT NULL AND carry_distance IS NOT NULL;

-- Index for deviation analysis (accuracy)
CREATE INDEX IF NOT EXISTS idx_shot_deviation 
ON shot (deviation) WHERE deviation IS NOT NULL;

-- Composite index for cross-session club comparisons
CREATE INDEX IF NOT EXISTS idx_shot_global_club_stats 
ON shot (club, carry_distance, ball_speed) 
WHERE club IS NOT NULL AND carry_distance IS NOT NULL AND ball_speed IS NOT NULL;

-- =============================================================================
-- SPECIALIZED INDEXES FOR ADVANCED ANALYTICS
-- =============================================================================

-- Index for finding best shots across all sessions
CREATE INDEX IF NOT EXISTS idx_shot_best_carry_global 
ON shot (carry_distance DESC, total_distance DESC, ball_speed DESC) 
WHERE carry_distance IS NOT NULL;

-- Index for finding best shots within sessions
CREATE INDEX IF NOT EXISTS idx_shot_best_carry_session 
ON shot (session_id, carry_distance DESC, shot_number) 
WHERE carry_distance IS NOT NULL;

-- Index for consistency analysis (standard deviation calculations)
CREATE INDEX IF NOT EXISTS idx_shot_consistency_metrics 
ON shot (session_id, club, carry_distance, total_distance) 
WHERE club IS NOT NULL AND carry_distance IS NOT NULL;

-- Index for time-based analysis
CREATE INDEX IF NOT EXISTS idx_shot_temporal_analysis 
ON shot (shot_time, club, carry_distance) 
WHERE shot_time IS NOT NULL AND club IS NOT NULL;

-- =============================================================================
-- PARTIAL INDEXES FOR SPECIFIC USE CASES
-- =============================================================================

-- Index for Garmin R10 specific metrics
CREATE INDEX IF NOT EXISTS idx_shot_garmin_metrics 
ON shot (session_id, attack_angle, face_angle, swing_path) 
WHERE attack_angle IS NOT NULL OR face_angle IS NOT NULL OR swing_path IS NOT NULL;

-- Index for Awesome Golf specific metrics
CREATE INDEX IF NOT EXISTS idx_shot_awesome_golf_metrics 
ON shot (session_id, smash, peak_height, descent_angle) 
WHERE smash IS NOT NULL OR peak_height IS NOT NULL OR descent_angle IS NOT NULL;

-- Index for high-performance shots (distance > 250 yards)
CREATE INDEX IF NOT EXISTS idx_shot_high_performance 
ON shot (carry_distance DESC, club, ball_speed DESC) 
WHERE carry_distance > 250;

-- Index for driver shots (most analyzed club)
CREATE INDEX IF NOT EXISTS idx_shot_driver_analysis 
ON shot (session_id, carry_distance, ball_speed, launch_angle, spin_rate, shot_number) 
WHERE club = 'Driver';

-- =============================================================================
-- COMMENTS AND DOCUMENTATION
-- =============================================================================

COMMENT ON INDEX idx_session_upload_date IS 'Optimizes session listing by upload date (primary sorting)';
COMMENT ON INDEX idx_session_session_date IS 'Optimizes session filtering by actual session date';
COMMENT ON INDEX idx_session_title_lower IS 'Optimizes case-insensitive title searches';
COMMENT ON INDEX idx_session_location_lower IS 'Optimizes case-insensitive location searches';
COMMENT ON INDEX idx_shot_session_shot_number IS 'Optimizes shot retrieval within sessions';
COMMENT ON INDEX idx_shot_session_club IS 'Optimizes club-specific shot filtering within sessions';
COMMENT ON INDEX idx_shot_club_metrics IS 'Optimizes club performance analytics queries';
COMMENT ON INDEX idx_shot_session_analytics IS 'Optimizes session-level analytics calculations';
COMMENT ON INDEX idx_shot_performance_metrics IS 'Optimizes multi-metric range filtering';
COMMENT ON INDEX idx_shot_driver_analysis IS 'Optimizes driver-specific analytics (most common club analysis)';