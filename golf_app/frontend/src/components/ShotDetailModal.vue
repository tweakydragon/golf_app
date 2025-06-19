<template>
  <div class="modal fade" :id="modalId" tabindex="-1" :aria-labelledby="modalId + 'Label'" aria-hidden="true">
    <div class="modal-dialog modal-xl modal-dialog-scrollable">
      <div class="modal-content">
        <div class="modal-header">
          <h1 class="modal-title h4" :id="modalId + 'Label'">
            <i class="bi bi-bullseye"></i> Shot #{{ shot.shotNumber }} Details
          </h1>
          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>
        <div class="modal-body">
          <!-- Shot Overview Card -->
          <div class="card mb-4">
            <div class="card-header">
              <h5 class="mb-0"><i class="bi bi-info-circle"></i> Shot Overview</h5>
            </div>
            <div class="card-body">
              <div class="row">
                <div class="col-md-6">
                  <div class="shot-info-grid">
                    <div class="info-item">
                      <label>Club:</label>
                      <span class="fw-bold">{{ shot.club }}</span>
                    </div>
                    <div class="info-item" v-if="shot.clubDescription">
                      <label>Club Description:</label>
                      <span>{{ shot.clubDescription }}</span>
                    </div>
                    <div class="info-item" v-if="shot.shotTime">
                      <label>Shot Time:</label>
                      <span>{{ formatDateTime(shot.shotTime) }}</span>
                    </div>
                    <div class="info-item" v-if="shot.shotClassification">
                      <label>Shot Classification:</label>
                      <span class="badge bg-secondary">{{ shot.shotClassification }}</span>
                    </div>
                  </div>
                </div>
                <div class="col-md-6">
                  <div class="shot-metrics-summary">
                    <div class="metric-card">
                      <div class="metric-icon">
                        <i class="bi bi-arrow-up-right text-success"></i>
                      </div>
                      <div class="metric-info">
                        <div class="metric-label">Total Distance</div>
                        <div class="metric-value">{{ formatValue(shot.totalDistance) }}<span class="unit">yds</span></div>
                      </div>
                    </div>
                    <div class="metric-card">
                      <div class="metric-icon">
                        <i class="bi bi-speedometer2 text-primary"></i>
                      </div>
                      <div class="metric-info">
                        <div class="metric-label">Ball Speed</div>
                        <div class="metric-value">{{ formatValue(shot.ballSpeed) }}<span class="unit">mph</span></div>
                      </div>
                    </div>
                    <div class="metric-card" v-if="shot.smash">
                      <div class="metric-icon">
                        <i class="bi bi-lightning-fill text-warning"></i>
                      </div>
                      <div class="metric-info">
                        <div class="metric-label">Smash Factor</div>
                        <div class="metric-value">{{ formatValue(shot.smash) }}</div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Key Metrics Cards -->
          <div class="row mb-4">
            <div class="col-md-4 mb-3">
              <div class="card h-100">
                <div class="card-header bg-light">
                  <h6 class="mb-0"><i class="bi bi-bullseye"></i> Distance Metrics</h6>
                </div>
                <div class="card-body">
                  <div class="metrics-list">
                    <div class="metric-row">
                      <span class="metric-label">Carry Distance:</span>
                      <span class="metric-value">{{ formatValue(shot.carryDistance, 'yds') }}</span>
                    </div>
                    <div class="metric-row">
                      <span class="metric-label">Total Distance:</span>
                      <span class="metric-value">{{ formatValue(shot.totalDistance, 'yds') }}</span>
                    </div>
                    <div class="metric-row" v-if="shot.rollDistance">
                      <span class="metric-label">Roll Distance:</span>
                      <span class="metric-value">{{ formatValue(shot.rollDistance, 'yds') }}</span>
                    </div>
                    <div class="metric-row" v-if="shot.deviation">
                      <span class="metric-label">Deviation:</span>
                      <span class="metric-value">{{ formatValue(shot.deviation, 'ft') }}</span>
                    </div>
                    <div class="metric-row" v-if="shot.apex">
                      <span class="metric-label">Apex:</span>
                      <span class="metric-value">{{ formatValue(shot.apex, 'ft') }}</span>
                    </div>
                    <div class="metric-row" v-if="shot.peakHeight">
                      <span class="metric-label">Peak Height:</span>
                      <span class="metric-value">{{ formatValue(shot.peakHeight, 'ft') }}</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <div class="col-md-4 mb-3">
              <div class="card h-100">
                <div class="card-header bg-light">
                  <h6 class="mb-0"><i class="bi bi-speedometer2"></i> Speed & Launch</h6>
                </div>
                <div class="card-body">
                  <div class="metrics-list">
                    <div class="metric-row">
                      <span class="metric-label">Ball Speed:</span>
                      <span class="metric-value">{{ formatValue(shot.ballSpeed, 'mph') }}</span>
                    </div>
                    <div class="metric-row">
                      <span class="metric-label">Club Speed:</span>
                      <span class="metric-value">{{ formatValue(shot.clubHeadSpeed, 'mph') }}</span>
                    </div>
                    <div class="metric-row">
                      <span class="metric-label">Launch Angle:</span>
                      <span class="metric-value">{{ formatValue(shot.launchAngle, '°') }}</span>
                    </div>
                    <div class="metric-row" v-if="shot.launchDirection">
                      <span class="metric-label">Launch Direction:</span>
                      <span class="metric-value">{{ formatValue(shot.launchDirection, '°') }}</span>
                    </div>
                    <div class="metric-row" v-if="shot.horizontalLaunch">
                      <span class="metric-label">Horizontal Launch:</span>
                      <span class="metric-value">{{ formatValue(shot.horizontalLaunch, '°') }}</span>
                    </div>
                    <div class="metric-row" v-if="shot.descentAngle">
                      <span class="metric-label">Descent Angle:</span>
                      <span class="metric-value">{{ formatValue(shot.descentAngle, '°') }}</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <div class="col-md-4 mb-3">
              <div class="card h-100">
                <div class="card-header bg-light">
                  <h6 class="mb-0"><i class="bi bi-arrow-repeat"></i> Spin & Angles</h6>
                </div>
                <div class="card-body">
                  <div class="metrics-list">
                    <div class="metric-row">
                      <span class="metric-label">Spin Rate:</span>
                      <span class="metric-value">{{ formatValue(shot.spinRate, 'rpm', true) }}</span>
                    </div>
                    <div class="metric-row" v-if="shot.spinAxis">
                      <span class="metric-label">Spin Axis:</span>
                      <span class="metric-value">{{ formatValue(shot.spinAxis, '°') }}</span>
                    </div>
                    <div class="metric-row" v-if="shot.attackAngle">
                      <span class="metric-label">Attack Angle:</span>
                      <span class="metric-value">{{ formatValue(shot.attackAngle, '°') }}</span>
                    </div>
                    <div class="metric-row" v-if="shot.faceAngle">
                      <span class="metric-label">Face Angle:</span>
                      <span class="metric-value">{{ formatValue(shot.faceAngle, '°') }}</span>
                    </div>
                    <div class="metric-row" v-if="shot.swingPath">
                      <span class="metric-label">Swing Path:</span>
                      <span class="metric-value">{{ formatValue(shot.swingPath, '°') }}</span>
                    </div>
                    <div class="metric-row" v-if="shot.faceToPath">
                      <span class="metric-label">Face to Path:</span>
                      <span class="metric-value">{{ formatValue(shot.faceToPath, '°') }}</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Advanced Metrics (Awesome Golf) -->
          <div v-if="hasAwesomeGolfMetrics" class="row mb-4">
            <div class="col-md-6 mb-3">
              <div class="card h-100">
                <div class="card-header bg-info text-white">
                  <h6 class="mb-0"><i class="bi bi-gear"></i> Advanced Club Metrics</h6>
                </div>
                <div class="card-body">
                  <div class="metrics-list">
                    <div class="metric-row" v-if="shot.dynamicLoft">
                      <span class="metric-label">Dynamic Loft:</span>
                      <span class="metric-value">{{ formatValue(shot.dynamicLoft, '°') }}</span>
                    </div>
                    <div class="metric-row" v-if="shot.spinLoft">
                      <span class="metric-label">Spin Loft:</span>
                      <span class="metric-value">{{ formatValue(shot.spinLoft, '°') }}</span>
                    </div>
                    <div class="metric-row" v-if="shot.lowPoint">
                      <span class="metric-label">Low Point:</span>
                      <span class="metric-value">{{ formatValue(shot.lowPoint, 'in') }}</span>
                    </div>
                    <div class="metric-row" v-if="shot.swingPlane">
                      <span class="metric-label">Swing Plane:</span>
                      <span class="metric-value">{{ formatValue(shot.swingPlane, '°') }}</span>
                    </div>
                    <div class="metric-row" v-if="shot.verticalFaceImpact">
                      <span class="metric-label">Vertical Face Impact:</span>
                      <span class="metric-value">{{ formatValue(shot.verticalFaceImpact, 'in') }}</span>
                    </div>
                    <div class="metric-row" v-if="shot.horizontalFaceImpact">
                      <span class="metric-label">Horizontal Face Impact:</span>
                      <span class="metric-value">{{ formatValue(shot.horizontalFaceImpact, 'in') }}</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <div class="col-md-6 mb-3">
              <div class="card h-100">
                <div class="card-header bg-success text-white">
                  <h6 class="mb-0"><i class="bi bi-crosshair"></i> Lateral & Curve Metrics</h6>
                </div>
                <div class="card-body">
                  <div class="metrics-list">
                    <div class="metric-row" v-if="shot.carryLateralDistance">
                      <span class="metric-label">Carry Lateral:</span>
                      <span class="metric-value">{{ formatValue(shot.carryLateralDistance, 'yds') }}</span>
                    </div>
                    <div class="metric-row" v-if="shot.totalLateralDistance">
                      <span class="metric-label">Total Lateral:</span>
                      <span class="metric-value">{{ formatValue(shot.totalLateralDistance, 'yds') }}</span>
                    </div>
                    <div class="metric-row" v-if="shot.carryCurveDistance">
                      <span class="metric-label">Carry Curve:</span>
                      <span class="metric-value">{{ formatValue(shot.carryCurveDistance, 'yds') }}</span>
                    </div>
                    <div class="metric-row" v-if="shot.totalCurveDistance">
                      <span class="metric-label">Total Curve:</span>
                      <span class="metric-value">{{ formatValue(shot.totalCurveDistance, 'yds') }}</span>
                    </div>
                    <div class="metric-row" v-if="shot.faceTarget">
                      <span class="metric-label">Face to Target:</span>
                      <span class="metric-value">{{ formatValue(shot.faceTarget, '°') }}</span>
                    </div>
                    <div class="metric-row" v-if="shot.altitude">
                      <span class="metric-label">Altitude:</span>
                      <span class="metric-value">{{ formatValue(shot.altitude, 'ft') }}</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Shot Visualization -->
          <div class="card">
            <div class="card-header">
              <h5 class="mb-0"><i class="bi bi-graph-up"></i> Shot Visualization</h5>
            </div>
            <div class="card-body">
              <div class="row">
                <div class="col-md-6 mb-3">
                  <div class="shot-trajectory-visual">
                    <h6 class="text-center mb-3">Ball Flight Profile</h6>
                    <div class="trajectory-container">
                      <canvas ref="trajectoryCanvas" width="400" height="200"></canvas>
                    </div>
                    <div class="trajectory-legend mt-2">
                      <small class="text-muted">
                        Carry: {{ formatValue(shot.carryDistance) || 'N/A' }} yds | 
                        Total: {{ formatValue(shot.totalDistance) || 'N/A' }} yds |
                        Peak: {{ formatValue(shot.apex || shot.peakHeight) || 'N/A' }} ft
                      </small>
                    </div>
                  </div>
                </div>
                <div class="col-md-6 mb-3">
                  <div class="shot-dispersion-visual">
                    <h6 class="text-center mb-3">Shot Dispersion</h6>
                    <div class="dispersion-container">
                      <canvas ref="dispersionCanvas" width="400" height="200"></canvas>
                    </div>
                    <div class="dispersion-legend mt-2">
                      <small class="text-muted">
                        <span v-if="shot.deviation">
                          Deviation: {{ shot.deviation > 0 ? 'Right' : 'Left' }} {{ formatValue(Math.abs(shot.deviation)) }} ft
                        </span>
                        <span v-else-if="shot.totalLateralDistance">
                          Lateral: {{ shot.totalLateralDistance > 0 ? 'Right' : 'Left' }} {{ formatValue(Math.abs(shot.totalLateralDistance)) }} yds
                        </span>
                        <span v-else>
                          Dispersion data not available
                        </span>
                      </small>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch, nextTick } from 'vue';

const props = defineProps({
  shot: {
    type: Object,
    required: true
  },
  modalId: {
    type: String,
    default: 'shotDetailModal'
  }
});

const trajectoryCanvas = ref(null);
const dispersionCanvas = ref(null);

const hasAwesomeGolfMetrics = computed(() => {
  return props.shot.smash || 
         props.shot.dynamicLoft || 
         props.shot.carryLateralDistance || 
         props.shot.rollDistance;
});

const formatValue = (value, unit = '', addCommas = false) => {
  if (value === null || value === undefined) return 'N/A';
  
  const numValue = typeof value === 'number' ? value : parseFloat(value);
  if (isNaN(numValue)) return 'N/A';
  
  let formatted;
  if (addCommas) {
    formatted = numValue.toLocaleString('en-US', {
      minimumFractionDigits: 1,
      maximumFractionDigits: 1
    });
  } else {
    formatted = numValue.toFixed(1);
  }
  
  return unit ? `${formatted} ${unit}` : formatted;
};

const formatDateTime = (dateString) => {
  if (!dateString) return 'N/A';
  
  const date = new Date(dateString);
  return new Intl.DateTimeFormat('en-US', {
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
    hour12: true
  }).format(date);
};

const drawTrajectory = () => {
  const canvas = trajectoryCanvas.value;
  if (!canvas) return;
  
  const ctx = canvas.getContext('2d');
  const width = canvas.width;
  const height = canvas.height;
  
  // Clear canvas
  ctx.clearRect(0, 0, width, height);
  
  // Draw background
  ctx.fillStyle = '#f8f9fa';
  ctx.fillRect(0, 0, width, height);
  
  // Draw ground line
  ctx.strokeStyle = '#28a745';
  ctx.lineWidth = 2;
  ctx.beginPath();
  ctx.moveTo(20, height - 20);
  ctx.lineTo(width - 20, height - 20);
  ctx.stroke();
  
  if (!props.shot.carryDistance && !props.shot.totalDistance) {
    ctx.fillStyle = '#6c757d';
    ctx.font = '14px Arial';
    ctx.textAlign = 'center';
    ctx.fillText('No trajectory data available', width / 2, height / 2);
    return;
  }
  
  // Calculate trajectory points
  const carryDist = props.shot.carryDistance || props.shot.totalDistance || 200;
  const totalDist = props.shot.totalDistance || carryDist;
  const maxHeight = props.shot.apex || props.shot.peakHeight || (carryDist * 0.15);
  
  const scaleX = (width - 40) / Math.max(totalDist, 250);
  const scaleY = (height - 60) / Math.max(maxHeight, 50);
  
  // Draw carry trajectory (arc)
  ctx.strokeStyle = '#007bff';
  ctx.lineWidth = 3;
  ctx.beginPath();
  ctx.moveTo(20, height - 20);
  
  for (let x = 0; x <= carryDist; x += carryDist / 20) {
    const progress = x / carryDist;
    // Parabolic trajectory
    const y = 4 * maxHeight * progress * (1 - progress);
    const canvasX = 20 + x * scaleX;
    const canvasY = height - 20 - y * scaleY;
    ctx.lineTo(canvasX, canvasY);
  }
  ctx.stroke();
  
  // Draw roll distance if available
  if (totalDist > carryDist) {
    ctx.strokeStyle = '#ffc107';
    ctx.lineWidth = 2;
    ctx.setLineDash([5, 5]);
    ctx.beginPath();
    ctx.moveTo(20 + carryDist * scaleX, height - 20);
    ctx.lineTo(20 + totalDist * scaleX, height - 20);
    ctx.stroke();
    ctx.setLineDash([]);
  }
  
  // Mark landing point
  ctx.fillStyle = '#dc3545';
  ctx.beginPath();
  ctx.arc(20 + carryDist * scaleX, height - 20, 4, 0, 2 * Math.PI);
  ctx.fill();
  
  // Mark final position
  if (totalDist > carryDist) {
    ctx.fillStyle = '#ffc107';
    ctx.beginPath();
    ctx.arc(20 + totalDist * scaleX, height - 20, 4, 0, 2 * Math.PI);
    ctx.fill();
  }
  
  // Add labels
  ctx.fillStyle = '#333';
  ctx.font = '12px Arial';
  ctx.textAlign = 'center';
  ctx.fillText('Launch', 20, height - 5);
  ctx.fillText('Landing', 20 + carryDist * scaleX, height - 5);
  if (totalDist > carryDist) {
    ctx.fillText('Final', 20 + totalDist * scaleX, height - 5);
  }
};

const drawDispersion = () => {
  const canvas = dispersionCanvas.value;
  if (!canvas) return;
  
  const ctx = canvas.getContext('2d');
  const width = canvas.width;
  const height = canvas.height;
  
  // Clear canvas
  ctx.clearRect(0, 0, width, height);
  
  // Draw background
  ctx.fillStyle = '#f8f9fa';
  ctx.fillRect(0, 0, width, height);
  
  // Draw target line (center)
  ctx.strokeStyle = '#28a745';
  ctx.lineWidth = 2;
  ctx.setLineDash([5, 5]);
  ctx.beginPath();
  ctx.moveTo(width / 2, 20);
  ctx.lineTo(width / 2, height - 20);
  ctx.stroke();
  ctx.setLineDash([]);
  
  // Calculate shot position
  const distance = props.shot.totalDistance || props.shot.carryDistance || 200;
  const deviation = props.shot.deviation || props.shot.totalLateralDistance || 0;
  
  // Scale factors
  const maxDist = Math.max(distance, 250);
  const maxDeviation = Math.max(Math.abs(deviation), 30);
  const scaleY = (height - 40) / maxDist;
  const scaleX = (width - 40) / (maxDeviation * 2);
  
  // Calculate shot position
  const shotX = width / 2 + (deviation * scaleX);
  const shotY = height - 20 - (distance * scaleY);
  
  // Draw shot position
  ctx.fillStyle = '#dc3545';
  ctx.beginPath();
  ctx.arc(shotX, shotY, 6, 0, 2 * Math.PI);
  ctx.fill();
  
  // Draw target
  ctx.strokeStyle = '#28a745';
  ctx.lineWidth = 2;
  ctx.beginPath();
  ctx.arc(width / 2, height - 20 - (distance * scaleY), 8, 0, 2 * Math.PI);
  ctx.stroke();
  
  // Add distance markers
  ctx.fillStyle = '#6c757d';
  ctx.font = '10px Arial';
  ctx.textAlign = 'left';
  for (let d = 50; d <= maxDist; d += 50) {
    const y = height - 20 - (d * scaleY);
    ctx.fillText(`${d}y`, 5, y + 3);
  }
  
  // Add labels
  ctx.fillStyle = '#333';
  ctx.font = '12px Arial';
  ctx.textAlign = 'center';
  ctx.fillText('Left', 20, height - 5);
  ctx.fillText('Target', width / 2, height - 5);
  ctx.fillText('Right', width - 20, height - 5);
};

watch(() => props.shot, () => {
  nextTick(() => {
    drawTrajectory();
    drawDispersion();
  });
}, { deep: true });

onMounted(() => {
  nextTick(() => {
    drawTrajectory();
    drawDispersion();
  });
});
</script>

<style scoped>
.shot-info-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 0.5rem;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.25rem 0;
  border-bottom: 1px solid #e9ecef;
}

.info-item label {
  font-weight: 500;
  color: #6c757d;
  margin: 0;
}

.shot-metrics-summary {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.metric-card {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1rem;
  background: #f8f9fa;
  border-radius: 0.5rem;
  border: 1px solid #e9ecef;
}

.metric-icon {
  font-size: 1.5rem;
}

.metric-info {
  flex: 1;
}

.metric-label {
  font-size: 0.875rem;
  color: #6c757d;
  margin-bottom: 0.25rem;
}

.metric-value {
  font-size: 1.25rem;
  font-weight: 600;
  color: #333;
}

.unit {
  font-size: 0.875rem;
  font-weight: normal;
  color: #6c757d;
  margin-left: 0.25rem;
}

.metrics-list {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.metric-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.5rem 0;
  border-bottom: 1px solid #f1f3f4;
}

.metric-row:last-child {
  border-bottom: none;
}

.metric-row .metric-label {
  font-weight: 500;
  color: #6c757d;
  font-size: 0.875rem;
}

.metric-row .metric-value {
  font-weight: 600;
  color: #333;
  font-size: 0.875rem;
}

.trajectory-container,
.dispersion-container {
  display: flex;
  justify-content: center;
  align-items: center;
  background: #f8f9fa;
  border: 1px solid #dee2e6;
  border-radius: 0.375rem;
  padding: 1rem;
}

.trajectory-legend,
.dispersion-legend {
  text-align: center;
}

.modal-xl {
  max-width: 1200px;
}

@media (max-width: 768px) {
  .shot-metrics-summary {
    gap: 0.5rem;
  }
  
  .metric-card {
    padding: 0.75rem;
  }
  
  .metric-value {
    font-size: 1rem;
  }
}
</style>
