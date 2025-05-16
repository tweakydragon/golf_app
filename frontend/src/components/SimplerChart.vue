<template>
  <div class="simpler-chart">
    <h3 class="text-center mb-3">Simpler Test Chart</h3>
    <div id="chart-status" class="mb-2 text-center"></div>
    <div class="chart-container">
      <canvas id="simpler-chart-canvas"></canvas>
    </div>
  </div>
</template>

<script setup>
import { onMounted } from 'vue';

// Use standard JavaScript approach
let chart = null;

// Function to render a simple chart
const renderChart = () => {
  const statusEl = document.getElementById('chart-status');
  if (statusEl) statusEl.textContent = "Attempting to render chart...";
  
  // Import Chart.js directly instead of dynamically
  import('chart.js/auto')
    .then(module => {
      const Chart = module.default;
      
      // Get canvas element
      const canvas = document.getElementById('simpler-chart-canvas');
      if (!canvas) {
        if (statusEl) statusEl.textContent = "Canvas element not found";
        console.error("Canvas element not found");
        return;
      }
      
      const ctx = canvas.getContext('2d');
      if (!ctx) {
        if (statusEl) statusEl.textContent = "Could not get 2D context";
        console.error("Could not get 2D context");
        return;
      }
      
      console.log("Canvas and context are ready, rendering chart...");
      
      // Simple static data for testing
      const data = {
        labels: ['Driver', '3 Wood', '7 Iron', '8 Iron', 'PW', 'SW'],
        datasets: [{
          label: 'Avg Distance (yards)',
          data: [265, 240, 165, 150, 125, 95],
          backgroundColor: [
            'rgba(255, 99, 132, 0.7)',
            'rgba(54, 162, 235, 0.7)',
            'rgba(255, 206, 86, 0.7)',
            'rgba(75, 192, 192, 0.7)',
            'rgba(153, 102, 255, 0.7)',
            'rgba(255, 159, 64, 0.7)'
          ],
          borderColor: [
            'rgb(255, 99, 132)',
            'rgb(54, 162, 235)',
            'rgb(255, 206, 86)',
            'rgb(75, 192, 192)',
            'rgb(153, 102, 255)',
            'rgb(255, 159, 64)'
          ],
          borderWidth: 1
        }]
      };
      
      // Clean up previous instance if it exists
      if (chart) chart.destroy();
      
      // Create chart
      chart = new Chart(ctx, {
        type: 'bar',
        data: data,
        options: {
          responsive: true,
          maintainAspectRatio: false,
          animation: {
            duration: 0 // General animation time
          },
          scales: {
            y: {
              beginAtZero: true
            }
          }
        }
      });
      
      if (statusEl) statusEl.textContent = "Chart rendered successfully!";
      console.log("Chart rendered successfully!");
    })
    .catch(error => {
      console.error("Error loading Chart.js:", error);
      if (statusEl) statusEl.textContent = "Error loading Chart.js: " + error.message;
    });
};

// Initialize chart when component is mounted
onMounted(() => {
  console.log("SimplerChart component mounted");
  // Make sure we give the DOM enough time to initialize
  setTimeout(() => {
    console.log("Attempting to render chart now");
    renderChart();
  }, 500);
  
  // Clean up on component unmount
  return () => {
    if (chart) {
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
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
  overflow: hidden;
}
</style>
