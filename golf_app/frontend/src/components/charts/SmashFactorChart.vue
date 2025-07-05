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

const createChart = () => {
  if (!chartCanvas.value || !props.shots || props.shots.length === 0) return;
  
  // Destroy existing chart if it exists
  if (chartInstance) {
    chartInstance.destroy();
  }
  
  // Filter shots with smash factor data (Awesome Golf only)
  const validShots = props.shots.filter(shot => 
    shot.smash && shot.smash > 0
  );
  
  if (validShots.length === 0) return;
  
  // Group by club and calculate average smash factor
  const clubSmashData = {};
  validShots.forEach(shot => {
    if (!clubSmashData[shot.club]) {
      clubSmashData[shot.club] = [];
    }
    clubSmashData[shot.club].push(shot.smash);
  });
  
  const clubs = Object.keys(clubSmashData);
  const avgSmashFactors = clubs.map(club => {
    const smashValues = clubSmashData[club];
    return smashValues.reduce((a, b) => a + b, 0) / smashValues.length;
  });
  
  const ctx = chartCanvas.value.getContext('2d');
  
  chartInstance = new ChartJS(ctx, {
    type: 'bar',
    data: {
      labels: clubs,
      datasets: [{
        label: 'Average Smash Factor',
        data: avgSmashFactors,
        backgroundColor: 'rgba(255, 206, 86, 0.7)',
        borderColor: 'rgba(255, 206, 86, 1)',
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
            label: (context) => {
              return `Avg. Smash Factor: ${context.parsed.y.toFixed(1)}`;
            }
          }
        }
      },
      scales: {
        y: {
          beginAtZero: true,
          max: 2.0,
          title: {
            display: true,
            text: 'Smash Factor'
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
  height: 300px;
  width: 100%;
}
</style>
