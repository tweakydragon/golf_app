<template>
  <BaseChart
    v-if="hasData"
    type="doughnut"
    :data="chartData"
    :options="chartOptions"
    :height="320"
  />
  <div v-else class="chart-empty">No club usage data available.</div>
</template>

<script setup>
import { computed } from 'vue';
import BaseChart from './BaseChart.vue';
import { getChartPalette, resolveToken } from '../../theme/palette';

const props = defineProps({
  clubCounts: {
    type: Object,
    default: () => ({})
  }
});

const clubs = computed(() => Object.keys(props.clubCounts || {}));
const counts = computed(() => clubs.value.map((club) => props.clubCounts[club] || 0));

const hasData = computed(() => counts.value.some((value) => value > 0));

const chartData = computed(() => {
  if (!hasData.value) {
    return { labels: [], datasets: [] };
  }

  const colors = getChartPalette(clubs.value.length);

  return {
    labels: clubs.value,
    datasets: [
      {
        data: counts.value,
        backgroundColor: colors,
        borderColor: resolveToken('--color-surface', '#ffffff'),
        borderWidth: 2,
        hoverOffset: 8
      }
    ]
  };
});

const chartOptions = computed(() => ({
  responsive: true,
  maintainAspectRatio: false,
  cutout: '55%',
  plugins: {
    legend: {
      position: 'bottom',
      labels: {
        padding: 20,
        usePointStyle: true
      }
    },
    tooltip: {
      callbacks: {
        label: (context) => {
          const label = context.label;
          const value = context.parsed;
          const total = context.dataset.data.reduce((sum, current) => sum + current, 0);
          const percentage = total > 0 ? ((value / total) * 100).toFixed(1) : '0.0';
          return `${label}: ${value} shots (${percentage}%)`;
        }
      }
    }
  },
  animation: {
    animateRotate: true,
    animateScale: true,
    duration: 600,
    easing: 'easeOutQuint'
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
