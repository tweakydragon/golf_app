<template>
  <div class="side-view">
    <h3 class="text-center mb-3">Side-On View</h3>
    <div id="side-view-status" class="mb-2 text-center"></div>
    <div class="chart-container">
      <canvas id="side-view-canvas"></canvas>
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
import { getColorForClub, generateTrajectoryPoints, formatNumber } from '../utils/chartUtils.js';

// Function to render the chart
const renderChart = () => {
  const status = document.getElementById('side-view-status');
  if (status) status.textContent = "Rendering side-on view...";
  
  console.log("SideView: Rendering chart with", filteredShots.value.length, "shots");
  
  // Import Chart.js
  import('chart.js/auto').then(module => {
    try {
      const Chart = module.default;
      
      // Get canvas element
      const canvas = document.getElementById('side-view-canvas');
      if (!canvas) {
        console.error("Canvas element not found for side view");
        if (status) status.textContent = "Canvas not found";
        return;
      }
      
      const ctx = canvas.getContext('2d');
      if (!ctx) {
        console.error("Could not get 2D context for side view");
        if (status) status.textContent = "Could not get 2D context";
        return;
      }
      
      const filtered = filteredShots.value || [];
      
      if (filtered.length === 0) {
        console.warn("No shots to display for side-on view");
        if (status) status.textContent = "No shots to display";
        return;
      }
      
      console.log("Processing shot data for side-on view");
      
      // Default values in case of missing data
      const defaultMaxDistance = 300;
      const defaultMaxHeight = 100;
      
      // Calculate max distance and height for chart scaling
      const validDistances = filtered.filter(shot => shot.carryDistance).map(shot => shot.carryDistance);
      const maxDistance = validDistances.length > 0 
        ? Math.max(...validDistances) * 1.1 
        : defaultMaxDistance;
      
      const validHeights = filtered.filter(shot => shot.apex).map(shot => shot.apex);
      let maxHeight = validHeights.length > 0 
        ? Math.max(...validHeights) * 1.2 
        : defaultMaxHeight;
      
      // Ensure minimum height for visualization
      maxHeight = Math.max(maxHeight, 50);
      
      console.log("Max distance:", maxDistance, "Max height:", maxHeight);
      
      // Calculate ground height (0 feet elevation)
      const groundY = 0;

      // Generate datasets for each shot
      const shotDatasets = filtered.map(shot => {
        const trajectoryPoints = generateTrajectoryPoints(shot);
        return {
          label: `${shot.club || 'Unknown'} - Shot #${shot.shotNumber || 0}`,
          data: trajectoryPoints,
          borderColor: getColorForClub(shot.club || 'Unknown'),
          backgroundColor: 'transparent',
          pointRadius: 0,
          pointHoverRadius: 5,
          pointBackgroundColor: getColorForClub(shot.club || 'Unknown'),
          tension: 0.4,
          showLine: true,
          fill: false,
          metadata: {
            shot: {
              club: shot.club || 'Unknown',
              shotNumber: shot.shotNumber || 0,
              carryDistance: shot.carryDistance,
              apex: shot.apex,
              launchAngle: shot.launchAngle,
              ballSpeed: shot.ballSpeed
            }
          }
        };
      });
      
      console.log("Generated", shotDatasets.length, "trajectory datasets");
      
      // Clean up previous chart instance if it exists
      if (chart) {
        chart.destroy();
        chart = null;
      }
      
      // Create a new chart
      chart = new Chart(ctx, {
        type: 'scatter',
        data: {
          datasets: [
            // Ground line
            {
              label: 'Ground',
              data: [
                { x: 0, y: groundY },
                { x: maxDistance, y: groundY }
              ],
              borderColor: '#a9d18e', // Light green for grass
              borderWidth: 2,
              pointRadius: 0,
              showLine: true,
              fill: false
            },
            // Shot trajectories
            ...shotDatasets
          ]
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
                text: 'Distance (yards)'
              },
              min: 0,
              max: maxDistance
            },
            y: {
              title: {
                display: true,
                text: 'Height (feet)'
              },
              min: 0,
              max: maxHeight
            }
          },
          plugins: {
            tooltip: {
              callbacks: {
                title: (tooltipItems) => {
                  const dataset = tooltipItems[0].dataset;
                  const shot = dataset.metadata?.shot;
                  return shot ? `${shot.club} - Shot #${shot.shotNumber}` : '';
                },
                label: (context) => {
                  const dataset = context.dataset;
                  const shot = dataset.metadata?.shot;
                  const point = context.raw;
                  
                  if (!shot) return '';
                  
                  const lines = [];
                  if (point.x !== undefined) lines.push(`Distance: ${point.x.toFixed(1)} yards`);
                  if (point.y !== undefined) lines.push(`Height: ${point.y.toFixed(1)} feet`);
                  if (shot.apex) lines.push(`Apex: ${shot.apex} feet`);
                  if (shot.launchAngle) lines.push(`Launch Angle: ${shot.launchAngle}°`);
                  if (shot.ballSpeed) lines.push(`Ball Speed: ${shot.ballSpeed} mph`);
                  
                  return lines;
                }
              }
            },
            legend: {
              display: false
            }
          },
          interaction: {
            intersect: false,
            mode: 'nearest'
          }
        }
      });
      
      console.log("Side-on chart created successfully");
      if (status) status.textContent = "";
    } catch (error) {
      console.error("Error in side view chart rendering:", error);
      if (status) status.textContent = "Error: " + error.message;
    }
  }).catch(error => {
    console.error("Error loading Chart.js for side view:", error);
    if (status) status.textContent = "Error loading Chart.js: " + error.message;
  });
};

// Watch for changes in shots or filter
watch(() => props.shots, (newShots) => {
  console.log("SideView: Shots data changed, shots count:", newShots?.length);
  renderChart();
}, { deep: true });

watch(() => props.clubFilter, (newFilter) => {
  console.log("SideView: Club filter changed to:", newFilter || 'All clubs');
  renderChart();
});

// Initialize chart when component is mounted
onMounted(() => {
  console.log("SideView mounted");
  
  // Make sure we give the DOM enough time to initialize
  const timer = setTimeout(() => {
    console.log("SideView: Initial render after timeout");
    renderChart();
  }, 1000);
  
  // Cleanup function
  return () => {
    clearTimeout(timer);
    if (chart) {
      console.log("SideView: Destroying chart instance");
      chart.destroy();
      chart = null;
    }
  };
});
</script>

<style scoped>
.chart-container {
  position: relative;
  height: 300px;
  width: 100%;
  background-color: #f6f9fc;
  border-radius: 8px;
  border: 1px solid #e0e0e0;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
  overflow: hidden;
}

#side-view-status {
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
