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
  PointElement,
  Title,
  Tooltip,
  Legend
} from 'chart.js';

ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
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
  
  // Filter shots with both ball speed and club speed data
  const validShots = props.shots.filter(shot => 
    shot.ballSpeed && shot.clubHeadSpeed &&
    shot.ballSpeed > 0 && shot.clubHeadSpeed > 0
  );
  
  if (validShots.length === 0) return;
  
  const ctx = chartCanvas.value.getContext('2d');
  
  chartInstance = new ChartJS(ctx, {
    type: 'scatter',
    data: {
      datasets: [{
        label: 'Shots',
        data: validShots.map(shot => ({
          x: shot.clubHeadSpeed,
          y: shot.ballSpeed,
          club: shot.club,
          shotNumber: shot.shotNumber,
          smashFactor: (shot.ballSpeed / shot.clubHeadSpeed).toFixed(1)
        })),
        backgroundColor: 'rgba(153, 102, 255, 0.6)',
        borderColor: 'rgba(153, 102, 255, 1)',
        borderWidth: 1,
        pointRadius: 6,
        pointHoverRadius: 8
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
              const point = context[0];
              return `Shot #${point.raw.shotNumber} (${point.raw.club})`;
            },
            label: (context) => {
              return [
                `Club Speed: ${context.parsed.x} mph`,
                `Ball Speed: ${context.parsed.y} mph`,
                `Smash Factor: ${context.raw.smashFactor}`
              ];
            }
          }
        }
      },
      scales: {
        x: {
          title: {
            display: true,
            text: 'Club Head Speed (mph)'
          },
          beginAtZero: true
        },
        y: {
          title: {
            display: true,
            text: 'Ball Speed (mph)'
          },
          beginAtZero: true
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
