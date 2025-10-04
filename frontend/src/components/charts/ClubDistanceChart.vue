<template>
  <BaseChart
    v-if="hasData"
    type="bar"
    :data="chartData"
    :options="chartOptions"
    :height="320"
  />
  <div v-else class="chart-empty">No club distance data available.</div>
</template>

<script setup>
import { computed } from 'vue';
import BaseChart from './BaseChart.vue';
import { applyAlpha, resolveToken } from '../../theme/palette';

const props = defineProps({
  clubStats: {
    type: Object,
    default: () => ({})
  }
});

const clubs = computed(() => Object.keys(props.clubStats || {}));

const hasData = computed(() => clubs.value.length > 0);

const chartData = computed(() => {
  if (!hasData.value) {
    return { labels: [], datasets: [] };
  }

  const carry = [];
  const total = [];

  clubs.value.forEach((club) => {
    const stats = props.clubStats[club] || {};
    carry.push(stats.avgCarry || 0);
    total.push(stats.avgTotal || 0);
  });

  const primary = resolveToken('--color-primary', '#0d6efd');
  const secondary = resolveToken('--color-warning', '#ffc107');

  return {
    labels: clubs.value,
    datasets: [
      {
        label: 'Avg. Carry Distance',
        data: carry,
        backgroundColor: applyAlpha(primary, 0.7),
        borderColor: primary,
        borderWidth: 1,
        borderRadius: 6,
        maxBarThickness: 32
      },
      {
        label: 'Avg. Total Distance',
        data: total,
        backgroundColor: applyAlpha(secondary, 0.7),
        borderColor: secondary,
        borderWidth: 1,
        borderRadius: 6,
        maxBarThickness: 32
      }
    ]
  };
});

const chartOptions = computed(() => ({
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: {
      position: 'top',
      labels: {
        boxWidth: 12,
        boxHeight: 12,
        usePointStyle: true
      }
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
      border: {
        display: false
      },
      title: {
        display: true,
        text: 'Distance (yards)'
      },
      grid: {
        color: resolveToken('--color-border', 'rgba(0,0,0,0.08)'),
        drawBorder: false
      }
    },
    x: {
      border: {
        display: false
      },
      title: {
        display: true,
        text: 'Club'
      },
      grid: {
        display: false
      }
    }
  },
  animation: {
    duration: 500,
    easing: 'easeOutQuart'
  }
}));
</script>

<style scoped>
.chart-empty {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 320px;
  border: 1px dashed var(--color-border);
  border-radius: 12px;
  color: var(--color-muted);
  font-size: 0.95rem;
}
</style>
