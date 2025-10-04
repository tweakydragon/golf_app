<template>
  <div class="chart-shell" :style="wrapperStyle">
    <canvas ref="canvasEl"></canvas>
  </div>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, ref, watch, nextTick } from 'vue';
import { Chart, registerables } from 'chart.js';

Chart.register(...registerables);

const props = defineProps({
  type: {
    type: String,
    required: true
  },
  data: {
    type: Object,
    required: true
  },
  options: {
    type: Object,
    default: () => ({})
  },
  height: {
    type: Number,
    default: 300
  },
  width: {
    type: Number,
    default: 0
  }
});

const canvasEl = ref(null);
let chartInstance = null;

const wrapperStyle = computed(() => {
  const style = {
    height: `${props.height}px`
  };
  if (props.width > 0) {
    style.width = `${props.width}px`;
  }
  return style;
});

const clone = (value) => {
  if (value === null || typeof value !== 'object') {
    return value;
  }
  if (Array.isArray(value)) {
    return value.map(clone);
  }
  const cloned = {};
  for (const key of Object.keys(value)) {
    cloned[key] = clone(value[key]);
  }
  return cloned;
};

const destroyChart = () => {
  if (chartInstance) {
    chartInstance.destroy();
    chartInstance = null;
  }
};

const renderChart = () => {
  if (!canvasEl.value) {
    return;
  }
  destroyChart();
  const context = canvasEl.value.getContext('2d');
  const config = {
    type: props.type,
    data: clone(props.data),
    options: clone(props.options)
  };
  chartInstance = new Chart(context, config);
};

watch(
  () => ({ type: props.type, data: props.data, options: props.options }),
  () => {
    nextTick(() => {
      renderChart();
    });
  },
  { deep: true }
);

onMounted(() => {
  nextTick(() => {
    renderChart();
  });
});

onBeforeUnmount(() => {
  destroyChart();
});
</script>

<style scoped>
.chart-shell {
  position: relative;
  width: 100%;
}

canvas {
  width: 100%;
  height: 100%;
}
</style>
