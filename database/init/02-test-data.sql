-- Add some test data to the database
-- This will create sample sessions with a few shots each from different launch monitors

-- Sample Garmin R10 Session
INSERT INTO session (title, upload_date, session_date, location, source_type)
VALUES (
  'Sample Garmin R10 Session',
  '2025-05-14 13:30:00',
  '2025-05-14 13:30:00',
  'Demo Golf Range',
  'GARMIN_R10'
);

-- Sample Awesome Golf Session
INSERT INTO session (title, upload_date, session_date, location, source_type)
VALUES (
  'Sample Awesome Golf Session',
  '2025-05-15 10:15:00',
  '2025-05-15 10:15:00',
  'Indoor Golf Simulator',
  'AWESOME_GOLF'
);

-- Sample Garmin R10 Shots
INSERT INTO shot (
  session_id, shot_number, club, ball_speed, club_head_speed, launch_angle, 
  launch_direction, spin_rate, spin_axis, carry_distance, total_distance,
  deviation, apex, attack_angle, face_angle, face_to_path, swing_path,
  total_lateral_distance, carry_lateral_distance
)
VALUES 
-- Shot 1 - Driver
(1, 1, 'Driver', 152.3, 105.2, 13.5, 1.2, 2650, -2.1, 245.8, 267.3, 5.2, 31.4, 1.2, -0.8, -2.0, 1.2, 8.5, 7.2),
-- Shot 2 - Driver
(1, 2, 'Driver', 155.6, 106.8, 14.2, -0.7, 2450, 0.5, 255.3, 278.6, -3.8, 33.6, 1.5, 0.3, -1.2, 1.5, -10.2, -9.5),
-- Shot 3 - 7 Iron
(1, 3, '7 Iron', 115.3, 83.2, 17.8, 0.4, 6350, -3.2, 158.6, 162.7, 2.1, 29.8, -3.2, -1.5, 1.7, -3.2, 3.7, 3.5),
-- Shot 4 - 7 Iron
(1, 4, '7 Iron', 112.8, 81.5, 18.2, 0.9, 6580, -2.8, 153.2, 156.4, 3.4, 30.6, -2.9, -1.2, 1.7, -2.9, 4.8, 4.2),
-- Shot 5 - Pitching Wedge
(1, 5, 'PW', 90.3, 74.6, 25.3, -1.5, 8740, 1.2, 110.8, 112.3, -2.1, 25.7, -5.3, 0.8, 6.1, -5.3, -2.1, -1.9);

-- Sample Awesome Golf Shots
INSERT INTO shot (
  session_id, shot_number, club, club_description, shot_time, altitude,
  ball_speed, club_head_speed, launch_angle, horizontal_launch, spin_rate, 
  spin_axis, carry_distance, total_distance, roll_distance, peak_height,
  descent_angle, smash, dynamic_loft, shot_classification,
  total_lateral_distance, carry_lateral_distance, apex, deviation
)
VALUES 
-- Shot 1 - Driver
(2, 1, 'Driver', 'TaylorMade Stealth', '2025-05-15 10:15:30', 0.0,
  135.9, 102.2, 20.4, 8.3, 5125, 28.9, 202.2, 214.6, 12.4, 136.3,
  48.1, 1.33, 23.3, 'Push Slice',
  42.8, 38.5, 45.3, 38.5),
-- Shot 2 - Driver
(2, 2, 'Driver', 'TaylorMade Stealth', '2025-05-15 10:17:45', 0.0,
  138.2, 96.6, 17.7, 2.4, 3799, 12.8, 227.9, 243.7, 15.8, 120.2,
  44.1, 1.43, 19.8, 'Push Fade',
  18.2, 16.5, 40.1, 16.5),
-- Shot 3 - Driver
(2, 3, 'Driver', 'TaylorMade Stealth', '2025-05-15 10:20:10', 0.0,
  149.8, 104.0, 15.2, -1.6, 3098, 10.1, 257.1, 274.7, 17.6, 112.9,
  40.6, 1.44, 17.2, 'Fade',
  -8.7, -7.9, 37.6, -7.9);

-- Add additional shots if desired
