<template>
  <div class="chart-container">
    <canvas ref="chartCanvas"></canvas>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, nextTick } from 'vue';
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend
} from 'chart.js';

ChartJS.register(
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend
);

const props = defineProps({
  shots: {
    type: Array,
    default: () => []
  }
});

const chartCanvas = ref(null);
let chartInstance = null;

const createDistributionBins = (distances) => {
  if (distances.length === 0) return { bins: [], counts: [] };
  
  const min = Math.min(...distances);
  const max = Math.max(...distances);
  const range = max - min;
  const binSize = Math.max(10, Math.ceil(range / 10)); // At least 10 yard bins, max 10 bins
  
  const bins = [];
  const counts = [];
  
  for (let i = Math.floor(min / binSize) * binSize; i <= max; i += binSize) {
    const binStart = i;
    const binEnd = i + binSize;
    const binLabel = `${binStart}-${binEnd - 1}`;
    
    const count = distances.filter(d => d >= binStart && d < binEnd).length;
    
    bins.push(binLabel);
    counts.push(count);
  }
  
  return { bins, counts };
};

const createChart = () => {
  if (!chartCanvas.value || !props.shots || props.shots.length === 0) return;
  
  // Destroy existing chart if it exists
  if (chartInstance) {
    chartInstance.destroy();
  }
  
  // Get total distances
  const totalDistances = props.shots
    .map(shot => shot.totalDistance)
    .filter(distance => distance && distance > 0);
  
  if (totalDistances.length === 0) return;
  
  const { bins, counts } = createDistributionBins(totalDistances);
  
  const ctx = chartCanvas.value.getContext('2d');
  
  chartInstance = new ChartJS(ctx, {
    type: 'bar',
    data: {
      labels: bins,
      datasets: [{
        label: 'Number of Shots',
        data: counts,
        backgroundColor: 'rgba(75, 192, 192, 0.7)',
        borderColor: 'rgba(75, 192, 192, 1)',
        borderWidth: 1
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: {
          display: false
        },
        tooltip: {
          callbacks: {
            title: (context) => {
              return `${context[0].label} yards`;
            },
            label: (context) => {
              const percentage = ((context.parsed.y / totalDistances.length) * 100).toFixed(1);
              return `${context.parsed.y} shots (${percentage}%)`;
            }
          }
        }
      },
      scales: {
        y: {
          beginAtZero: true,
          title: {
            display: true,
            text: 'Number of Shots'
          },
          ticks: {
            stepSize: 1
          }
        },
        x: {
          title: {
            display: true,
            text: 'Distance Range (yards)'
          }
        }
      }
    }
  });
};

watch(() => props.shots, () => {
  nextTick(() => {
    createChart();
  });
}, { deep: true });

onMounted(() => {
  nextTick(() => {
    createChart();
  });
});
</script>

<style scoped>
.chart-container {
  position: relative;
  height: 350px;
  width: 100%;
}
</style>