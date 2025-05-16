/**
 * Chart Utility Functions for Golf Swing Analysis App
 * 
 * This file contains common utility functions for data processing
 * and visualization used across different chart components.
 */

// Club color mapping - consistent colors for each club type
export const CLUB_COLORS = {
  "Driver": { color: '#FF6384', borderColor: '#FF2E63' },
  "3 Wood": { color: '#36A2EB', borderColor: '#0D83CD' },
  "5 Wood": { color: '#FFCE56', borderColor: '#FFA91F' },
  "3 Iron": { color: '#4BC0C0', borderColor: '#2A9090' },
  "4 Iron": { color: '#9966FF', borderColor: '#7A37FF' },
  "5 Iron": { color: '#FF9F40', borderColor: '#E67E12' },
  "6 Iron": { color: '#C9CBCF', borderColor: '#9DA1A8' },
  "7 Iron": { color: '#7FB800', borderColor: '#5A8700' },
  "8 Iron": { color: '#00B7FF', borderColor: '#0092CC' },
  "9 Iron": { color: '#8B008B', borderColor: '#5F005F' },
  "PW": { color: '#FF7F50', borderColor: '#E55A28' },
  "GW": { color: '#006400', borderColor: '#004000' },
  "SW": { color: '#8B0000', borderColor: '#5F0000' },
  "LW": { color: '#00008B', borderColor: '#00005F' }
};

/**
 * Gets a consistent color for a club
 * @param {string} club - The club name
 * @param {number} opacity - Optional opacity (0-1) for the color
 * @returns {object} Object containing color and borderColor
 */
export const getColorForClub = (club, opacity = 0.7) => {
  if (!club) return { color: 'rgba(150, 150, 150, ' + opacity + ')', borderColor: 'rgb(100, 100, 100)' };
  
  // Check if we have a predefined color
  const predefined = CLUB_COLORS[club];
  if (predefined) {
    // If opacity requested is different from default, adjust the color
    if (opacity !== 0.7) {
      // Extract RGB from the color and create a new rgba string
      const hex = predefined.color.replace('#', '');
      const r = parseInt(hex.substring(0, 2), 16);
      const g = parseInt(hex.substring(2, 4), 16);
      const b = parseInt(hex.substring(4, 6), 16);
      return {
        color: `rgba(${r}, ${g}, ${b}, ${opacity})`,
        borderColor: predefined.borderColor
      };
    }
    return predefined;
  }
  
  // Generate a color based on the club name
  const hue = Math.abs(club.charCodeAt(0) * 15) % 360;
  return {
    color: `hsla(${hue}, 70%, 50%, ${opacity})`,
    borderColor: `hsl(${hue}, 70%, 40%)`
  };
};

/**
 * Generate a parabolic trajectory for a golf shot
 * @param {Object} shot - Shot data object
 * @param {number} numPoints - Number of points to generate
 * @returns {Array} Array of {x,y} coordinates
 */
export const generateTrajectoryPoints = (shot, numPoints = 50) => {
  // Handle cases where we don't have enough data
  if (!shot || !shot.carryDistance || shot.carryDistance <= 0) {
    console.warn("Missing carry distance for trajectory calculation");
    return [];
  }
  
  // Use reasonable defaults for missing data, based on club type
  const clubType = shot.club?.toLowerCase() || '';
  let defaultLaunchAngle = 15; // Default for mid irons
  
  // Adjust default launch angle based on club type
  if (clubType.includes('driver') || clubType.includes('wood')) {
    defaultLaunchAngle = 12;
  } else if (clubType.includes('wedge') || 
             clubType.includes('pw') || 
             clubType.includes('gw') || 
             clubType.includes('sw') || 
             clubType.includes('lw')) {
    defaultLaunchAngle = 28;
  } else if (clubType.includes('9') || clubType.includes('8')) {
    defaultLaunchAngle = 22;
  } else if (clubType.includes('7') || clubType.includes('6')) {
    defaultLaunchAngle = 18;
  } else if (clubType.includes('5') || clubType.includes('4') || clubType.includes('3')) {
    defaultLaunchAngle = 15;
  }
  
  // Use actual data when available, otherwise use calculated defaults
  const launchAngle = shot.launchAngle || defaultLaunchAngle;
  const totalDistance = shot.carryDistance;
  
  // Calculate approximate apex if missing
  // Formula: apex ≈ (carry distance * tan(launch angle)) / 4
  // The division factor adjusts for air resistance and gravity
  const calculatedApex = totalDistance * Math.tan(launchAngle * Math.PI / 180) * 0.25;
  const maxHeight = shot.apex || calculatedApex;
  
  // Generate x positions from 0 to total distance
  const xPositions = Array.from({ length: numPoints }, (_, i) => 
    (i / (numPoints - 1)) * totalDistance
  );
  
  // Calculate simplified parabolic trajectory
  return xPositions.map(x => {
    // Parabola equation: y = 4 * h * (x/d) * (1 - x/d), where h is max height and d is total distance
    const normalizedX = x / totalDistance;
    const y = 4 * maxHeight * normalizedX * (1 - normalizedX);
    
    return { x, y };
  });
};

/**
 * Safely extract numerical value from shot data
 * @param {Object} shot - Shot data object
 * @param {string} property - Property name to extract
 * @param {number} defaultValue - Default value if property is missing
 * @returns {number} The extracted value or default
 */
export const getShotValue = (shot, property, defaultValue = 0) => {
  if (!shot) return defaultValue;
  
  const value = shot[property];
  if (value === undefined || value === null || isNaN(value)) {
    return defaultValue;
  }
  
  return Number(value);
};

/**
 * Get descriptive text for shot quality based on metrics
 * @param {Object} shot - Shot data object
 * @returns {string} Quality description
 */
export const getShotQuality = (shot) => {
  if (!shot) return 'Unknown';
  
  // Use shot classification if available
  if (shot.shotClassification) {
    return shot.shotClassification;
  }
  
  // For shots without classification, infer from other metrics
  if (shot.deviation) {
    const absDeviation = Math.abs(shot.deviation);
    if (absDeviation < 5) return 'Straight';
    if (absDeviation < 15) {
      return shot.deviation > 0 ? 'Slight Fade/Push' : 'Slight Draw/Pull';
    }
    return shot.deviation > 0 ? 'Slice/Push' : 'Hook/Pull';
  }
  
  return 'Unknown';
};

/**
 * Format numbers for display in tooltips and labels
 * @param {number} value - Value to format
 * @param {number} precision - Number of decimal places
 * @returns {string} Formatted value
 */
export const formatNumber = (value, precision = 1) => {
  if (value === null || value === undefined || isNaN(value)) {
    return 'N/A';
  }
  return Number(value).toFixed(precision);
};
