<template>
  <div class="club-stats-chart">
    <h3 class="text-center mb-3">Club Performance Comparison</h3>
    <div id="club-stats-status" class="mb-2 text-center"></div>
    <div class="chart-container">
      <canvas id="club-stats-canvas"></canvas>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue';
import { getColorForClub, formatNumber } from '../utils/chartUtils.js';

const props = defineProps({
  stats: {
    type: Object,
    required: true
  },
  metric: {
    type: String,
    default: 'avgCarry'
  }
});

// Chart instance
let chart = null;

// Available metrics for display
const metrics = {
  avgCarry: {
    label: 'Average Carry Distance (yards)',
    property: 'avgCarry',
    formatter: (val) => formatNumber(val, 1)
  },
  avgTotal: {
    label: 'Average Total Distance (yards)',
    property: 'avgTotal',
    formatter: (val) => formatNumber(val, 1)
  },
  avgBallSpeed: {
    label: 'Average Ball Speed (mph)',
    property: 'avgBallSpeed',
    formatter: (val) => formatNumber(val, 1)
  }
};

// Function to render the chart
const renderChart = () => {
  const status = document.getElementById('club-stats-status');
  if (status) status.textContent = "Rendering club stats...";
  
  if (!props.stats || !props.stats.clubStats) {
    if (status) status.textContent = "No club stats available";
    console.warn("No club stats available to visualize");
    return;
  }
  
  console.log("ClubStatsChart: Rendering chart with metric", props.metric);
  
  // Import Chart.js
  import('chart.js/auto').then(module => {
    try {
      const Chart = module.default;
      
      // Get canvas element
      const canvas = document.getElementById('club-stats-canvas');
      if (!canvas) {
        console.error("Canvas element not found for club stats");
        if (status) status.textContent = "Canvas not found";
        return;
      }
      
      const ctx = canvas.getContext('2d');
      if (!ctx) {
        console.error("Could not get 2D context for club stats");
        if (status) status.textContent = "Could not get 2D context";
        return;
      }
      
      // Get metric info
      const currentMetric = metrics[props.metric] || metrics.avgCarry;
      
      // Prepare data from club stats
      const clubData = [];
      const labels = [];
      const colors = [];
      const borderColors = [];
      
      // Sort clubs in a logical order (Woods first, then irons in order, then wedges)
      const sortedClubs = Object.keys(props.stats.clubStats).sort((a, b) => {
        // Define a ranking system for clubs
        const clubRank = (club) => {
          if (club.toLowerCase().includes('driver')) return 0;
          if (club.toLowerCase().includes('wood')) {
            // Extract number from wood (e.g., "3 Wood" -> 3)
            const match = club.match(/(\d+)/);
            return match ? 1 + parseInt(match[1])/10 : 1;
          }
          if (club.toLowerCase().includes('iron')) {
            // Extract number from iron (e.g., "7 Iron" -> 7)
            const match = club.match(/(\d+)/);
            return 10 + parseInt(match[1]);
          }
          if (club === 'PW') return 20;
          if (club === 'GW' || club === '52°') return 21;
          if (club === 'SW' || club === '56°') return 22;
          if (club === 'LW' || club === '60°') return 23;
          return 30; // Other clubs
        };
        
        return clubRank(a) - clubRank(b);
      });
      
      // Process the data
      sortedClubs.forEach(club => {
        const clubMetric = props.stats.clubStats[club][currentMetric.property];
        if (clubMetric !== undefined) {
          labels.push(club);
          clubData.push(clubMetric);
          
          const colorObj = getColorForClub(club);
          colors.push(colorObj.color);
          borderColors.push(colorObj.borderColor);
        }
      });
      
      // Clean up previous chart instance if it exists
      if (chart) {
        chart.destroy();
        chart = null;
      }
      
      // Create chart
      chart = new Chart(ctx, {
        type: 'bar',
        data: {
          labels: labels,
          datasets: [{
            label: currentMetric.label,
            data: clubData,
            backgroundColor: colors,
            borderColor: borderColors,
            borderWidth: 1
          }]
        },
        options: {
          indexAxis: 'y', // Horizontal bars
          responsive: true,
          maintainAspectRatio: false,
          plugins: {
            tooltip: {
              callbacks: {
                label: (context) => {
                  const value = context.raw;
                  return `${currentMetric.label}: ${currentMetric.formatter(value)}`;
                }
              }
            },
            legend: {
              display: false
            }
          },
          scales: {
            x: {
              beginAtZero: true,
              title: {
                display: true,
                text: currentMetric.label
              }
            }
          }
        }
      });
      
      console.log("Club stats chart created successfully");
      if (status) status.textContent = "";
    } catch (error) {
      console.error("Error in club stats chart rendering:", error);
      if (status) status.textContent = "Error: " + error.message;
    }
  }).catch(error => {
    console.error("Error loading Chart.js for club stats:", error);
    if (status) status.textContent = "Error loading Chart.js: " + error.message;
  });
};

// Watch for changes in stats or metric
watch(() => props.stats, () => {
  console.log("ClubStatsChart: Stats data changed");
  renderChart();
}, { deep: true });

watch(() => props.metric, (newMetric) => {
  console.log(`ClubStatsChart: Metric changed to ${newMetric}`);
  renderChart();
});

// Initialize chart when component is mounted
onMounted(() => {
  console.log("ClubStatsChart mounted");
  
  // Make sure we give the DOM enough time to initialize
  const timer = setTimeout(() => {
    console.log("ClubStatsChart: Initial render after timeout");
    renderChart();
  }, 500);
  
  // Cleanup function
  return () => {
    clearTimeout(timer);
    if (chart) {
      console.log("ClubStatsChart: Destroying chart instance");
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

#club-stats-status {
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
