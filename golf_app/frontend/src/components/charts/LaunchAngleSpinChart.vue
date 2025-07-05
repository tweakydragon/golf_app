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
  
  // Filter shots with both launch angle and spin rate data
  const validShots = props.shots.filter(shot => 
    shot.launchAngle && shot.spinRate &&
    shot.launchAngle > 0 && shot.spinRate > 0
  );
  
  if (validShots.length === 0) return;
  
  const ctx = chartCanvas.value.getContext('2d');
  
  chartInstance = new ChartJS(ctx, {
    type: 'scatter',
    data: {
      datasets: [{
        label: 'Shots',
        data: validShots.map(shot => ({
          x: shot.launchAngle,
          y: shot.spinRate,
          club: shot.club,
          shotNumber: shot.shotNumber,
          distance: shot.totalDistance
        })),
        backgroundColor: 'rgba(255, 159, 64, 0.6)',
        borderColor: 'rgba(255, 159, 64, 1)',
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
                `Launch Angle: ${context.parsed.x.toFixed(1)}°`,
                `Spin Rate: ${context.parsed.y.toLocaleString()} rpm`,
                `Distance: ${context.raw.distance.toFixed(1)} yards`
              ];
            }
          }
        }
      },
      scales: {
        x: {
          title: {
            display: true,
            text: 'Launch Angle (degrees)'
          },
          beginAtZero: true
        },
        y: {
          title: {
            display: true,
            text: 'Spin Rate (rpm)'
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
