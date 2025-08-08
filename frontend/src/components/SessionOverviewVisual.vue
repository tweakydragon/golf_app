<template>
  <div 
    class="session-visual-container"
    :class="{
      'minimized': isMinimized,
      'hover-enlarged': isHoverEnlarged,
      'fullscreen': isFullscreen
    }"
    ref="visualContainer"
  >
    <!-- Full Screen Mode -->
    <div v-if="isFullscreen" class="fullscreen-overlay" @click="exitFullscreen">
      <div class="fullscreen-content" @click.stop>
        <div class="fullscreen-header">
          <h2>{{ session?.title }} - Shot Overview</h2>
          <button class="btn btn-outline-light" @click="exitFullscreen">
            <i class="bi bi-x-lg"></i>
          </button>
        </div>
        
        <div class="fullscreen-body">
          <!-- Data Table Column -->
          <div class="data-column">
            <div class="data-table-container">
              <h4>Session Statistics</h4>
              <table class="table table-dark table-sm">
                <tbody>
                  <tr>
                    <td>Total Shots</td>
                    <td>{{ shots.length }}</td>
                  </tr>
                  <tr>
                    <td>Avg Distance</td>
                    <td>{{ averageDistance.toFixed(1) }} yds</td>
                  </tr>
                  <tr>
                    <td>Best Shot</td>
                    <td>{{ bestDistance.toFixed(1) }} yds</td>
                  </tr>
                  <tr>
                    <td>Avg Ball Speed</td>
                    <td>{{ averageBallSpeed.toFixed(1) }} mph</td>
                  </tr>
                </tbody>
              </table>

              <h4 class="mt-4">Shot Details</h4>
              <div class="shot-list">
                <div 
                  v-for="(shot, index) in shots" 
                  :key="shot.id"
                  class="shot-item"
                  :class="{ 'highlighted': highlightedShotIndex === index }"
                  @mouseenter="highlightShot(index)"
                  @mouseleave="highlightShot(null)"
                >
                  <div class="shot-header">
                    <strong>Shot #{{ shot.shotNumber }}</strong>
                    <span class="shot-club">{{ shot.club }}</span>
                  </div>
                  <div class="shot-metrics">
                    <span>{{ (shot.totalDistance || shot.carryDistance || 0).toFixed(1) }} yds</span>
                    <span>{{ (shot.ballSpeed || 0).toFixed(1) }} mph</span>
                    <span>{{ (shot.launchAngle || 0).toFixed(1) }}°</span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Visualization Column -->
          <div class="visual-column">
            <div class="canvas-container">
              <canvas 
                ref="fullscreenCanvas" 
                width="800" 
                height="600"
                @mousemove="handleCanvasMouseMove"
                @mouseleave="highlightShot(null)"
              ></canvas>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Regular/Minimized Mode -->
    <div v-else class="visual-content">
      <div class="visual-header" v-if="!isMinimized">
        <h3>Shot Overview</h3>
        <div class="view-controls">
          <button 
            class="btn btn-sm" 
            :class="viewMode === 'overhead' ? 'btn-primary' : 'btn-outline-primary'"
            @click="viewMode = 'overhead'"
          >
            Overhead
          </button>
          <button 
            class="btn btn-sm ms-1" 
            :class="viewMode === 'side' ? 'btn-primary' : 'btn-outline-primary'"
            @click="viewMode = 'side'"
          >
            Side View
          </button>
        </div>
      </div>

      <div 
        class="canvas-wrapper"
        @mouseenter="onVisualHover"
        @mouseleave="onVisualLeave"
        @click="enterFullscreen"
      >
        <canvas 
          ref="mainCanvas" 
          :width="canvasWidth" 
          :height="canvasHeight"
          @mousemove="handleCanvasMouseMove"
          @mouseleave="highlightShot(null)"
        ></canvas>
        
        <!-- Hover hint -->
        <div v-if="isMinimized && isHoverEnlarged" class="hover-hint">
          Click for full view
        </div>
      </div>

      <!-- Legend -->
      <div v-if="!isMinimized" class="legend">
        <div class="legend-item">
          <div class="legend-color" style="background: #007bff;"></div>
          <span>Shot Paths</span>
        </div>
        <div class="legend-item">
          <div class="legend-color" style="background: #dc3545;"></div>
          <span>Landing Points</span>
        </div>
        <div class="legend-item">
          <div class="legend-color" style="background: #ffc107;"></div>
          <span>Highlighted Shot</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch, nextTick } from 'vue';

const props = defineProps({
  shots: {
    type: Array,
    required: true
  },
  session: {
    type: Object,
    required: true
  },
  highlightedShotIndex: {
    type: Number,
    default: null
  },
  isMinimized: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(['shot-highlight', 'toggle-minimize']);

// Component state
const visualContainer = ref(null);
const mainCanvas = ref(null);
const fullscreenCanvas = ref(null);
const viewMode = ref('overhead'); // 'overhead' or 'side'
const isFullscreen = ref(false);
const isHoverEnlarged = ref(false);

// Canvas dimensions
const canvasWidth = computed(() => {
  if (props.isMinimized) {
    return isHoverEnlarged.value ? 384 : 320; // 20% larger when hovered
  }
  return 800;
});

const canvasHeight = computed(() => {
  if (props.isMinimized) {
    return isHoverEnlarged.value ? 288 : 240; // 20% larger when hovered
  }
  return 400;
});

// Statistics
const averageDistance = computed(() => {
  const distances = props.shots.map(shot => shot.totalDistance || shot.carryDistance || 0).filter(d => d > 0);
  return distances.length > 0 ? distances.reduce((sum, d) => sum + d, 0) / distances.length : 0;
});

const bestDistance = computed(() => {
  const distances = props.shots.map(shot => shot.totalDistance || shot.carryDistance || 0);
  return Math.max(...distances, 0);
});

const averageBallSpeed = computed(() => {
  const speeds = props.shots.map(shot => shot.ballSpeed || 0).filter(s => s > 0);
  return speeds.length > 0 ? speeds.reduce((sum, s) => sum + s, 0) / speeds.length : 0;
});

// Methods
const highlightShot = (index) => {
  emit('shot-highlight', index);
  redraw();
};

const onVisualHover = () => {
  if (props.isMinimized) {
    isHoverEnlarged.value = true;
    nextTick(() => redraw());
  }
};

const onVisualLeave = () => {
  if (props.isMinimized) {
    isHoverEnlarged.value = false;
    nextTick(() => redraw());
  }
};

const enterFullscreen = () => {
  if (props.isMinimized || !isFullscreen.value) {
    isFullscreen.value = true;
    nextTick(() => drawFullscreenCanvas());
  }
};

const exitFullscreen = () => {
  isFullscreen.value = false;
  nextTick(() => redraw());
};

const handleCanvasMouseMove = (event) => {
  const canvas = event.target;
  const rect = canvas.getBoundingClientRect();
  const x = event.clientX - rect.left;
  const y = event.clientY - rect.top;
  
  // Find shot near mouse position
  const shotIndex = findShotAtPosition(x, y, canvas);
  if (shotIndex !== null && shotIndex !== props.highlightedShotIndex) {
    highlightShot(shotIndex);
  }
};

const findShotAtPosition = (x, y, canvas) => {
  const width = canvas.width;
  const height = canvas.height;
  const margin = 40;
  
  if (viewMode.value === 'overhead') {
    return findShotAtPositionOverhead(x, y, width, height, margin);
  } else {
    return findShotAtPositionSide(x, y, width, height, margin);
  }
};

const findShotAtPositionOverhead = (x, y, width, height, margin) => {
  const maxDistance = Math.max(...props.shots.map(shot => 
    shot.totalDistance || shot.carryDistance || 200), 300);
  const maxLateral = Math.max(...props.shots.map(shot => 
    Math.abs(shot.deviation || shot.totalLateralDistance || 0)), 50);
  
  const scaleX = (width - 2 * margin) / maxDistance;
  const scaleY = (height - 2 * margin) / (maxLateral * 2);
  
  for (let i = 0; i < props.shots.length; i++) {
    const shot = props.shots[i];
    const distance = shot.totalDistance || shot.carryDistance || 0;
    const lateral = shot.deviation || shot.totalLateralDistance || shot.carryLateralDistance || 0;
    const launchDirection = shot.launchDirection || shot.horizontalLaunch || 0;
    const spinAxis = shot.spinAxis || 0;
    
    if (distance > 0) {
      // Check along the flight path, not just the landing point
      const steps = 10;
      for (let step = 0; step <= steps; step++) {
        const t = step / steps;
        const currentDistance = distance * t;
        
        // Calculate lateral movement (same logic as drawing)
        let currentLateral = 0;
        
        // Use multiple approaches to create realistic curves (same as drawing)
        if (Math.abs(launchDirection) > 0.1) {
          const launchEffect = launchDirection * Math.sin(t * Math.PI * 0.8) * 0.4;
          currentLateral += launchEffect;
        }
        
        if (Math.abs(spinAxis) > 0.1) {
          const spinEffect = (spinAxis / 45) * lateral * Math.sin(t * Math.PI * 1.2) * t;
          currentLateral += spinEffect;
        }
        
        if (Math.abs(lateral) > 0.1) {
          const finalCurve = lateral * (3 * t * t - 2 * t * t * t);
          currentLateral += finalCurve;
        }
        
        // Add shot classification effects
        if (shot.shotClassification) {
          const classification = shot.shotClassification.toLowerCase();
          if (classification.includes('slice')) {
            currentLateral += (distance / 200) * 10 * Math.pow(t, 2.5);
          } else if (classification.includes('hook') || classification.includes('draw')) {
            currentLateral -= (distance / 200) * 8 * Math.pow(t, 2.5);
          } else if (classification.includes('fade')) {
            currentLateral += (distance / 200) * 5 * Math.pow(t, 2);
          }
        }
        
        const shotX = margin + currentDistance * scaleX;
        const shotY = height / 2 - currentLateral * scaleY;
        
        const dx = x - shotX;
        const dy = y - shotY;
        const distance2 = dx * dx + dy * dy;
        
        if (distance2 < 144) { // 12px radius for easier selection along path
          return i;
        }
      }
    }
  }
  return null;
};

const findShotAtPositionSide = (x, y, width, height, margin) => {
  const maxDistance = Math.max(...props.shots.map(shot => 
    shot.totalDistance || shot.carryDistance || 200), 300);
  const maxHeight = Math.max(...props.shots.map(shot => 
    shot.apex || shot.peakHeight || 0), 100);
  
  const scaleX = (width - 2 * margin) / maxDistance;
  const scaleY = (height - 2 * margin) / maxHeight;
  
  for (let i = 0; i < props.shots.length; i++) {
    const shot = props.shots[i];
    const distance = shot.totalDistance || shot.carryDistance || 0;
    
    const shotX = margin + distance * scaleX;
    const shotY = height - margin; // Landing point
    
    const dx = x - shotX;
    const dy = y - shotY;
    const distance2 = dx * dx + dy * dy;
    
    if (distance2 < 100) { // 10px radius
      return i;
    }
  }
  return null;
};

const drawOverheadView = (ctx, width, height) => {
  const margin = 40;
  
  // Clear and setup
  ctx.clearRect(0, 0, width, height);
  
  // Background gradient (golf course)
  const grassGradient = ctx.createRadialGradient(width/2, height/2, 0, width/2, height/2, Math.max(width, height)/2);
  grassGradient.addColorStop(0, '#90EE90');
  grassGradient.addColorStop(1, '#228B22');
  ctx.fillStyle = grassGradient;
  ctx.fillRect(0, 0, width, height);
  
  // Calculate scales
  const maxDistance = Math.max(...props.shots.map(shot => 
    shot.totalDistance || shot.carryDistance || 200), 300);
  const maxLateral = Math.max(...props.shots.map(shot => 
    Math.abs(shot.deviation || shot.totalLateralDistance || 0)), 50);
  
  const scaleX = (width - 2 * margin) / maxDistance;
  const scaleY = (height - 2 * margin) / (maxLateral * 2);
  
  // Draw distance markers every 50 yards
  ctx.strokeStyle = 'rgba(255, 255, 255, 0.6)';
  ctx.lineWidth = 1;
  ctx.font = '10px Arial';
  ctx.fillStyle = '#333';
  ctx.textAlign = 'center';
  
  for (let dist = 50; dist <= maxDistance; dist += 50) {
    const x = margin + dist * scaleX;
    ctx.setLineDash([3, 3]);
    ctx.beginPath();
    ctx.moveTo(x, margin);
    ctx.lineTo(x, height - margin);
    ctx.stroke();
    ctx.setLineDash([]);
    ctx.fillText(`${dist}y`, x, height - margin + 15);
  }
  
  // Draw target line
  ctx.strokeStyle = '#FFD700';
  ctx.lineWidth = 2;
  ctx.setLineDash([10, 5]);
  ctx.beginPath();
  ctx.moveTo(margin, height / 2);
  ctx.lineTo(width - margin, height / 2);
  ctx.stroke();
  ctx.setLineDash([]);
  
  // Draw tee box
  ctx.fillStyle = '#8B4513';
  ctx.fillRect(margin - 10, height / 2 - 5, 20, 10);
  
  // Draw all shots with curved flight paths
  props.shots.forEach((shot, index) => {
    const distance = shot.totalDistance || shot.carryDistance || 0;
    const lateral = shot.deviation || shot.totalLateralDistance || shot.carryLateralDistance || 0;
    const launchDirection = shot.launchDirection || shot.horizontalLaunch || 0;
    const spinAxis = shot.spinAxis || 0;
    
    // Debug log for first shot to see available data (remove after testing)
    if (index === 0 && !window.debugLogged) {
      console.log('Shot data:', {
        distance,
        lateral,
        launchDirection,
        spinAxis,
        totalLateralDistance: shot.totalLateralDistance,
        carryLateralDistance: shot.carryLateralDistance,
        deviation: shot.deviation,
        horizontalLaunch: shot.horizontalLaunch,
        shotClassification: shot.shotClassification
      });
      window.debugLogged = true;
    }
    
    if (distance > 0) {
      const shotX = margin + distance * scaleX;
      const shotY = height / 2 - lateral * scaleY;
      
      // Calculate flight path curve
      ctx.strokeStyle = props.highlightedShotIndex === index ? '#ffc107' : '#007bff';
      ctx.lineWidth = props.highlightedShotIndex === index ? 3 : 2;
      ctx.beginPath();
      ctx.moveTo(margin, height / 2);
      
      // Draw curved flight path based on shot data
      const steps = 20;
      for (let i = 1; i <= steps; i++) {
        const t = i / steps;
        const currentDistance = distance * t;
        
        // Calculate lateral movement based on available data
        let currentLateral = 0;
        
        // Use multiple approaches to create realistic curves
        if (Math.abs(launchDirection) > 0.1) {
          // Initial launch direction effect (more prominent early in flight)
          const launchEffect = launchDirection * Math.sin(t * Math.PI * 0.8) * 0.4;
          currentLateral += launchEffect;
        }
        
        if (Math.abs(spinAxis) > 0.1) {
          // Spin axis effect (creates draw/fade)
          const spinEffect = (spinAxis / 45) * lateral * Math.sin(t * Math.PI * 1.2) * t;
          currentLateral += spinEffect;
        }
        
        if (Math.abs(lateral) > 0.1) {
          // Final lateral position creates a curve
          const finalCurve = lateral * (3 * t * t - 2 * t * t * t); // Smooth S-curve
          currentLateral += finalCurve;
        }
        
        // Add some natural variation based on shot classification
        if (shot.shotClassification) {
          const classification = shot.shotClassification.toLowerCase();
          if (classification.includes('slice')) {
            // Slice curves more to the right late in flight
            currentLateral += (distance / 200) * 10 * Math.pow(t, 2.5);
          } else if (classification.includes('hook') || classification.includes('draw')) {
            // Hook/draw curves left
            currentLateral -= (distance / 200) * 8 * Math.pow(t, 2.5);
          } else if (classification.includes('fade')) {
            // Fade curves slightly right
            currentLateral += (distance / 200) * 5 * Math.pow(t, 2);
          }
        }
        
        const currentX = margin + currentDistance * scaleX;
        const currentY = height / 2 - currentLateral * scaleY;
        
        ctx.lineTo(currentX, currentY);
      }
      ctx.stroke();
      
      // Draw flight path points for highlighted shot
      if (props.highlightedShotIndex === index) {
        ctx.fillStyle = 'rgba(255, 193, 7, 0.3)';
        for (let i = 0; i <= steps; i += 5) {
          const t = i / steps;
          const currentDistance = distance * t;
          let currentLateral = 0;
          
          // Use same calculation as main drawing
          if (Math.abs(launchDirection) > 0.1) {
            const launchEffect = launchDirection * Math.sin(t * Math.PI * 0.8) * 0.4;
            currentLateral += launchEffect;
          }
          
          if (Math.abs(spinAxis) > 0.1) {
            const spinEffect = (spinAxis / 45) * lateral * Math.sin(t * Math.PI * 1.2) * t;
            currentLateral += spinEffect;
          }
          
          if (Math.abs(lateral) > 0.1) {
            const finalCurve = lateral * (3 * t * t - 2 * t * t * t);
            currentLateral += finalCurve;
          }
          
          if (shot.shotClassification) {
            const classification = shot.shotClassification.toLowerCase();
            if (classification.includes('slice')) {
              currentLateral += (distance / 200) * 10 * Math.pow(t, 2.5);
            } else if (classification.includes('hook') || classification.includes('draw')) {
              currentLateral -= (distance / 200) * 8 * Math.pow(t, 2.5);
            } else if (classification.includes('fade')) {
              currentLateral += (distance / 200) * 5 * Math.pow(t, 2);
            }
          }
          
          const currentX = margin + currentDistance * scaleX;
          const currentY = height / 2 - currentLateral * scaleY;
          
          ctx.beginPath();
          ctx.arc(currentX, currentY, 2, 0, 2 * Math.PI);
          ctx.fill();
        }
      }
      
      // Landing point
      ctx.fillStyle = props.highlightedShotIndex === index ? '#ffc107' : '#dc3545';
      ctx.beginPath();
      ctx.arc(shotX, shotY, props.highlightedShotIndex === index ? 8 : 6, 0, 2 * Math.PI);
      ctx.fill();
      
      // Shot number for highlighted shot
      if (props.highlightedShotIndex === index) {
        ctx.fillStyle = '#000';
        ctx.font = 'bold 10px Arial';
        ctx.textAlign = 'center';
        ctx.fillText(shot.shotNumber, shotX, shotY + 3);
      }
    }
  });
  
  // Labels
  ctx.fillStyle = '#333';
  ctx.font = '12px Arial';
  ctx.textAlign = 'center';
  ctx.fillText('TEE', margin, height / 2 + 20);
};

const drawSideView = (ctx, width, height) => {
  const margin = 40;
  
  // Clear and setup
  ctx.clearRect(0, 0, width, height);
  
  // Background gradient (sky to ground)
  const skyGradient = ctx.createLinearGradient(0, 0, 0, height);
  skyGradient.addColorStop(0, '#87CEEB');
  skyGradient.addColorStop(0.7, '#E0F6FF');
  skyGradient.addColorStop(1, '#90EE90');
  ctx.fillStyle = skyGradient;
  ctx.fillRect(0, 0, width, height);
  
  // Calculate scales
  const maxDistance = Math.max(...props.shots.map(shot => 
    shot.totalDistance || shot.carryDistance || 200), 300);
  const maxHeight = Math.max(...props.shots.map(shot => 
    shot.apex || shot.peakHeight || 0), 100);
  
  const scaleX = (width - 2 * margin) / maxDistance;
  const scaleY = (height - 2 * margin) / maxHeight;
  
  // Draw distance markers every 50 yards
  ctx.strokeStyle = 'rgba(255, 255, 255, 0.8)';
  ctx.lineWidth = 1;
  ctx.font = '10px Arial';
  ctx.fillStyle = '#333';
  ctx.textAlign = 'center';
  
  for (let dist = 50; dist <= maxDistance; dist += 50) {
    const x = margin + dist * scaleX;
    ctx.setLineDash([2, 2]);
    ctx.beginPath();
    ctx.moveTo(x, margin);
    ctx.lineTo(x, height - margin);
    ctx.stroke();
    ctx.setLineDash([]);
    ctx.fillText(`${dist}y`, x, height - margin + 15);
  }
  
  // Draw ground line
  ctx.strokeStyle = '#2d5016';
  ctx.lineWidth = 3;
  ctx.beginPath();
  ctx.moveTo(margin, height - margin);
  ctx.lineTo(width - margin, height - margin);
  ctx.stroke();
  
  // Draw all shot trajectories
  props.shots.forEach((shot, index) => {
    const distance = shot.totalDistance || shot.carryDistance || 0;
    const maxH = shot.apex || shot.peakHeight || (distance * 0.15);
    const launchAngle = shot.launchAngle || 15;
    
    if (distance > 0) {
      // Draw trajectory
      ctx.strokeStyle = props.highlightedShotIndex === index ? '#ffc107' : '#007bff';
      ctx.lineWidth = props.highlightedShotIndex === index ? 4 : 2;
      ctx.beginPath();
      
      for (let i = 0; i <= 50; i++) {
        const t = i / 50;
        const x = distance * t;
        const y = 4 * maxH * t * (1 - t); // Parabolic trajectory
        
        const canvasX = margin + x * scaleX;
        const canvasY = height - margin - y * scaleY;
        
        if (i === 0) {
          ctx.moveTo(canvasX, canvasY);
        } else {
          ctx.lineTo(canvasX, canvasY);
        }
      }
      ctx.stroke();
      
      // Landing point
      ctx.fillStyle = props.highlightedShotIndex === index ? '#ffc107' : '#dc3545';
      ctx.beginPath();
      ctx.arc(margin + distance * scaleX, height - margin, props.highlightedShotIndex === index ? 8 : 6, 0, 2 * Math.PI);
      ctx.fill();
      
      // Peak point for highlighted shot
      if (props.highlightedShotIndex === index && maxH > 0) {
        ctx.fillStyle = '#17a2b8';
        ctx.beginPath();
        ctx.arc(margin + (distance / 2) * scaleX, height - margin - maxH * scaleY, 5, 0, 2 * Math.PI);
        ctx.fill();
      }
    }
  });
  
  // Tee marker
  ctx.fillStyle = '#28a745';
  ctx.beginPath();
  ctx.arc(margin, height - margin, 6, 0, 2 * Math.PI);
  ctx.fill();
  
  // Labels
  ctx.fillStyle = '#333';
  ctx.font = '12px Arial';
  ctx.textAlign = 'center';
  ctx.fillText('TEE', margin, height - margin + 20);
};

const redraw = () => {
  nextTick(() => {
    const canvas = mainCanvas.value;
    if (!canvas) return;
    
    const ctx = canvas.getContext('2d');
    const width = canvas.width;
    const height = canvas.height;
    
    if (viewMode.value === 'overhead') {
      drawOverheadView(ctx, width, height);
    } else {
      drawSideView(ctx, width, height);
    }
  });
};

const drawFullscreenCanvas = () => {
  nextTick(() => {
    const canvas = fullscreenCanvas.value;
    if (!canvas) return;
    
    const ctx = canvas.getContext('2d');
    const width = canvas.width;
    const height = canvas.height;
    
    if (viewMode.value === 'overhead') {
      drawOverheadView(ctx, width, height);
    } else {
      drawSideView(ctx, width, height);
    }
  });
};

// Watchers
watch(() => props.shots, redraw, { deep: true });
watch(() => props.highlightedShotIndex, redraw);
watch(() => viewMode.value, () => {
  redraw();
  if (isFullscreen.value) {
    drawFullscreenCanvas();
  }
});
watch(() => props.isMinimized, redraw);
watch(() => isHoverEnlarged.value, redraw);

onMounted(() => {
  redraw();
});
</script>

<style scoped>
.session-visual-container {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  transition: all 0.3s ease;
}

.session-visual-container.minimized {
  position: fixed;
  top: 20px;
  left: 20px;
  z-index: 1000;
  max-width: 320px;
  cursor: pointer;
}

.session-visual-container.minimized.hover-enlarged {
  transform: scale(1.2);
  z-index: 1001;
}

.visual-content {
  padding: 1rem;
}

.visual-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.visual-header h3 {
  margin: 0;
  font-size: 1.2rem;
}

.view-controls {
  display: flex;
  gap: 0.5rem;
}

.canvas-wrapper {
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
  border: 2px solid #dee2e6;
  border-radius: 8px;
  background: #f8f9fa;
  transition: border-color 0.3s ease;
}

.canvas-wrapper:hover {
  border-color: #007bff;
}

.minimized .canvas-wrapper {
  border: 1px solid #ccc;
}

.hover-hint {
  position: absolute;
  bottom: 5px;
  right: 5px;
  background: rgba(0, 0, 0, 0.8);
  color: white;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 10px;
  pointer-events: none;
}

.legend {
  display: flex;
  justify-content: center;
  gap: 1rem;
  margin-top: 1rem;
  font-size: 0.875rem;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.legend-color {
  width: 12px;
  height: 12px;
  border-radius: 2px;
}

/* Fullscreen Styles */
.fullscreen-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.95);
  z-index: 2000;
  display: flex;
  align-items: center;
  justify-content: center;
}

.fullscreen-content {
  width: 95vw;
  height: 90vh;
  background: #1a1a1a;
  border-radius: 12px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.fullscreen-header {
  background: #333;
  color: white;
  padding: 1rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #555;
}

.fullscreen-header h2 {
  margin: 0;
  font-size: 1.5rem;
}

.fullscreen-body {
  flex: 1;
  display: flex;
  height: calc(100% - 80px);
}

.data-column {
  width: 300px;
  background: #2a2a2a;
  border-right: 1px solid #555;
  overflow-y: auto;
}

.data-table-container {
  padding: 1rem;
  color: white;
}

.data-table-container h4 {
  color: #ffc107;
  font-size: 1rem;
  margin-bottom: 0.5rem;
}

.table-dark {
  font-size: 0.875rem;
}

.shot-list {
  max-height: 400px;
  overflow-y: auto;
}

.shot-item {
  background: #333;
  margin-bottom: 0.5rem;
  padding: 0.75rem;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s ease;
  border-left: 3px solid transparent;
}

.shot-item:hover {
  background: #404040;
}

.shot-item.highlighted {
  background: #ffc107;
  color: #000;
  border-left-color: #f59c07;
}

.shot-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.5rem;
}

.shot-club {
  background: rgba(255, 255, 255, 0.2);
  padding: 2px 6px;
  border-radius: 3px;
  font-size: 0.75rem;
}

.shot-item.highlighted .shot-club {
  background: rgba(0, 0, 0, 0.2);
}

.shot-metrics {
  display: flex;
  gap: 1rem;
  font-size: 0.875rem;
  opacity: 0.8;
}

.visual-column {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #1a1a1a;
}

.canvas-container {
  border: 2px solid #555;
  border-radius: 8px;
  overflow: hidden;
}

@media (max-width: 768px) {
  .fullscreen-body {
    flex-direction: column;
  }
  
  .data-column {
    width: 100%;
    height: 200px;
  }
  
  .visual-column {
    height: calc(100% - 200px);
  }
  
  .canvas-container canvas {
    width: 100%;
    height: auto;
  }
}
</style>