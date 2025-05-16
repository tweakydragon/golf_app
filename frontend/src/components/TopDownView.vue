<template>
  <div class="top-down-view">
    <h3 class="text-center mb-3">Top-Down View</h3>
    <div id="top-down-status" class="mb-2 text-center"></div>
    <div class="chart-container">
      <canvas id="top-down-canvas"></canvas>
    </div>
    <div class="mt-2 text-center">
      <small class="text-muted">{{ shotCount }} shots</small>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, watch } from 'vue';

const props = defineProps({
  shots: {
    type: Array,
    required: true,
  },
  clubFilter: {
    type: String,
    default: ''
  }
});

// Computed property for filtered shots
const filteredShots = computed(() => {
  if (!props.clubFilter) return props.shots;
  return props.shots.filter(shot => shot.club === props.clubFilter);
});

// Computed property for shot count
const shotCount = computed(() => {
  return filteredShots.value.length;
});

// Chart instance
let chart = null;

// Import chart utilities from centralized location
import { getColorForClub, formatNumber } from '../utils/chartUtils.js';

// Function to render the chart
const renderChart = () => {
  const status = document.getElementById('top-down-status');
  if (status) status.textContent = "Rendering top-down view...";
  
  console.log("TopDownView: Rendering chart with", filteredShots.value.length, "shots");
  
  // Import Chart.js  
  import('chart.js/auto').then(module => {
    try {
      const Chart = module.default;
      
      // Get canvas element
      const canvas = document.getElementById('top-down-canvas');
      if (!canvas) {
        console.error("Canvas element not found");
        if (status) status.textContent = "Canvas not found";
        return;
      }
      
      const ctx = canvas.getContext('2d');
      if (!ctx) {
        console.error("Could not get 2D context");
        if (status) status.textContent = "Could not get 2D context";
        return;
      }
      
      const filtered = filteredShots.value || [];
      
      if (filtered.length === 0) {
        console.warn("No shots to display for top-down view");
        if (status) status.textContent = "No shots to display";
        return;
      }
      
      console.log("Processing shot data for top-down view");
      
      // Default values in case of missing data
      const defaultMaxDistance = 300;
      const defaultMaxDeviation = 50;
      
      // Calculate the max distance to set a reasonable scale
      const validDistances = filtered.filter(shot => shot.totalDistance).map(shot => shot.totalDistance);
      const maxTotalDistance = validDistances.length > 0 
        ? Math.max(...validDistances) * 1.1 
        : defaultMaxDistance;
      
      // Calculate maximum deviation (right/left distance)
      const getDeviation = (shot) => {
        if (shot.totalLateralDistance !== undefined && shot.totalLateralDistance !== null) {
          return Math.abs(shot.totalLateralDistance);
        }
        if (shot.deviation) {
          return Math.abs(shot.deviation / 3); // Scale factor for deviation
        }
        return 0;
      };
      
      const deviations = filtered.map(shot => getDeviation(shot));
      const maxDeviation = deviations.length > 0 
        ? Math.max(...deviations) * 1.5 
        : defaultMaxDeviation;

      console.log("Max distance:", maxTotalDistance, "Max deviation:", maxDeviation);

      // Clean up previous chart instance if it exists
      if (chart) {
        chart.destroy();
        chart = null;
      }
      
      // Prepare data points
      const dataPoints = filtered.map(shot => {
        // Use totalLateralDistance if available, otherwise fall back to deviation or 0
        const lateralDistance = shot.totalLateralDistance !== undefined && shot.totalLateralDistance !== null
          ? shot.totalLateralDistance
          : (shot.deviation ? shot.deviation / 3 : 0);
        
        return {
          x: lateralDistance,
          y: shot.totalDistance || 0,
          club: shot.club || 'Unknown',
          shotNumber: shot.shotNumber || 0,
          carryDistance: shot.carryDistance,
          totalDistance: shot.totalDistance,
          ballSpeed: shot.ballSpeed
        };
      });

      console.log("Creating chart with", dataPoints.length, "data points");
      
      // Create a new chart
      chart = new Chart(ctx, {
        type: 'scatter',
        data: {
          datasets: [{
            label: 'Shots',
            data: dataPoints,
            backgroundColor: filtered.map(shot => getColorForClub(shot.club || 'Unknown')),
            pointRadius: 6,
            pointHoverRadius: 8
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          animation: {
            duration: 500 // Faster animation for performance
          },
          scales: {
            x: {
              title: {
                display: true,
                text: 'Lateral Distance (yards)'
              },
              min: -maxDeviation,
              max: maxDeviation
            },
            y: {
              title: {
                display: true,
                text: 'Distance (yards)'
              },
              min: 0,
              max: maxTotalDistance
            }
          },
          plugins: {
            tooltip: {
              callbacks: {
                label: (context) => {
                  const point = context.raw;
                  const lines = [`Shot #: ${point.shotNumber || 'N/A'}`];
                  
                  if (point.club) lines.push(`Club: ${point.club}`);
                  if (point.carryDistance) lines.push(`Carry: ${point.carryDistance} yds`);
                  if (point.totalDistance) lines.push(`Total: ${point.totalDistance} yds`);
                  if (point.ballSpeed) lines.push(`Ball Speed: ${point.ballSpeed} mph`);
                  if (point.x !== undefined) {
                    lines.push(`Lateral: ${point.x > 0 ? 'Right' : 'Left'} ${Math.abs(point.x).toFixed(1)} yds`);
                  }
                  
                  return lines;
                }
              }
            },
            legend: {
              display: false
            }
          }
        }
      });
      
      console.log("Top-down chart created successfully");
      if (status) status.textContent = "";
    } catch (error) {
      console.error("Error in chart rendering:", error);
      if (status) status.textContent = "Error: " + error.message;
    }
  }).catch(error => {
    console.error("Error loading Chart.js:", error);
    if (status) status.textContent = "Error loading Chart.js: " + error.message;
  });
};

// Watch for changes in shots or filter
watch(() => props.shots, (newShots) => {
  console.log("TopDownView: Shots data changed, shots count:", newShots?.length);
  renderChart();
}, { deep: true });

watch(() => props.clubFilter, (newFilter) => {
  console.log("TopDownView: Club filter changed to:", newFilter || 'All clubs');
  renderChart();
});

// Initialize chart when component is mounted
onMounted(() => {
  console.log("TopDownView mounted");
  
  // Make sure we give the DOM enough time to initialize
  const timer = setTimeout(() => {
    console.log("TopDownView: Initial render after timeout");
    renderChart();
  }, 1000);
  
  // Cleanup function
  return () => {
    clearTimeout(timer);
    if (chart) {
      console.log("TopDownView: Destroying chart instance");
      chart.destroy();
      chart = null;
    }
  };
});
</script>

<style scoped>
.chart-container {
  position: relative;
  height: 400px;
  width: 100%;
  background-color: #f6f9fc;
  border-radius: 8px;
  border: 1px solid #e0e0e0;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
  overflow: hidden;
}

#top-down-status {
  min-height: 20px;
  color: #666;
  font-size: 0.9rem;
}

canvas {
  display: block;
  width: 100%;
  height: 100%;
}
</style>
