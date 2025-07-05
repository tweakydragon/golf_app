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
            <div class="card-header d-flex justify-content-between align-items-center">
              <h5 class="mb-0"><i class="bi bi-graph-up"></i> Shot Flight Path Visualization</h5>
              <div class="zoom-controls">
                <button class="btn btn-sm btn-outline-secondary" @click="zoomOut" :disabled="zoomLevel <= 0.5">
                  <i class="bi bi-zoom-out"></i>
                </button>
                <span class="mx-2 small">{{ Math.round(zoomLevel * 100) }}%</span>
                <button class="btn btn-sm btn-outline-secondary" @click="zoomIn" :disabled="zoomLevel >= 3">
                  <i class="bi bi-zoom-in"></i>
                </button>
                <button class="btn btn-sm btn-outline-secondary ms-2" @click="resetZoom">
                  <i class="bi bi-arrows-fullscreen"></i>
                </button>
              </div>
            </div>
            <div class="card-body">
              <div class="row">
                <div class="col-12 mb-4">
                  <div class="shot-trajectory-visual">
                    <h6 class="text-center mb-3">3D Ball Flight Profile</h6>
                    <div class="trajectory-container-enhanced">
                      <canvas ref="trajectoryCanvas" width="800" height="400" @wheel="handleWheel" @mousedown="startPan" @mousemove="handlePan" @mouseup="endPan"></canvas>
                    </div>
                    <div class="trajectory-controls mt-3">
                      <div class="row">
                        <div class="col-md-8">
                          <div class="trajectory-legend">
                            <small class="text-muted">
                              <strong>Carry:</strong> {{ formatValue(shot.carryDistance) || 'N/A' }} yds |
                              <strong>Total:</strong> {{ formatValue(shot.totalDistance) || 'N/A' }} yds |
                              <strong>Peak Height:</strong> {{ formatValue(shot.apex || shot.peakHeight) || 'N/A' }} ft |
                              <strong>Launch Angle:</strong> {{ formatValue(shot.launchAngle) || 'N/A' }}°
                            </small>
                          </div>
                        </div>
                        <div class="col-md-4 text-end">
                          <div class="view-toggle">
                            <button class="btn btn-sm" :class="viewMode === 'side' ? 'btn-primary' : 'btn-outline-primary'" @click="viewMode = 'side'">
                              Side View
                            </button>
                            <button class="btn btn-sm ms-1" :class="viewMode === 'top' ? 'btn-primary' : 'btn-outline-primary'" @click="viewMode = 'top'">
                              Top View
                            </button>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              
              <!-- Dispersion Analysis -->
              <div class="row mt-4">
                <div class="col-md-6 mb-3">
                  <div class="shot-dispersion-visual">
                    <h6 class="text-center mb-3">Shot Landing Pattern</h6>
                    <div class="dispersion-container-enhanced">
                      <canvas ref="dispersionCanvas" width="400" height="300"></canvas>
                    </div>
                    <div class="dispersion-legend mt-2">
                      <small class="text-muted">
                        <span v-if="shot.deviation">
                          <strong>Deviation:</strong> {{ shot.deviation > 0 ? 'Right' : 'Left' }} {{ formatValue(Math.abs(shot.deviation)) }} ft
                        </span>
                        <span v-else-if="shot.totalLateralDistance">
                          <strong>Lateral:</strong> {{ shot.totalLateralDistance > 0 ? 'Right' : 'Left' }} {{ formatValue(Math.abs(shot.totalLateralDistance)) }} yds
                        </span>
                        <span v-else>
                          Dispersion data not available
                        </span>
                      </small>
                    </div>
                  </div>
                </div>
                <div class="col-md-6 mb-3">
                  <div class="shot-metrics-visual">
                    <h6 class="text-center mb-3">Flight Characteristics</h6>
                    <div class="metrics-chart">
                      <canvas ref="metricsCanvas" width="400" height="300"></canvas>
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
const metricsCanvas = ref(null);

// Visualization state
const zoomLevel = ref(1);
const viewMode = ref('side'); // 'side' or 'top'
const panOffset = ref({ x: 0, y: 0 });
const isPanning = ref(false);
const lastPanPoint = ref({ x: 0, y: 0 });

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

// Zoom and pan controls
const zoomIn = () => {
  if (zoomLevel.value < 3) {
    zoomLevel.value = Math.min(3, zoomLevel.value * 1.2);
    redrawAll();
  }
};

const zoomOut = () => {
  if (zoomLevel.value > 0.5) {
    zoomLevel.value = Math.max(0.5, zoomLevel.value / 1.2);
    redrawAll();
  }
};

const resetZoom = () => {
  zoomLevel.value = 1;
  panOffset.value = { x: 0, y: 0 };
  redrawAll();
};

const handleWheel = (event) => {
  event.preventDefault();
  if (event.deltaY < 0) {
    zoomIn();
  } else {
    zoomOut();
  }
};

const startPan = (event) => {
  isPanning.value = true;
  lastPanPoint.value = { x: event.clientX, y: event.clientY };
};

const handlePan = (event) => {
  if (!isPanning.value) return;
  
  const deltaX = event.clientX - lastPanPoint.value.x;
  const deltaY = event.clientY - lastPanPoint.value.y;
  
  panOffset.value.x += deltaX;
  panOffset.value.y += deltaY;
  
  lastPanPoint.value = { x: event.clientX, y: event.clientY };
  redrawAll();
};

const endPan = () => {
  isPanning.value = false;
};

const drawTrajectory = () => {
  const canvas = trajectoryCanvas.value;
  if (!canvas) return;
  
  const ctx = canvas.getContext('2d');
  const width = canvas.width;
  const height = canvas.height;
  
  // Clear canvas
  ctx.clearRect(0, 0, width, height);
  
  // Draw background gradient
  const gradient = ctx.createLinearGradient(0, 0, 0, height);
  gradient.addColorStop(0, '#87CEEB'); // Sky blue
  gradient.addColorStop(0.7, '#E0F6FF'); // Light blue
  gradient.addColorStop(1, '#90EE90'); // Light green (ground)
  ctx.fillStyle = gradient;
  ctx.fillRect(0, 0, width, height);
  
  if (!props.shot.carryDistance && !props.shot.totalDistance) {
    ctx.fillStyle = '#6c757d';
    ctx.font = '16px Arial';
    ctx.textAlign = 'center';
    ctx.fillText('No trajectory data available', width / 2, height / 2);
    return;
  }
  
  // Calculate trajectory parameters
  const carryDist = props.shot.carryDistance || props.shot.totalDistance || 200;
  const totalDist = props.shot.totalDistance || carryDist;
  const maxHeight = props.shot.apex || props.shot.peakHeight || (carryDist * 0.15);
  const launchAngle = props.shot.launchAngle || 15;
  const lateralDeviation = props.shot.deviation || props.shot.totalLateralDistance || 0;
  
  // Apply zoom and pan transformations
  ctx.save();
  ctx.translate(panOffset.value.x, panOffset.value.y);
  ctx.scale(zoomLevel.value, zoomLevel.value);
  
  const margin = 40;
  const drawWidth = width - (2 * margin);
  const drawHeight = height - (2 * margin);
  
  if (viewMode.value === 'side') {
    drawSideView(ctx, width, height, carryDist, totalDist, maxHeight, launchAngle, margin);
  } else {
    drawTopView(ctx, width, height, carryDist, totalDist, lateralDeviation, margin);
  }
  
  ctx.restore();
};

const drawSideView = (ctx, width, height, carryDist, totalDist, maxHeight, launchAngle, margin) => {
  const drawWidth = width - (2 * margin);
  const drawHeight = height - (2 * margin);
  
  // Calculate scales
  const maxDist = Math.max(totalDist, 300);
  const maxH = Math.max(maxHeight, 50);
  const scaleX = drawWidth / maxDist;
  const scaleY = drawHeight / maxH;
  
  // Draw distance markers every 50 yards
  ctx.strokeStyle = '#ccc';
  ctx.lineWidth = 1;
  ctx.font = '12px Arial';
  ctx.fillStyle = '#666';
  ctx.textAlign = 'center';
  
  for (let dist = 50; dist <= maxDist; dist += 50) {
    const x = margin + dist * scaleX;
    ctx.setLineDash([2, 2]);
    ctx.beginPath();
    ctx.moveTo(x, margin);
    ctx.lineTo(x, height - margin);
    ctx.stroke();
    ctx.setLineDash([]);
    
    // Distance labels
    ctx.fillText(`${dist}y`, x, height - margin + 15);
  }
  
  // Draw height markers every 25 feet
  for (let h = 25; h <= maxH; h += 25) {
    const y = height - margin - h * scaleY;
    ctx.setLineDash([2, 2]);
    ctx.beginPath();
    ctx.moveTo(margin, y);
    ctx.lineTo(width - margin, y);
    ctx.stroke();
    ctx.setLineDash([]);
    
    // Height labels
    ctx.textAlign = 'right';
    ctx.fillText(`${h}ft`, margin - 5, y + 4);
  }
  
  // Draw ground line
  ctx.strokeStyle = '#2d5016';
  ctx.lineWidth = 3;
  ctx.beginPath();
  ctx.moveTo(margin, height - margin);
  ctx.lineTo(width - margin, height - margin);
  ctx.stroke();
  
  // Calculate and draw realistic trajectory
  ctx.strokeStyle = '#ff4444';
  ctx.lineWidth = 4;
  ctx.beginPath();
  
  const steps = 100;
  for (let i = 0; i <= steps; i++) {
    const t = i / steps;
    const x = carryDist * t;
    
    // More realistic parabolic trajectory using physics
    const g = 32.2; // gravity ft/s²
    const v0 = Math.sqrt((carryDist * g) / Math.sin(2 * launchAngle * Math.PI / 180));
    const vx = v0 * Math.cos(launchAngle * Math.PI / 180);
    const vy0 = v0 * Math.sin(launchAngle * Math.PI / 180);
    
    const time = (x * 3) / vx; // Convert yards to feet for calculation
    let y = (vy0 * time - 0.5 * g * time * time) / 3; // Convert back to yards
    
    // Ensure we don't go below ground
    y = Math.max(0, y);
    
    const canvasX = margin + x * scaleX;
    const canvasY = height - margin - y * scaleY;
    
    if (i === 0) {
      ctx.moveTo(canvasX, canvasY);
    } else {
      ctx.lineTo(canvasX, canvasY);
    }
  }
  ctx.stroke();
  
  // Draw roll distance if available
  if (totalDist > carryDist) {
    ctx.strokeStyle = '#ffc107';
    ctx.lineWidth = 3;
    ctx.setLineDash([8, 4]);
    ctx.beginPath();
    ctx.moveTo(margin + carryDist * scaleX, height - margin);
    ctx.lineTo(margin + totalDist * scaleX, height - margin);
    ctx.stroke();
    ctx.setLineDash([]);
  }
  
  // Mark key points
  // Launch point
  ctx.fillStyle = '#28a745';
  ctx.beginPath();
  ctx.arc(margin, height - margin, 6, 0, 2 * Math.PI);
  ctx.fill();
  
  // Peak point
  const peakX = margin + (carryDist / 2) * scaleX;
  const peakY = height - margin - maxHeight * scaleY;
  ctx.fillStyle = '#17a2b8';
  ctx.beginPath();
  ctx.arc(peakX, peakY, 5, 0, 2 * Math.PI);
  ctx.fill();
  
  // Landing point
  ctx.fillStyle = '#dc3545';
  ctx.beginPath();
  ctx.arc(margin + carryDist * scaleX, height - margin, 6, 0, 2 * Math.PI);
  ctx.fill();
  
  // Final position
  if (totalDist > carryDist) {
    ctx.fillStyle = '#ffc107';
    ctx.beginPath();
    ctx.arc(margin + totalDist * scaleX, height - margin, 6, 0, 2 * Math.PI);
    ctx.fill();
  }
  
  // Add labels with better positioning
  ctx.fillStyle = '#333';
  ctx.font = '14px Arial';
  ctx.textAlign = 'center';
  ctx.fillText('Tee', margin, height - margin + 25);
  ctx.fillText('Peak', peakX, peakY - 10);
  ctx.fillText('Land', margin + carryDist * scaleX, height - margin + 25);
  if (totalDist > carryDist) {
    ctx.fillText('Final', margin + totalDist * scaleX, height - margin + 25);
  }
};

const drawTopView = (ctx, width, height, carryDist, totalDist, lateralDeviation, margin) => {
  const drawWidth = width - (2 * margin);
  const drawHeight = height - (2 * margin);
  
  // Calculate scales
  const maxDist = Math.max(totalDist, 300);
  const maxLateral = Math.max(Math.abs(lateralDeviation), 50);
  const scaleX = drawWidth / maxDist;
  const scaleY = drawHeight / (maxLateral * 2);
  
  // Draw distance markers every 50 yards
  ctx.strokeStyle = '#ccc';
  ctx.lineWidth = 1;
  ctx.font = '12px Arial';
  ctx.fillStyle = '#666';
  ctx.textAlign = 'center';
  
  for (let dist = 50; dist <= maxDist; dist += 50) {
    const x = margin + dist * scaleX;
    ctx.setLineDash([2, 2]);
    ctx.beginPath();
    ctx.moveTo(x, margin);
    ctx.lineTo(x, height - margin);
    ctx.stroke();
    ctx.setLineDash([]);
    
    ctx.fillText(`${dist}y`, x, height - margin + 15);
  }
  
  // Draw center line (target line)
  ctx.strokeStyle = '#28a745';
  ctx.lineWidth = 2;
  ctx.setLineDash([10, 5]);
  ctx.beginPath();
  ctx.moveTo(margin, height / 2);
  ctx.lineTo(width - margin, height / 2);
  ctx.stroke();
  ctx.setLineDash([]);
  
  // Draw lateral markers
  for (let lat = 25; lat <= maxLateral; lat += 25) {
    // Right side
    const yRight = height / 2 - lat * scaleY;
    ctx.strokeStyle = '#ccc';
    ctx.lineWidth = 1;
    ctx.setLineDash([2, 2]);
    ctx.beginPath();
    ctx.moveTo(margin, yRight);
    ctx.lineTo(width - margin, yRight);
    ctx.stroke();
    
    // Left side
    const yLeft = height / 2 + lat * scaleY;
    ctx.beginPath();
    ctx.moveTo(margin, yLeft);
    ctx.lineTo(width - margin, yLeft);
    ctx.stroke();
    ctx.setLineDash([]);
    
    // Labels
    ctx.textAlign = 'right';
    ctx.fillText(`${lat}y R`, margin - 5, yRight + 4);
    ctx.fillText(`${lat}y L`, margin - 5, yLeft + 4);
  }
  
  // Draw shot path
  ctx.strokeStyle = '#ff4444';
  ctx.lineWidth = 4;
  ctx.beginPath();
  ctx.moveTo(margin, height / 2);
  
  // Simple curve for lateral movement
  const endY = height / 2 - lateralDeviation * scaleY;
  const endX = margin + totalDist * scaleX;
  
  // Curve the shot path
  const cpX = margin + (totalDist * 0.3) * scaleX;
  const cpY = height / 2 - (lateralDeviation * 0.2) * scaleY;
  
  ctx.quadraticCurveTo(cpX, cpY, endX, endY);
  ctx.stroke();
  
  // Mark key points
  ctx.fillStyle = '#28a745';
  ctx.beginPath();
  ctx.arc(margin, height / 2, 6, 0, 2 * Math.PI);
  ctx.fill();
  
  ctx.fillStyle = '#dc3545';
  ctx.beginPath();
  ctx.arc(endX, endY, 6, 0, 2 * Math.PI);
  ctx.fill();
  
  // Labels
  ctx.fillStyle = '#333';
  ctx.font = '14px Arial';
  ctx.textAlign = 'center';
  ctx.fillText('Tee', margin, height / 2 + 20);
  ctx.fillText('Ball', endX, endY + 20);
};

const drawDispersion = () => {
  const canvas = dispersionCanvas.value;
  if (!canvas) return;
  
  const ctx = canvas.getContext('2d');
  const width = canvas.width;
  const height = canvas.height;
  
  // Clear canvas
  ctx.clearRect(0, 0, width, height);
  
  // Draw grass background
  const grassGradient = ctx.createRadialGradient(width/2, height/2, 0, width/2, height/2, Math.max(width, height)/2);
  grassGradient.addColorStop(0, '#90EE90');
  grassGradient.addColorStop(1, '#228B22');
  ctx.fillStyle = grassGradient;
  ctx.fillRect(0, 0, width, height);
  
  const margin = 30;
  const distance = props.shot.totalDistance || props.shot.carryDistance || 200;
  const deviation = props.shot.deviation || props.shot.totalLateralDistance || 0;
  
  // Scale factors
  const maxDist = Math.max(distance, 250);
  const maxDeviation = Math.max(Math.abs(deviation) || 50, 50);
  const scaleY = (height - 2 * margin) / maxDist;
  const scaleX = (width - 2 * margin) / (maxDeviation * 2);
  
  // Draw distance markers every 50 yards
  ctx.strokeStyle = 'rgba(255, 255, 255, 0.8)';
  ctx.lineWidth = 1;
  ctx.font = '11px Arial';
  ctx.fillStyle = '#333';
  ctx.textAlign = 'left';
  
  for (let d = 50; d <= maxDist; d += 50) {
    const y = height - margin - (d * scaleY);
    ctx.setLineDash([3, 3]);
    ctx.beginPath();
    ctx.moveTo(margin, y);
    ctx.lineTo(width - margin, y);
    ctx.stroke();
    ctx.setLineDash([]);
    ctx.fillText(`${d}y`, 5, y + 4);
  }
  
  // Draw target line (center)
  ctx.strokeStyle = '#FFD700';
  ctx.lineWidth = 3;
  ctx.setLineDash([8, 4]);
  ctx.beginPath();
  ctx.moveTo(width / 2, margin);
  ctx.lineTo(width / 2, height - margin);
  ctx.stroke();
  ctx.setLineDash([]);
  
  // Draw fairway boundaries (approximate)
  const fairwayWidth = Math.min(width * 0.4, 120);
  ctx.strokeStyle = 'rgba(255, 255, 255, 0.6)';
  ctx.lineWidth = 2;
  ctx.setLineDash([5, 10]);
  ctx.beginPath();
  ctx.moveTo(width / 2 - fairwayWidth / 2, margin);
  ctx.lineTo(width / 2 - fairwayWidth / 2, height - margin);
  ctx.moveTo(width / 2 + fairwayWidth / 2, margin);
  ctx.lineTo(width / 2 + fairwayWidth / 2, height - margin);
  ctx.stroke();
  ctx.setLineDash([]);
  
  // Calculate shot position
  const shotX = width / 2 + (deviation * scaleX);
  const shotY = height - margin - (distance * scaleY);
  
  // Draw shot trail
  ctx.strokeStyle = 'rgba(255, 68, 68, 0.7)';
  ctx.lineWidth = 3;
  ctx.setLineDash([]);
  ctx.beginPath();
  ctx.moveTo(width / 2, height - margin);
  ctx.lineTo(shotX, shotY);
  ctx.stroke();
  
  // Draw landing area (rough estimate)
  ctx.fillStyle = 'rgba(255, 68, 68, 0.2)';
  ctx.beginPath();
  ctx.arc(shotX, shotY, 15, 0, 2 * Math.PI);
  ctx.fill();
  
  // Draw shot position
  ctx.fillStyle = '#dc3545';
  ctx.strokeStyle = '#fff';
  ctx.lineWidth = 2;
  ctx.beginPath();
  ctx.arc(shotX, shotY, 8, 0, 2 * Math.PI);
  ctx.fill();
  ctx.stroke();
  
  // Draw target
  ctx.strokeStyle = '#FFD700';
  ctx.lineWidth = 3;
  ctx.beginPath();
  ctx.arc(width / 2, shotY, 12, 0, 2 * Math.PI);
  ctx.stroke();
  
  // Add lateral markers
  ctx.fillStyle = '#333';
  ctx.font = '11px Arial';
  ctx.textAlign = 'center';
  
  for (let lat = 25; lat <= maxDeviation; lat += 25) {
    // Right side markers
    const xRight = width / 2 + lat * scaleX;
    if (xRight < width - margin) {
      ctx.fillText(`${lat}y`, xRight, height - margin + 15);
    }
    
    // Left side markers
    const xLeft = width / 2 - lat * scaleX;
    if (xLeft > margin) {
      ctx.fillText(`${lat}y`, xLeft, height - margin + 15);
    }
  }
  
  // Add labels
  ctx.fillStyle = '#333';
  ctx.font = '12px Arial';
  ctx.textAlign = 'center';
  ctx.fillText('Tee', width / 2, height - margin + 25);
  ctx.fillText('Target', width / 2, shotY - 20);
  
  // Direction indicators
  ctx.textAlign = 'center';
  ctx.font = '10px Arial';
  ctx.fillStyle = '#666';
  ctx.fillText('Left', margin + 15, height - 10);
  ctx.fillText('Right', width - margin - 15, height - 10);
};

const drawMetrics = () => {
  const canvas = metricsCanvas.value;
  if (!canvas) return;
  
  const ctx = canvas.getContext('2d');
  const width = canvas.width;
  const height = canvas.height;
  
  // Clear canvas
  ctx.clearRect(0, 0, width, height);
  
  // Draw background
  ctx.fillStyle = '#f8f9fa';
  ctx.fillRect(0, 0, width, height);
  
  // Draw title
  ctx.fillStyle = '#333';
  ctx.font = '16px Arial';
  ctx.textAlign = 'center';
  ctx.fillText('Flight Characteristics', width / 2, 25);
  
  // Key metrics to display
  const metrics = [
    { label: 'Ball Speed', value: props.shot.ballSpeed, unit: 'mph', color: '#007bff' },
    { label: 'Launch Angle', value: props.shot.launchAngle, unit: '°', color: '#28a745' },
    { label: 'Spin Rate', value: props.shot.spinRate, unit: 'rpm', color: '#dc3545' },
    { label: 'Peak Height', value: props.shot.apex || props.shot.peakHeight, unit: 'ft', color: '#ffc107' },
  ];
  
  const barHeight = 30;
  const barSpacing = 45;
  const startY = 60;
  const maxBarWidth = width - 120;
  
  // Find max value for scaling
  const maxValues = {
    'Ball Speed': 200,
    'Launch Angle': 30,
    'Spin Rate': 5000,
    'Peak Height': 150
  };
  
  metrics.forEach((metric, index) => {
    if (metric.value != null) {
      const y = startY + index * barSpacing;
      const maxVal = maxValues[metric.label] || 100;
      const barWidth = (metric.value / maxVal) * maxBarWidth;
      
      // Draw bar background
      ctx.fillStyle = '#e9ecef';
      ctx.fillRect(80, y, maxBarWidth, barHeight);
      
      // Draw bar
      ctx.fillStyle = metric.color;
      ctx.fillRect(80, y, Math.max(barWidth, 0), barHeight);
      
      // Draw label
      ctx.fillStyle = '#333';
      ctx.font = '12px Arial';
      ctx.textAlign = 'right';
      ctx.fillText(metric.label, 75, y + 20);
      
      // Draw value
      ctx.textAlign = 'left';
      const displayValue = typeof metric.value === 'number' ? 
        (metric.label === 'Spin Rate' ? metric.value.toLocaleString() : metric.value.toFixed(1)) : 
        'N/A';
      ctx.fillText(`${displayValue} ${metric.unit}`, 85 + Math.max(barWidth, 40), y + 20);
    }
  });
};

const redrawAll = () => {
  nextTick(() => {
    drawTrajectory();
    drawDispersion();
    drawMetrics();
  });
};

watch(() => props.shot, () => {
  redrawAll();
}, { deep: true });

watch(() => viewMode.value, () => {
  redrawAll();
});

onMounted(() => {
  redrawAll();
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

.trajectory-container-enhanced,
.dispersion-container-enhanced,
.metrics-chart {
  display: flex;
  justify-content: center;
  align-items: center;
  background: #ffffff;
  border: 2px solid #dee2e6;
  border-radius: 0.5rem;
  padding: 1rem;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  position: relative;
  overflow: hidden;
}

.trajectory-container-enhanced canvas {
  cursor: grab;
  transition: cursor 0.2s;
}

.trajectory-container-enhanced canvas:active {
  cursor: grabbing;
}

.trajectory-legend,
.dispersion-legend {
  text-align: center;
}

.zoom-controls {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.zoom-controls .btn {
  padding: 0.25rem 0.5rem;
  font-size: 0.875rem;
}

.view-toggle {
  display: flex;
  gap: 0.25rem;
}

.view-toggle .btn {
  padding: 0.25rem 0.75rem;
  font-size: 0.875rem;
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
