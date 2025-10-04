<template>
  <BaseChart
    v-if="hasData"
    type="scatter"
    :data="chartData"
    :options="chartOptions"
    :height="350"
  />
  <div v-else class="chart-empty">No speed metrics available.</div>
</template>

<script setup>
import { computed } from 'vue';
import BaseChart from './BaseChart.vue';
import { applyAlpha, resolveToken } from '../../theme/palette';

const props = defineProps({
  shots: {
    type: Array,
    default: () => []
  }
});

const validShots = computed(() => {
  return (props.shots || []).filter((shot) => {
    return shot && shot.ballSpeed && shot.clubHeadSpeed && shot.ballSpeed > 0 && shot.clubHeadSpeed > 0;
  });
});

const hasData = computed(() => validShots.value.length > 0);

const chartData = computed(() => {
  if (!hasData.value) {
    return { datasets: [] };
  }

  const emphasis = resolveToken('--color-info', '#0dcaf0');

  return {
    datasets: [
      {
        label: 'Shots',
        data: validShots.value.map((shot) => ({
          x: shot.clubHeadSpeed,
          y: shot.ballSpeed,
          club: shot.club,
          shotNumber: shot.shotNumber,
          smashFactor: shot.clubHeadSpeed ? (shot.ballSpeed / shot.clubHeadSpeed).toFixed(2) : '0.00'
        })),
        backgroundColor: applyAlpha(emphasis, 0.65),
        borderColor: emphasis,
        borderWidth: 1,
        pointRadius: 6,
        pointHoverRadius: 8,
        pointHoverBorderWidth: 2
      }
    ]
  };
});

const chartOptions = computed(() => ({
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: {
      display: false
    },
    tooltip: {
      callbacks: {
        title: (items) => {
          const point = items[0];
          return `Shot #${point.raw.shotNumber || '?'} (${point.raw.club || 'Unknown'})`;
        },
        label: (context) => {
          return [
            `Club Speed: ${context.parsed.x.toFixed(1)} mph`,
            `Ball Speed: ${context.parsed.y.toFixed(1)} mph`,
            `Smash Factor: ${context.raw.smashFactor}`
          ];
        }
      }
    }
  },
  scales: {
    x: {
      beginAtZero: true,
      title: {
        display: true,
        text: 'Club Head Speed (mph)'
      },
      grid: {
        color: resolveToken('--color-border', 'rgba(0,0,0,0.08)')
      }
    },
    y: {
      beginAtZero: true,
      title: {
        display: true,
        text: 'Ball Speed (mph)'
      },
      grid: {
        color: resolveToken('--color-border', 'rgba(0,0,0,0.08)')
      }
    }
  },
  animation: {
    duration: 500,
    easing: 'easeOutQuad'
  }
}));
</script>

<style scoped>
.chart-empty {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 350px;
  border: 1px dashed var(--color-border);
  border-radius: 12px;
  color: var(--color-muted);
  font-size: 0.95rem;
}
</style>
