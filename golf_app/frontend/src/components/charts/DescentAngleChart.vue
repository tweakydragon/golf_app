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
  
  // Filter shots with descent angle data (Awesome Golf only)
  const validShots = props.shots.filter(shot => 
    shot.descentAngle && shot.descentAngle > 0
  );
  
  if (validShots.length === 0) return;
  
  // Group by club and calculate average descent angle
  const clubDescentData = {};
  validShots.forEach(shot => {
    if (!clubDescentData[shot.club]) {
      clubDescentData[shot.club] = [];
    }
    clubDescentData[shot.club].push(shot.descentAngle);
  });
  
  const clubs = Object.keys(clubDescentData);
  const avgDescentAngles = clubs.map(club => {
    const descentValues = clubDescentData[club];
    return descentValues.reduce((a, b) => a + b, 0) / descentValues.length;
  });
  
  const ctx = chartCanvas.value.getContext('2d');
  
  chartInstance = new ChartJS(ctx, {
    type: 'bar',
    data: {
      labels: clubs,
      datasets: [{
        label: 'Average Descent Angle',
        data: avgDescentAngles,
        backgroundColor: 'rgba(201, 203, 207, 0.7)',
        borderColor: 'rgba(201, 203, 207, 1)',
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
              return `Avg. Descent Angle: ${context.parsed.y.toFixed(1)}°`;
            }
          }
        }
      },
      scales: {
        y: {
          beginAtZero: true,
          title: {
            display: true,
            text: 'Descent Angle (degrees)'
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
