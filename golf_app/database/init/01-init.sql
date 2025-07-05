-- Initialize the golf app database schema
-- This SQL script will be automatically run when the PostgreSQL container starts

-- Create extensions if needed
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Ensure the database exists (should be created by Docker Compose)
-- CREATE DATABASE golfdb;

-- Create tables if they don't exist
-- Note: These will likely be created by Hibernate but defining them here
-- ensures the schema matches our expectations

-- Session table
CREATE TABLE IF NOT EXISTS session (
  id BIGSERIAL PRIMARY KEY,
  title VARCHAR(255) NOT NULL,
  upload_date TIMESTAMP,
  session_date TIMESTAMP,
  location VARCHAR(255),
  source_type VARCHAR(50)
);

-- Shot table
CREATE TABLE IF NOT EXISTS shot (
  id BIGSERIAL PRIMARY KEY,
  session_id BIGINT REFERENCES session(id) ON DELETE CASCADE,
  shot_number INTEGER,
  shot_time TIMESTAMP,
  club VARCHAR(100),
  club_description VARCHAR(255),
  altitude DOUBLE PRECISION,
  
  -- Common metrics
  ball_speed DOUBLE PRECISION,
  club_head_speed DOUBLE PRECISION,
  carry_distance DOUBLE PRECISION,
  total_distance DOUBLE PRECISION,
  roll_distance DOUBLE PRECISION,
  launch_angle DOUBLE PRECISION,
  launch_direction DOUBLE PRECISION,
  spin_rate DOUBLE PRECISION,
  spin_axis DOUBLE PRECISION,
  apex DOUBLE PRECISION,
  
  -- Advanced metrics
  attack_angle DOUBLE PRECISION,
  face_angle DOUBLE PRECISION,
  face_to_path DOUBLE PRECISION,
  swing_path DOUBLE PRECISION,
  swing_plane DOUBLE PRECISION,
  vertical_face_impact DOUBLE PRECISION,
  horizontal_face_impact DOUBLE PRECISION,
  
  -- Awesome Golf specific metrics
  smash DOUBLE PRECISION,
  peak_height DOUBLE PRECISION,
  descent_angle DOUBLE PRECISION,
  horizontal_launch DOUBLE PRECISION,
  carry_lateral_distance DOUBLE PRECISION,
  total_lateral_distance DOUBLE PRECISION,
  carry_curve_distance DOUBLE PRECISION,
  total_curve_distance DOUBLE PRECISION,
  dynamic_loft DOUBLE PRECISION,
  spin_loft DOUBLE PRECISION,
  low_point DOUBLE PRECISION,
  face_target DOUBLE PRECISION,
  swing_plane_tilt DOUBLE PRECISION,
  swing_plane_rotation DOUBLE PRECISION,
  shot_classification VARCHAR(100),

  -- Add timestamp
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for common queries
CREATE INDEX IF NOT EXISTS idx_shot_session_id ON shot(session_id);
CREATE INDEX IF NOT EXISTS idx_session_date ON session(session_date);
