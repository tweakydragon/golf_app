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
  clubStats: {
    type: Object,
    default: () => ({})
  }
});

const chartCanvas = ref(null);
let chartInstance = null;

const createChart = () => {
  if (!chartCanvas.value || !props.clubStats) return;
  
  // Destroy existing chart if it exists
  if (chartInstance) {
    chartInstance.destroy();
  }
  
  const clubs = Object.keys(props.clubStats);
  
  if (clubs.length === 0) {
    return;
  }
  
  const carryDistances = clubs.map(club => props.clubStats[club]?.avgCarry || 0);
  const totalDistances = clubs.map(club => props.clubStats[club]?.avgTotal || 0);
  
  const ctx = chartCanvas.value.getContext('2d');
  
  chartInstance = new ChartJS(ctx, {
    type: 'bar',
    data: {
      labels: clubs,
      datasets: [
        {
          label: 'Avg. Carry Distance',
          data: carryDistances,
          backgroundColor: 'rgba(54, 162, 235, 0.7)',
          borderColor: 'rgba(54, 162, 235, 1)',
          borderWidth: 1
        },
        {
          label: 'Avg. Total Distance',
          data: totalDistances,
          backgroundColor: 'rgba(255, 99, 132, 0.7)',
          borderColor: 'rgba(255, 99, 132, 1)',
          borderWidth: 1
        }
      ]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: {
          position: 'top'
        },
        tooltip: {
          callbacks: {
            label: (context) => {
              return `${context.dataset.label}: ${context.parsed.y.toFixed(1)} yards`;
            }
          }
        }
      },
      scales: {
        y: {
          beginAtZero: true,
          title: {
            display: true,
            text: 'Distance (yards)'
          }
        },
        x: {
          title: {
            display: true,
            text: 'Club'
          }
        }
      }
    }
  });
};

watch(() => props.clubStats, () => {
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
  height: 300px;
  width: 100%;
}
</style>