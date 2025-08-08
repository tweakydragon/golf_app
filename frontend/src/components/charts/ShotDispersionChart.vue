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
  
  // Filter shots with lateral deviation data
  const validShots = props.shots.filter(shot => 
    shot.lateralDeviation !== null && shot.lateralDeviation !== undefined &&
    shot.totalDistance && shot.totalDistance > 0
  );
  
  if (validShots.length === 0) {
    // If no lateral deviation data, create a simple scatter based on shot sequence
    const fallbackShots = props.shots.filter(shot => shot.totalDistance && shot.totalDistance > 0);
    if (fallbackShots.length === 0) return;
    
    const ctx = chartCanvas.value.getContext('2d');
    
    chartInstance = new ChartJS(ctx, {
      type: 'scatter',
      data: {
        datasets: [{
          label: 'Shot Pattern',
          data: fallbackShots.map((shot, index) => ({
            x: (index % 5) - 2, // Spread shots across x-axis
            y: shot.totalDistance,
            club: shot.club,
            shotNumber: shot.shotNumber
          })),
          backgroundColor: 'rgba(255, 99, 132, 0.6)',
          borderColor: 'rgba(255, 99, 132, 1)',
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
                return `Distance: ${context.parsed.y.toFixed(1)} yards`;
              }
            }
          }
        },
        scales: {
          x: {
            title: {
              display: true,
              text: 'Shot Pattern'
            }
          },
          y: {
            title: {
              display: true,
              text: 'Distance (yards)'
            },
            beginAtZero: true
          }
        }
      }
    });
    return;
  }
  
  const ctx = chartCanvas.value.getContext('2d');
  
  chartInstance = new ChartJS(ctx, {
    type: 'scatter',
    data: {
      datasets: [{
        label: 'Shot Dispersion',
        data: validShots.map(shot => ({
          x: shot.lateralDeviation || 0,
          y: shot.totalDistance,
          club: shot.club,
          shotNumber: shot.shotNumber
        })),
        backgroundColor: 'rgba(75, 192, 192, 0.6)',
        borderColor: 'rgba(75, 192, 192, 1)',
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
              const deviation = context.parsed.x;
              const direction = deviation > 0 ? 'Right' : deviation < 0 ? 'Left' : 'Straight';
              return [
                `Distance: ${context.parsed.y.toFixed(1)} yards`,
                `Lateral: ${Math.abs(deviation).toFixed(1)} yards ${direction}`
              ];
            }
          }
        }
      },
      scales: {
        x: {
          title: {
            display: true,
            text: 'Lateral Deviation (yards) - Left(-) / Right(+)'
          }
        },
        y: {
          title: {
            display: true,
            text: 'Distance (yards)'
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
  height: 400px;
  width: 100%;
}
</style>