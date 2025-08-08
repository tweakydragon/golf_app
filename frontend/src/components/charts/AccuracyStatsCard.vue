<template>
  <div class="accuracy-stats">
    <div class="stat-item mb-3">
      <h6 class="text-muted mb-1">Average Distance</h6>
      <h4 class="text-primary">{{ averageDistance.toFixed(1) }} yards</h4>
    </div>
    
    <div class="stat-item mb-3">
      <h6 class="text-muted mb-1">Distance Standard Deviation</h6>
      <h4 class="text-info">{{ distanceStdDev.toFixed(1) }} yards</h4>
    </div>
    
    <div class="stat-item mb-3">
      <h6 class="text-muted mb-1">Consistency Score</h6>
      <h4 class="text-success">{{ consistencyScore.toFixed(1) }}%</h4>
    </div>
    
    <div class="stat-item mb-3" v-if="lateralStats.hasData">
      <h6 class="text-muted mb-1">Lateral Accuracy</h6>
      <h4 class="text-warning">{{ Math.abs(lateralStats.average).toFixed(1) }} yards {{ lateralStats.tendency }}</h4>
    </div>
    
    <div class="stat-item">
      <h6 class="text-muted mb-1">Best Shot</h6>
      <h4 class="text-success">{{ bestShot.distance.toFixed(1) }} yards</h4>
      <small class="text-muted">Shot #{{ bestShot.shotNumber }} ({{ bestShot.club }})</small>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue';

const props = defineProps({
  shots: {
    type: Array,
    default: () => []
  }
});

const validShots = computed(() => {
  return props.shots.filter(shot => shot.totalDistance && shot.totalDistance > 0);
});

const averageDistance = computed(() => {
  if (validShots.value.length === 0) return 0;
  
  const total = validShots.value.reduce((sum, shot) => sum + shot.totalDistance, 0);
  return total / validShots.value.length;
});

const distanceStdDev = computed(() => {
  if (validShots.value.length <= 1) return 0;
  
  const avg = averageDistance.value;
  const variance = validShots.value.reduce((sum, shot) => {
    return sum + Math.pow(shot.totalDistance - avg, 2);
  }, 0) / (validShots.value.length - 1);
  
  return Math.sqrt(variance);
});

const consistencyScore = computed(() => {
  if (averageDistance.value === 0) return 0;
  
  // Consistency score based on coefficient of variation (lower is better)
  const coefficientOfVariation = distanceStdDev.value / averageDistance.value;
  const score = Math.max(0, Math.min(100, 100 - (coefficientOfVariation * 100)));
  return Math.round(score);
});

const lateralStats = computed(() => {
  const lateralShots = validShots.value.filter(shot => 
    shot.lateralDeviation !== null && shot.lateralDeviation !== undefined
  );
  
  if (lateralShots.length === 0) {
    return { hasData: false, average: 0, tendency: 'No Data' };
  }
  
  const average = lateralShots.reduce((sum, shot) => sum + shot.lateralDeviation, 0) / lateralShots.length;
  const tendency = average > 0 ? 'Right' : average < 0 ? 'Left' : 'Straight';
  
  return {
    hasData: true,
    average,
    tendency
  };
});

const bestShot = computed(() => {
  if (validShots.value.length === 0) {
    return { distance: 0, shotNumber: 0, club: 'N/A' };
  }
  
  const best = validShots.value.reduce((best, shot) => {
    return shot.totalDistance > best.totalDistance ? shot : best;
  }, validShots.value[0]);
  
  return {
    distance: best.totalDistance,
    shotNumber: best.shotNumber,
    club: best.club
  };
});
</script>

<style scoped>
.accuracy-stats {
  padding: 0;
}

.stat-item {
  padding: 15px 0;
  border-bottom: 1px solid #e9ecef;
}

.stat-item:last-child {
  border-bottom: none;
}

h6 {
  font-size: 0.875rem;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

h4 {
  font-size: 1.5rem;
  font-weight: 700;
  margin: 0;
}

small {
  display: block;
  margin-top: 5px;
}
</style>
