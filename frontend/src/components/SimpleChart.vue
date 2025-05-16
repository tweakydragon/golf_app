<template>
  <div class="simple-chart">
    <h3 class="text-center mb-3">Simple Test Chart</h3>
    <div id="chart-status" class="mb-2 text-center"></div>"mb-2 text-center"></div>
    <div class="chart-container">
      <canvas id="simple-chart-canvas"></canvas>vas id="simple-chart-canvas"></canvas>
    </div>v>
  </div>
</template></template>

<script setup>
import { onMounted } from 'vue';

// Use standard JavaScript approachrd JavaScript approach
let chart = null;

// Function to render a simple chart// Function to render a simple chart
const renderChart = () => {
  document.getElementById('chart-status').innerHTML = "Attempting to render chart...";mple test chart");
  
  try {if (!chartCanvas.value) {
    // Import Chart.js dynamically  ready");
    import('chart.js/auto').then(module => {
      const Chart = module.default;
      
      // Get canvas elementconst ctx = chartCanvas.value.getContext('2d');
      const canvas = document.getElementById('simple-chart-canvas');
      if (!canvas) {// Simple static data for testing
        document.getElementById('chart-status').innerHTML = "Canvas element not found";
        return;d', 'Blue', 'Yellow', 'Green', 'Purple', 'Orange'],
      }
      st Data',
      const ctx = canvas.getContext('2d');, 2, 3],
      if (!ctx) {
        document.getElementById('chart-status').innerHTML = "Could not get 2D context";32, 0.5)',
        return;
      }
      
      // Simple static data for testing,
      const data = {
        labels: ['Red', 'Blue', 'Yellow', 'Green', 'Purple', 'Orange'],
        datasets: [{rderColor: [
          label: 'Test Data',9, 132, 1)',
          data: [12, 19, 3, 5, 2, 3],
          backgroundColor: [
            'rgba(255, 99, 132, 0.5)',




















































</style>}  overflow: hidden;  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);  border-radius: 8px;  background-color: #f6f9fc;  width: 100%;  height: 300px;  position: relative;.chart-container {<style scoped></script>});  setTimeout(renderChart, 300);  // Wait a bit for the DOM to be ready  console.log("SimpleChart component mounted");onMounted(() => {// Initialize chart when component is mounted};  }    document.getElementById('chart-status').innerHTML = "Error: " + error.message;  } catch (error) {    });      document.getElementById('chart-status').innerHTML = "Error loading Chart.js: " + error.message;    }).catch(error => {      document.getElementById('chart-status').innerHTML = "Chart rendered successfully!";            });        }          }            }              beginAtZero: true            y: {          scales: {          maintainAspectRatio: false,          responsive: true,        options: {        data: data,        type: 'bar',      chart = new Chart(ctx, {      // Create chart            };        }]          ]            'rgba(255, 159, 64, 0.5)'            'rgba(153, 102, 255, 0.5)',            'rgba(75, 192, 192, 0.5)',            'rgba(255, 206, 86, 0.5)',            'rgba(54, 162, 235, 0.5)',        'rgba(153, 102, 255, 1)',
        'rgba(255, 159, 64, 1)'
      ],
      borderWidth: 1
    }]
  };
  
  // Create chart
  chart = new Chart(ctx, {
    type: 'bar',
    data: data,
    options: {
      responsive: true,
      maintainAspectRatio: false,
      scales: {
        y: {
          beginAtZero: true
        }
      }
    }
  });
};

// Initialize chart when component is mounted
onMounted(() => {
  console.log("SimpleChart component mounted");
  renderChart();
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
