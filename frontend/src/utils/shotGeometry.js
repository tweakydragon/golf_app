const DEFAULT_MARGIN = 40;
const DEFAULT_MAX_DISTANCE = 300;
const DEFAULT_MAX_LATERAL = 50;
const DEFAULT_MAX_HEIGHT = 100;

const isNumber = (value) => typeof value === 'number' && Number.isFinite(value);

export const getShotDistance = (shot = {}) =>
  shot.totalDistance || shot.carryDistance || 0;

export const getShotLateral = (shot = {}) =>
  shot.deviation ||
  shot.totalLateralDistance ||
  shot.carryLateralDistance ||
  0;

export const getShotLaunchDirection = (shot = {}) =>
  shot.launchDirection || shot.horizontalLaunch || 0;

export const getShotSpinAxis = (shot = {}) => shot.spinAxis || 0;

export const getShotClassification = (shot = {}) =>
  (shot.shotClassification || '').toLowerCase();

export const getShotApex = (shot = {}) => {
  const explicitApex = shot.apex || shot.peakHeight;
  if (isNumber(explicitApex) && explicitApex > 0) {
    return explicitApex;
  }
  const distance = getShotDistance(shot);
  return distance > 0 ? distance * 0.15 : 0;
};

export const computeOverheadScales = (shots, width, height, margin = DEFAULT_MARGIN) => {
  const maxDistance = Math.max(
    ...(shots || []).map((shot) => getShotDistance(shot)),
    DEFAULT_MAX_DISTANCE
  );
  const maxLateralMagnitude = Math.max(
    ...(shots || []).map((shot) => Math.abs(getShotLateral(shot))),
    DEFAULT_MAX_LATERAL
  );

  const usableWidth = Math.max(width - 2 * margin, 1);
  const usableHeight = Math.max(height - 2 * margin, 1);
  const safeDistance = Math.max(maxDistance, 1);
  const safeLateral = Math.max(maxLateralMagnitude, 1);

  return {
    margin,
    maxDistance,
    maxLateral: maxLateralMagnitude,
    scaleX: usableWidth / safeDistance,
    scaleY: usableHeight / (safeLateral * 2),
  };
};

export const computeSideScales = (shots, width, height, margin = DEFAULT_MARGIN) => {
  const maxDistance = Math.max(
    ...(shots || []).map((shot) => getShotDistance(shot)),
    DEFAULT_MAX_DISTANCE
  );
  const maxHeight = Math.max(
    ...(shots || []).map((shot) => getShotApex(shot)),
    DEFAULT_MAX_HEIGHT
  );

  const usableWidth = Math.max(width - 2 * margin, 1);
  const usableHeight = Math.max(height - 2 * margin, 1);
  const safeDistance = Math.max(maxDistance, 1);
  const safeHeight = Math.max(maxHeight, 1);

  return {
    margin,
    maxDistance,
    maxHeight,
    scaleX: usableWidth / safeDistance,
    scaleY: usableHeight / safeHeight,
  };
};

export const calculateOverheadLateralOffset = (shot, t) => {
  const distance = getShotDistance(shot);
  const lateral = getShotLateral(shot);
  const launchDirection = getShotLaunchDirection(shot);
  const spinAxis = getShotSpinAxis(shot);
  const classification = getShotClassification(shot);

  let currentLateral = 0;

  if (Math.abs(launchDirection) > 0.1) {
    currentLateral += launchDirection * Math.sin(t * Math.PI * 0.8) * 0.4;
  }

  if (Math.abs(spinAxis) > 0.1) {
    currentLateral += (spinAxis / 45) * lateral * Math.sin(t * Math.PI * 1.2) * t;
  }

  if (Math.abs(lateral) > 0.1) {
    currentLateral += lateral * (3 * t * t - 2 * t * t * t);
  }

  if (classification) {
    if (classification.includes('slice')) {
      currentLateral += (distance / 200) * 10 * Math.pow(t, 2.5);
    } else if (classification.includes('hook') || classification.includes('draw')) {
      currentLateral -= (distance / 200) * 8 * Math.pow(t, 2.5);
    } else if (classification.includes('fade')) {
      currentLateral += (distance / 200) * 5 * Math.pow(t, 2);
    }
  }

  return currentLateral;
};

export const generateOverheadPath = (shot, steps = 20) => {
  const distance = getShotDistance(shot);
  if (!isNumber(steps) || steps <= 0 || distance <= 0) {
    return [];
  }

  const path = [];
  for (let i = 0; i <= steps; i += 1) {
    const t = i / steps;
    path.push({
      distance: distance * t,
      lateral: calculateOverheadLateralOffset(shot, t),
    });
  }
  return path;
};

export const getOverheadLandingPoint = (shot) => ({
  distance: getShotDistance(shot),
  lateral: getShotLateral(shot),
});
