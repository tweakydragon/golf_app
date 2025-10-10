<template>
  <div class="session-view-container">
    <!-- Loading State -->
    <div v-if="loading" class="text-center p-5">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Loading...</span>
      </div>
      <p class="mt-3">Loading session data...</p>
    </div>
    
    <!-- Error State -->
    <div v-else-if="error" class="alert alert-danger">
      {{ error }}
      <div class="mt-3">
        <router-link to="/" class="btn btn-outline-primary">
          Back to Sessions
        </router-link>
      </div>
    </div>
    
    <!-- Session Data View -->
    <div v-else-if="session" class="session-dashboard">
      <header class="dashboard-header">
        <div class="dashboard-header__left">
          <div class="dashboard-header__brand">
            <span class="brand-mark">{{ (session.title || 'S').charAt(0).toUpperCase() }}</span>
            <div class="dashboard-header__stack">
              <h1 class="dashboard-header__title">{{ session.title }}</h1>
              <div class="dashboard-header__meta">
                <span>{{ session.location || 'No location specified' }}</span>
                <span class="meta-dot">&bull;</span>
                <span>{{ formatDate(session.uploadDate) }}</span>
              </div>
            </div>
          </div>
        </div>
        <div class="dashboard-header__right">
          <span class="source-pill">{{ formatSourceType(session.sourceType) }}</span>
          <router-link to="/" class="dashboard-header__back">
            <i class="bi bi-arrow-left"></i>
            <span>Back to Sessions</span>
          </router-link>
        </div>
      </header>

      <section class="dashboard-table-card">
        <div class="dashboard-table-card__toolbar">
          <div>
            <h2 class="section-title">Shot Ledger</h2>
            <p class="section-subtitle">
              Real-time averages react to your club filters for quick comparisons.
            </p>
          </div>
          <div class="dashboard-table-card__controls">
            <label class="dashboard-table-card__control">
              <span>Club Filter</span>
              <select class="control-select" v-model="clubFilter">
                <option value="">All Clubs</option>
                <option
                  v-for="(_, club) in stats?.clubCounts"
                  :key="club"
                  :value="club"
                >
                  {{ club }}
                </option>
              </select>
            </label>
          </div>
        </div>

        <div class="dashboard-table-card__table">
          <table class="session-table">
            <thead>
              <tr>
                <th>Shot</th>
                <th>Club</th>
                <th>Club Speed</th>
                <th>Ball Speed</th>
                <th>Carry</th>
                <th>Total</th>
                <th>Horiz. Launch</th>
                <th>Spin Rate</th>
                <th>Shot Classification</th>
              </tr>
            </thead>
            <tbody>
              <tr
                v-if="aggregateAverages.shotCount"
                class="session-table__summary-row"
              >
                <td>
                  <div class="summary-badge">
                    <span class="summary-badge__title">Average</span>
                    <small class="summary-badge__subtitle">
                      Across {{ aggregateAverages.shotCount }} shots
                    </small>
                  </div>
                </td>
                <td class="text-center">—</td>
                <td class="text-center">{{ formatFixed(aggregateAverages.avgClubSpeed) }}</td>
                <td class="text-center">{{ formatFixed(aggregateAverages.avgBallSpeed) }}</td>
                <td class="text-center">{{ formatFixed(aggregateAverages.avgCarryDistance) }}</td>
                <td class="text-center">{{ formatFixed(aggregateAverages.avgTotalDistance) }}</td>
                <td class="text-center">{{ formatDegreeSigned(aggregateAverages.avgLaunchDirection) }}</td>
                <td class="text-center">{{ formatSpinShort(aggregateAverages.avgSpinRate) }}</td>
                <td class="text-center">
                  <span v-if="shotShapeSummary" class="summary-chip">
                    {{ shotShapeSummary.label }}
                    <small v-if="shotShapePercent">{{ shotShapePercent }}</small>
                  </span>
                  <span v-else>-</span>
                </td>
              </tr>
              <tr
                v-for="(shot, index) in filteredShots"
                :key="shot.id || index"
                class="session-table__row"
                :class="{ 'highlighted-shot': highlightedShotIndex === resolveShotIndex(shot, index) }"
                @click="openShotDetail(shot)"
                @mouseenter="handleShotHover(shot, index)"
                @mouseleave="handleShotLeave"
              >
                <td>
                  <div class="shot-label">
                    <span class="shot-number">#{{ shot.shotNumber }}</span>
                  </div>
                </td>
                <td>{{ shot.club || '—' }}</td>
                <td class="text-center">{{ formatFixed(shot.clubHeadSpeed) }}</td>
                <td class="text-center">{{ formatFixed(shot.ballSpeed) }}</td>
                <td class="text-center">{{ formatFixed(shot.carryDistance) }}</td>
                <td class="text-center">{{ formatFixed(shot.totalDistance) }}</td>
                <td class="text-center">{{ formatDegreeSigned(shot.launchDirection ?? shot.horizontalLaunch) }}</td>
                <td class="text-center">{{ formatSpinShort(shot.spinRate) }}</td>
                <td class="text-center">{{ shot.shotClassification || '—' }}</td>
              </tr>
            </tbody>
          </table>
          <div v-if="!filteredShots.length" class="session-table__empty">
            <i class="bi bi-search"></i>
            <p>No shots match the current filter selection.</p>
          </div>
        </div>
      </section>

      <section class="dashboard-summary" v-if="aggregateAverages.shotCount">
        <span class="dashboard-summary__title">Total Average</span>
        <div class="dashboard-summary__metrics">
          <div class="summary-metric">
            <span class="summary-metric__label">Carry</span>
            <span class="summary-metric__value">{{ formatDistanceValue(aggregateAverages.avgCarryDistance) }}</span>
          </div>
          <div class="summary-metric">
            <span class="summary-metric__label">Total</span>
            <span class="summary-metric__value">{{ formatDistanceValue(aggregateAverages.avgTotalDistance) }}</span>
          </div>
          <div class="summary-metric">
            <span class="summary-metric__label">Ball Speed</span>
            <span class="summary-metric__value">{{ formatSpeedValue(aggregateAverages.avgBallSpeed) }}</span>
          </div>
          <div class="summary-metric">
            <span class="summary-metric__label">Smash</span>
            <span class="summary-metric__value">{{ formatFixed(aggregateAverages.avgSmashFactor, 2) }}</span>
          </div>
        </div>
      </section>

      <section class="dashboard-core">
        <div class="core-column">
          <div class="metric-panel">
            <div class="metric-panel__header">
              <h3>Launch Profile</h3>
              <span>Trajectory tendencies</span>
            </div>
            <ul class="metric-list">
              <li>
                <span>Vertical Launch</span>
                <strong>{{ formatDegreeValue(aggregateAverages.avgLaunchAngle) }}</strong>
              </li>
              <li>
                <span>Horizontal Launch</span>
                <strong>{{ formatDegreeSigned(aggregateAverages.avgLaunchDirection) }}</strong>
              </li>
              <li>
                <span>Attack Angle</span>
                <strong>{{ formatDegreeValue(aggregateAverages.avgAttackAngle) }}</strong>
              </li>
            </ul>
          </div>

          <div class="metric-panel">
            <div class="metric-panel__header">
              <h3>Session Insights</h3>
              <span>Auto-generated highlights</span>
            </div>
            <ul class="metric-list">
              <li>
                <span>Best Shot</span>
                <strong>
                  <template v-if="bestShot">
                    {{ (bestShot.totalDistance || bestShot.carryDistance || 0).toFixed(1) }} yds
                  </template>
                  <template v-else>—</template>
                </strong>
              </li>
              <li>
                <span>Consistency</span>
                <strong>{{ distanceConsistency }}</strong>
              </li>
              <li>
                <span>Smash Factor</span>
                <strong>{{ averageSmashFactor }}</strong>
              </li>
              <li>
                <span>Data Quality</span>
                <strong :class="['quality-pill', dataQualityBadge]">{{ dataQualityText }}</strong>
              </li>
            </ul>
            <p class="metric-panel__footer">{{ sessionSummaryText }}</p>
          </div>

          <div class="metric-panel">
            <div class="metric-panel__header">
              <h3>Spin Insights</h3>
              <span>Shot shaping cues</span>
            </div>
            <ul class="metric-list">
              <li>
                <span>Spin Rate</span>
                <strong>{{ formatSpinValue(aggregateAverages.avgSpinRate) }}</strong>
              </li>
              <li>
                <span>Spin Axis</span>
                <strong>{{ formatDegreeSigned(aggregateAverages.avgSpinAxis) }}</strong>
              </li>
              <li>
                <span>Lateral Dispersion</span>
                <strong>{{ formatLateralDistance(aggregateAverages.avgLateral) }}</strong>
              </li>
            </ul>
          </div>
        </div>

        <div class="core-visual" ref="overviewSection">
          <div class="dashboard-visual-card">
            <div class="dashboard-visual-card__header">
              <h3>Shot Map</h3>
              <span>{{ aggregateAverages.shotCount }} shots visualized</span>
            </div>
            <SessionOverviewVisual
              :shots="shots"
              :session="session"
              :highlighted-shot-index="highlightedShotIndex"
              :is-minimized="isVisualMinimized"
              @shot-highlight="handleShotHighlight"
            />
          </div>
        </div>

        <aside class="core-sidebar">
          <div class="metrics-grid">
            <div
              v-for="tile in keyMetricTiles"
              :key="tile.label"
              class="metrics-tile"
            >
              <span class="metrics-tile__label">{{ tile.label }}</span>
              <strong class="metrics-tile__value">{{ tile.value }}</strong>
              <span v-if="tile.hint" class="metrics-tile__hint">{{ tile.hint }}</span>
            </div>
          </div>
        </aside>
      </section>

      <section class="dashboard-club-table" v-if="stats && stats.clubStats">
        <div class="section-header">
          <h2 class="section-title">Club Breakdown</h2>
          <p class="section-subtitle">Shot counts and averages per club</p>
        </div>
        <div class="club-table__wrapper">
          <table class="club-table">
            <thead>
              <tr>
                <th>Club</th>
                <th>Shots</th>
                <th>Avg. Carry</th>
                <th>Avg. Total</th>
                <th>Avg. Ball Speed</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(clubCount, club) in stats.clubCounts" :key="club">
                <td>{{ club }}</td>
                <td>{{ clubCount }}</td>
                <td>{{ formatDistanceValue(stats.clubStats[club]?.avgCarry) }}</td>
                <td>{{ formatDistanceValue(stats.clubStats[club]?.avgTotal) }}</td>
                <td>{{ formatSpeedValue(stats.clubStats[club]?.avgBallSpeed) }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </section>

      <section class="dashboard-analytics">
        <div class="section-header">
          <h2 class="section-title">Advanced Analytics</h2>
          <p class="section-subtitle">Dive deeper into patterns and correlations</p>
        </div>

        <div class="analytics-grid">
          <div class="analytics-card analytics-card--wide">
            <div class="analytics-card__header">
              <h3>Distance Progression</h3>
              <span>Monitor consistency over time</span>
            </div>
            <DistanceProgressionChart :shots="shots" />
          </div>

          <div class="analytics-card">
            <div class="analytics-card__header">
              <h3>Club Usage Distribution</h3>
            </div>
            <ClubUsageChart :clubCounts="stats?.clubCounts || {}" />
          </div>

          <div class="analytics-card">
            <div class="analytics-card__header">
              <h3>Average Distance by Club</h3>
            </div>
            <ClubDistanceChart :clubStats="stats?.clubStats || {}" />
          </div>

          <div class="analytics-card">
            <div class="analytics-card__header">
              <h3>Carry vs Total Distance</h3>
            </div>
            <CarryVsTotalChart :shots="shots" />
          </div>

          <div class="analytics-card">
            <div class="analytics-card__header">
              <h3>Distance Distribution</h3>
            </div>
            <DistanceDistributionChart :shots="shots" />
          </div>

          <div class="analytics-card analytics-card--wide">
            <div class="analytics-card__header">
              <h3>Shot Dispersion Pattern</h3>
            </div>
            <ShotDispersionChart :shots="shots" />
          </div>

          <div class="analytics-card">
            <div class="analytics-card__header">
              <h3>Accuracy Stats</h3>
            </div>
            <AccuracyStatsCard :shots="shots" />
          </div>

          <div class="analytics-card">
            <div class="analytics-card__header">
              <h3>Ball vs Club Speed</h3>
            </div>
            <BallVsClubSpeedChart :shots="shots" />
          </div>

          <div class="analytics-card">
            <div class="analytics-card__header">
              <h3>Launch Angle vs Spin Rate</h3>
            </div>
            <LaunchAngleSpinChart :shots="shots" />
          </div>

          <div
            class="analytics-card"
            v-if="hasAwesomeGolfData"
          >
            <div class="analytics-card__header">
              <h3>Smash Factor Analysis</h3>
            </div>
            <SmashFactorChart :shots="shots" />
          </div>

          <div
            class="analytics-card"
            v-if="hasAwesomeGolfData"
          >
            <div class="analytics-card__header">
              <h3>Descent Angle Analysis</h3>
            </div>
            <DescentAngleChart :shots="shots" />
          </div>
        </div>
      </section>
    </div>
  </div>

  <!-- Shot Detail Modal -->
  <ShotDetailModal 
    v-if="selectedShot" 
    :shot="selectedShot" 
    :modalId="'shotDetailModal'" 
  />
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed, watch, nextTick } from 'vue';
import { useRoute } from 'vue-router';
import axios from 'axios';

// Import chart components
import ClubUsageChart from './charts/ClubUsageChart.vue';
import ClubDistanceChart from './charts/ClubDistanceChart.vue';
import DistanceProgressionChart from './charts/DistanceProgressionChart.vue';
import CarryVsTotalChart from './charts/CarryVsTotalChart.vue';
import DistanceDistributionChart from './charts/DistanceDistributionChart.vue';
import ShotDispersionChart from './charts/ShotDispersionChart.vue';
import BallVsClubSpeedChart from './charts/BallVsClubSpeedChart.vue';
import LaunchAngleSpinChart from './charts/LaunchAngleSpinChart.vue';
import SmashFactorChart from './charts/SmashFactorChart.vue';
import DescentAngleChart from './charts/DescentAngleChart.vue';
import AccuracyStatsCard from './charts/AccuracyStatsCard.vue';
import ShotDetailModal from './ShotDetailModal.vue';
import SessionOverviewVisual from './SessionOverviewVisual.vue';

const route = useRoute();
const sessionId = computed(() => route.params.id);

const session = ref(null);
const shots = ref([]);
const stats = ref(null);
const loading = ref(true);
const error = ref(null);
const clubFilter = ref('');
const selectedShot = ref(null);

const isFiniteNumber = (value) => typeof value === 'number' && Number.isFinite(value);

const formatFixed = (value, digits = 1) => {
  return isFiniteNumber(value) ? value.toFixed(digits) : '—';
};

const formatDistanceValue = (value) => {
  return isFiniteNumber(value) ? `${value.toFixed(1)} yds` : '—';
};

const formatSpeedValue = (value) => {
  return isFiniteNumber(value) ? `${value.toFixed(1)} mph` : '—';
};

const formatDegreeValue = (value) => {
  return isFiniteNumber(value) ? `${value.toFixed(1)}°` : '—';
};

const formatDegreeSigned = (value) => {
  if (!isFiniteNumber(value)) {
    return '—';
  }
  if (value === 0) {
    return '0.0°';
  }
  const direction = value > 0 ? 'R' : 'L';
  return `${Math.abs(value).toFixed(1)}° ${direction}`;
};

const formatSpinValue = (value) => {
  return isFiniteNumber(value) ? `${Math.round(value).toLocaleString()} rpm` : '—';
};

const formatSpinShort = (value) => {
  return isFiniteNumber(value) ? Math.round(value).toLocaleString() : '—';
};

const formatPercentValue = (value) => {
  return isFiniteNumber(value) ? `${value.toFixed(0)}%` : '—';
};

const formatHeightValue = (value) => {
  return isFiniteNumber(value) ? `${value.toFixed(1)} ft` : '—';
};

const formatLateralDistance = (value) => {
  if (!isFiniteNumber(value)) {
    return '—';
  }
  if (value === 0) {
    return '0.0 yds';
  }
  const direction = value > 0 ? 'R' : 'L';
  return `${Math.abs(value).toFixed(1)} yds ${direction}`;
};

const unwrapApiResponse = (payload) => {
  if (!payload) {
    return null;
  }

  if (typeof payload === 'object' && payload !== null) {
    if ('success' in payload && payload.success === false) {
      const apiError = new Error(payload.message || 'Failed to load session data.');
      apiError.code = payload.errorCode;
      throw apiError;
    }

    if ('data' in payload) {
      return payload.data;
    }
  }

  return payload;
};


// Overview visual state
const highlightedShotIndex = ref(null);
const isVisualMinimized = ref(false);
const overviewSection = ref(null);

// Compute filtered shots based on club filter
const filteredShots = computed(() => {
  if (!clubFilter.value) return shots.value;
  return shots.value.filter(shot => shot.club === clubFilter.value);
});

const aggregateAverages = computed(() => {
  const currentShots = filteredShots.value;

  if (!currentShots.length) {
    return {
      shotCount: 0,
      avgClubSpeed: null,
      avgBallSpeed: null,
      avgCarryDistance: null,
      avgTotalDistance: null,
      avgRollDistance: null,
      avgLaunchAngle: null,
      avgLaunchDirection: null,
      avgSpinRate: null,
      avgSpinAxis: null,
      avgApex: null,
      avgAttackAngle: null,
      avgDescentAngle: null,
      avgLateral: null,
      avgSmashFactor: null
    };
  }

  const averageOf = (getter) => {
    const values = currentShots
      .map(getter)
      .filter(isFiniteNumber);

    if (!values.length) {
      return null;
    }

    return values.reduce((sum, value) => sum + value, 0) / values.length;
  };

  const avgCarry = stats.value?.avgCarryDistance ?? averageOf(shot => shot.carryDistance);
  const avgTotal = stats.value?.avgTotalDistance ?? averageOf(shot => shot.totalDistance);
  const avgBall = stats.value?.avgBallSpeed ?? averageOf(shot => shot.ballSpeed);

  return {
    shotCount: currentShots.length,
    avgClubSpeed: averageOf(shot => shot.clubHeadSpeed),
    avgBallSpeed: avgBall,
    avgCarryDistance: avgCarry,
    avgTotalDistance: avgTotal,
    avgRollDistance: avgTotal != null && avgCarry != null ? avgTotal - avgCarry : null,
    avgLaunchAngle: averageOf(shot => shot.launchAngle),
    avgLaunchDirection: averageOf(shot => shot.launchDirection ?? shot.horizontalLaunch),
    avgSpinRate: averageOf(shot => shot.spinRate),
    avgSpinAxis: averageOf(shot => shot.spinAxis),
    avgApex: averageOf(shot => shot.apex ?? shot.peakHeight),
    avgAttackAngle: averageOf(shot => shot.attackAngle),
    avgDescentAngle: averageOf(shot => shot.descentAngle),
    avgLateral: averageOf(shot => shot.deviation ?? shot.totalLateralDistance ?? shot.carryLateralDistance),
    avgSmashFactor: averageOf(shot => {
      if (isFiniteNumber(shot.ballSpeed) && isFiniteNumber(shot.clubHeadSpeed) && shot.clubHeadSpeed !== 0) {
        return shot.ballSpeed / shot.clubHeadSpeed;
      }
      return null;
    })
  };
});

const shotShapeSummary = computed(() => {
  const currentShots = filteredShots.value;
  if (!currentShots.length) {
    return null;
  }

  const counts = new Map();
  currentShots.forEach((shot) => {
    const label = (shot.shotClassification || 'Unclassified').trim() || 'Unclassified';
    counts.set(label, (counts.get(label) || 0) + 1);
  });

  let topLabel = null;
  let topCount = 0;
  counts.forEach((count, label) => {
    if (count > topCount) {
      topCount = count;
      topLabel = label;
    }
  });

  if (!topLabel) {
    return null;
  }

  return {
    label: topLabel,
    count: topCount,
    percentage: (topCount / currentShots.length) * 100
  };
});

const shotShapePercent = computed(() => {
  const summary = shotShapeSummary.value;
  if (!summary) {
    return null;
  }
  const percent = formatPercentValue(summary.percentage);
  return percent !== '-' ? percent : null;
});

const keyMetricTiles = computed(() => {
  const averages = aggregateAverages.value;
  const shape = shotShapeSummary.value;
  const shapePercent = shotShapePercent.value;

  return [
    { label: 'Carry', value: formatDistanceValue(averages.avgCarryDistance), hint: null },
    { label: 'Total', value: formatDistanceValue(averages.avgTotalDistance), hint: null },
    { label: 'Roll', value: formatDistanceValue(averages.avgRollDistance), hint: null },
    { label: 'Ball Speed', value: formatSpeedValue(averages.avgBallSpeed), hint: null },
    { label: 'Club Speed', value: formatSpeedValue(averages.avgClubSpeed), hint: null },
    { label: 'Smash', value: formatFixed(averages.avgSmashFactor, 2), hint: null },
    { label: 'Apex', value: formatHeightValue(averages.avgApex), hint: null },
    { label: 'Lateral', value: formatLateralDistance(averages.avgLateral), hint: null },
    { label: 'Descent', value: formatDegreeValue(averages.avgDescentAngle), hint: null },
    { label: 'Spin Rate', value: formatSpinValue(averages.avgSpinRate), hint: null },
    { label: 'Spin Axis', value: formatDegreeSigned(averages.avgSpinAxis), hint: null },
    {
      label: 'Shot Shape',
      value: shape ? shape.label : '-',
      hint: shapePercent || null
    }
  ];
});

const hasAwesomeGolfData = computed(() => {
  return session.value?.sourceType === 'AWESOME_GOLF' || 
         shots.value.some(shot => shot.smash || shot.descentAngle);
});

// Session insights computed properties
const bestShot = computed(() => {
  if (!shots.value.length) return null;
  return shots.value.reduce((best, shot) => {
    const shotDistance = shot.totalDistance || shot.carryDistance || 0;
    const bestDistance = best?.totalDistance || best?.carryDistance || 0;
    return shotDistance > bestDistance ? shot : best;
  }, null);
});

const distanceConsistency = computed(() => {
  if (!shots.value.length) return 'N/A';
  
  const distances = shots.value
    .map(shot => shot.totalDistance || shot.carryDistance)
    .filter(d => d && d > 0);
  
  if (distances.length < 3) return 'Limited Data';
  
  const avg = distances.reduce((sum, d) => sum + d, 0) / distances.length;
  const variance = distances.reduce((sum, d) => sum + Math.pow(d - avg, 2), 0) / distances.length;
  const stdDev = Math.sqrt(variance);
  const cv = (stdDev / avg) * 100; // Coefficient of variation
  
  if (cv < 10) return 'Excellent';
  if (cv < 20) return 'Good';
  if (cv < 30) return 'Average';
  return 'Inconsistent';
});

const averageSmashFactor = computed(() => {
  if (!shots.value.length) return 'N/A';
  
  const validShots = shots.value.filter(shot => 
    shot.ballSpeed && shot.clubHeadSpeed && 
    shot.ballSpeed > 0 && shot.clubHeadSpeed > 0
  );
  
  if (validShots.length === 0) return 'N/A';
  
  const totalSmash = validShots.reduce((sum, shot) => {
    return sum + (shot.ballSpeed / shot.clubHeadSpeed);
  }, 0);
  
  return (totalSmash / validShots.length).toFixed(1);
});

const dataQualityText = computed(() => {
  if (!shots.value.length) return 'No Data';
  
  const totalShots = shots.value.length;
  const shotsWithDistance = shots.value.filter(shot => 
    (shot.totalDistance && shot.totalDistance > 0) || 
    (shot.carryDistance && shot.carryDistance > 0)
  ).length;
  const shotsWithSpeed = shots.value.filter(shot => 
    shot.ballSpeed && shot.ballSpeed > 0
  ).length;
  
  const distanceQuality = (shotsWithDistance / totalShots) * 100;
  const speedQuality = (shotsWithSpeed / totalShots) * 100;
  const overallQuality = (distanceQuality + speedQuality) / 2;
  
  if (overallQuality >= 80) return 'Excellent';
  if (overallQuality >= 60) return 'Good';
  if (overallQuality >= 40) return 'Fair';
  return 'Poor';
});

const dataQualityBadge = computed(() => {
  switch (dataQualityText.value) {
    case 'Excellent': return 'quality-pill--excellent';
    case 'Good': return 'quality-pill--good';
    case 'Fair': return 'quality-pill--fair';
    case 'Poor': return 'quality-pill--poor';
    default: return 'quality-pill--neutral';
  }
});

const sessionSummaryText = computed(() => {
  if (!shots.value.length) return 'No shot data available for analysis.';
  
  const totalShots = shots.value.length;
  const avgDistance = stats.value?.avgTotalDistance || stats.value?.avgCarryDistance;
  const avgSpeed = stats.value?.avgBallSpeed;
  
  let summary = `Session contains ${totalShots} shots.`;
  
  if (avgDistance) {
    summary += ` Average distance: ${avgDistance.toFixed(1)} yards.`;
  }
  
  if (avgSpeed) {
    summary += ` Average ball speed: ${avgSpeed.toFixed(1)} mph.`;
  }
  
  if (hasAwesomeGolfData.value) {
    summary += ' Advanced metrics available from Awesome Golf system.';
  }
  
  return summary;
});

const fetchSessionData = async () => {
  loading.value = true;
  error.value = null;
  
  try {
    // Fetch session details
    const sessionResponse = await axios.get(`http://localhost:8080/api/sessions/${sessionId.value}`);
    session.value = unwrapApiResponse(sessionResponse.data);
    
    // Fetch session shots
    const shotsResponse = await axios.get(`http://localhost:8080/api/sessions/${sessionId.value}/shots`);
    shots.value = shotsResponse.data;
    
    // Fetch session stats
    const statsResponse = await axios.get(`http://localhost:8080/api/sessions/${sessionId.value}/stats`);
    stats.value = statsResponse.data;
  } catch (err) {
    console.error('Error fetching session data:', err);
    const message = err.response?.data?.message || err.message || 'Failed to load session data. Please try again later.';
    error.value = message;
  } finally {
    loading.value = false;
  }
};

const handleShotHighlight = (index) => {
  highlightedShotIndex.value = index;
};

const resolveShotIndex = (shot, fallbackIndex = -1) => {
  if (!shot) {
    return fallbackIndex;
  }

  if (shot.id) {
    const matchById = shots.value.findIndex(candidate => candidate.id === shot.id);
    if (matchById >= 0) {
      return matchById;
    }
  }

  if (shot.shotNumber) {
    const matchByNumber = shots.value.findIndex(candidate => candidate.shotNumber === shot.shotNumber);
    if (matchByNumber >= 0) {
      return matchByNumber;
    }
  }

  const matchByReference = shots.value.indexOf(shot);
  if (matchByReference >= 0) {
    return matchByReference;
  }

  return fallbackIndex;
};

const handleShotHover = (shot, index) => {
  highlightedShotIndex.value = resolveShotIndex(shot, index);
};

const handleShotLeave = () => {
  highlightedShotIndex.value = null;
};

const openShotDetail = (shot) => {
  selectedShot.value = shot;
  // Use Bootstrap's modal API to show the modal
  nextTick(() => {
    const modalElement = document.getElementById('shotDetailModal');
    if (modalElement) {
      const modal = new window.bootstrap.Modal(modalElement);
      modal.show();
    }
  });
};

// Scroll handling for minimizing the visual
const handleScroll = () => {
  if (!overviewSection.value) return;
  
  const rect = overviewSection.value.getBoundingClientRect();
  const scrollThreshold = 200; // Minimize when scrolled past this point
  
  isVisualMinimized.value = window.scrollY > scrollThreshold;
};

const formatDate = (dateString) => {
  if (!dateString) return 'Unknown date';
  
  const date = new Date(dateString);
  return new Intl.DateTimeFormat('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  }).format(date);
};

const formatNumber = (value) => {
  if (!isFiniteNumber(value)) return '—';
  return value.toLocaleString();
};

const formatSourceType = (sourceType) => {
  if (!sourceType) return 'Unknown Source';
  
  // Convert ENUM_STYLE to Title Case
  const formatted = sourceType.toLowerCase()
    .replace(/_/g, ' ')
    .replace(/\b\w/g, c => c.toUpperCase());
  
  return formatted;
};

// Load data when component mounts or when sessionId changes
watch(() => route.params.id, (newId) => {
  if (newId) {
    fetchSessionData();
  }
}, { immediate: true });

onMounted(() => {
  fetchSessionData();
  
  // Add scroll listener for visual minimization
  window.addEventListener('scroll', handleScroll);
});

// Cleanup scroll listener when component unmounts
onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll);
});
</script>

<style scoped>
.session-view-container {
  min-height: 100vh;
  padding: clamp(24px, 4vw, 52px);
  background: radial-gradient(circle at 15% -10%, rgba(255, 140, 26, 0.28) 0%, rgba(9, 9, 12, 0.92) 45%, #050505 100%);
  color: #f5f7fb;
}

.session-dashboard {
  --dashboard-surface: rgba(24, 26, 32, 0.88);
  --dashboard-surface-alt: rgba(18, 20, 26, 0.85);
  --dashboard-border: rgba(255, 255, 255, 0.08);
  --dashboard-muted: rgba(235, 240, 255, 0.65);
  --dashboard-accent: #ff8c1a;
  --dashboard-accent-strong: #ffa733;
  --dashboard-glow: rgba(255, 140, 26, 0.35);
  max-width: 1240px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: clamp(24px, 3.5vw, 40px);
}

.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: clamp(16px, 4vw, 32px);
  padding: clamp(20px, 3vw, 32px);
  border-radius: 24px;
  background: linear-gradient(120deg, rgba(24, 25, 32, 0.92), rgba(14, 14, 18, 0.96));
  border: 1px solid var(--dashboard-border);
  box-shadow: 0 24px 60px rgba(0, 0, 0, 0.35);
  position: relative;
  overflow: hidden;
}

.dashboard-header::after {
  content: '';
  position: absolute;
  inset: -40% 35% 60% -25%;
  background: radial-gradient(circle at center, var(--dashboard-glow) 0%, transparent 60%);
  opacity: 0.55;
  pointer-events: none;
}

.dashboard-header__left {
  display: flex;
  align-items: center;
  gap: 24px;
  z-index: 1;
}

.dashboard-header__brand {
  display: flex;
  align-items: center;
  gap: 18px;
}

.brand-mark {
  display: grid;
  place-items: center;
  width: 56px;
  height: 56px;
  border-radius: 16px;
  background: linear-gradient(135deg, var(--dashboard-accent), var(--dashboard-accent-strong));
  color: #111;
  font-weight: 700;
  font-size: 1.4rem;
  box-shadow: 0 12px 25px rgba(255, 140, 26, 0.35);
}

.dashboard-header__stack {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.dashboard-header__title {
  font-size: clamp(1.6rem, 3vw, 2rem);
  margin: 0;
  font-weight: 600;
  letter-spacing: 0.01em;
  color: #fefefe;
}

.dashboard-header__meta {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 0.95rem;
  color: var(--dashboard-muted);
}

.meta-dot {
  color: rgba(255, 255, 255, 0.4);
}

.dashboard-header__right {
  display: flex;
  align-items: center;
  gap: 16px;
  z-index: 1;
}

.source-pill {
  padding: 8px 16px;
  border-radius: 14px;
  background: rgba(255, 140, 26, 0.16);
  border: 1px solid rgba(255, 140, 26, 0.4);
  color: #fed7aa;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.06em;
  font-size: 0.78rem;
}

.dashboard-header__back {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 10px 18px;
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.12);
  color: #f1f5f9;
  font-weight: 500;
  text-decoration: none;
  transition: background 0.2s ease, transform 0.2s ease, border-color 0.2s ease;
}

.dashboard-header__back:hover {
  background: rgba(255, 140, 26, 0.2);
  border-color: rgba(255, 140, 26, 0.45);
  transform: translateY(-1px);
}

.dashboard-table-card {
  background: var(--dashboard-surface);
  border-radius: 28px;
  border: 1px solid var(--dashboard-border);
  padding: clamp(20px, 3vw, 32px);
  box-shadow: 0 18px 50px rgba(0, 0, 0, 0.32);
  display: flex;
  flex-direction: column;
  gap: clamp(18px, 2.5vw, 28px);
}

.dashboard-table-card__toolbar {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  flex-wrap: wrap;
  gap: 16px;
}

.section-title {
  margin: 0;
  font-size: clamp(1.2rem, 2.4vw, 1.5rem);
  font-weight: 600;
  color: #fdfdfd;
}

.section-subtitle {
  margin: 6px 0 0;
  font-size: 0.92rem;
  color: var(--dashboard-muted);
  max-width: 460px;
}

.dashboard-table-card__controls {
  display: flex;
  align-items: center;
  gap: 12px;
}

.dashboard-table-card__control {
  display: flex;
  flex-direction: column;
  gap: 6px;
  font-size: 0.85rem;
  color: var(--dashboard-muted);
  text-transform: uppercase;
  letter-spacing: 0.08em;
}

.control-select {
  min-width: 160px;
  padding: 10px 14px;
  border-radius: 12px;
  border: 1px solid rgba(148, 163, 184, 0.35);
  background: rgba(10, 12, 16, 0.9);
  color: #f8fafc;
  font-weight: 500;
  outline: none;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

.control-select:focus {
  border-color: var(--dashboard-accent);
  box-shadow: 0 0 0 3px rgba(255, 140, 26, 0.2);
}

.dashboard-table-card__table {
  position: relative;
  border-radius: 22px;
  overflow: hidden;
  border: 1px solid rgba(148, 163, 184, 0.15);
}

.session-table {
  width: 100%;
  border-collapse: separate;
  border-spacing: 0 10px;
  color: inherit;
  font-size: 0.92rem;
}

.session-table thead th {
  text-transform: uppercase;
  letter-spacing: 0.08em;
  font-size: 0.75rem;
  font-weight: 600;
  color: rgba(248, 250, 252, 0.7);
  padding: 12px 16px;
  background: rgba(14, 16, 21, 0.85);
  border-bottom: 1px solid rgba(148, 163, 184, 0.2);
}

.session-table tbody tr {
  background: rgba(16, 19, 27, 0.85);
  transition: transform 0.18s ease, background 0.18s ease;
}

.session-table tbody tr:hover {
  transform: translateY(-2px);
  background: rgba(21, 25, 34, 0.92);
}

.session-table tbody td {
  padding: 14px 16px;
  border-top: 1px solid rgba(148, 163, 184, 0.08);
  border-bottom: 1px solid rgba(148, 163, 184, 0.08);
  color: #f8fafc;
}

.session-table tbody td:first-child {
  border-left: 1px solid rgba(148, 163, 184, 0.08);
  border-top-left-radius: 18px;
  border-bottom-left-radius: 18px;
}

.session-table tbody td:last-child {
  border-right: 1px solid rgba(148, 163, 184, 0.08);
  border-top-right-radius: 18px;
  border-bottom-right-radius: 18px;
}

.session-table__summary-row td {
  background: rgba(255, 140, 26, 0.12);
  color: #ffd7aa;
  font-weight: 600;
  border-top: 1px solid rgba(255, 140, 26, 0.35);
  border-bottom: 1px solid rgba(255, 140, 26, 0.35);
}

.summary-badge {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.summary-badge__title {
  text-transform: uppercase;
  letter-spacing: 0.08em;
  font-size: 0.78rem;
}

.summary-badge__subtitle {
  font-size: 0.72rem;
  color: rgba(255, 215, 170, 0.68);
}

.summary-chip {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 10px;
  border-radius: 999px;
  background: rgba(255, 140, 26, 0.18);
  border: 1px solid rgba(255, 140, 26, 0.4);
  font-size: 0.78rem;
  color: #ffd7aa;
}

.shot-label {
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.shot-number {
  font-weight: 600;
  color: #f8fafc;
}

.highlighted-shot td {
  background: rgba(255, 196, 45, 0.14);
  color: #ffe8b5;
  border-top-color: rgba(255, 196, 45, 0.35);
  border-bottom-color: rgba(255, 196, 45, 0.35);
}

.session-table__empty {
  display: grid;
  place-items: center;
  gap: 8px;
  padding: 32px 12px;
  color: var(--dashboard-muted);
  background: rgba(12, 14, 18, 0.92);
  border-top: 1px solid rgba(148, 163, 184, 0.2);
}

.session-table__empty i {
  font-size: 1.4rem;
  color: rgba(255, 140, 26, 0.55);
}

.dashboard-summary {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 18px;
  padding: 16px 22px;
  border-radius: 18px;
  background: linear-gradient(90deg, rgba(255, 140, 26, 0.24), rgba(255, 140, 26, 0.12));
  border: 1px solid rgba(255, 140, 26, 0.32);
  color: #fff4e6;
}

.dashboard-summary__title {
  font-size: 1rem;
  font-weight: 600;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.dashboard-summary__metrics {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
  gap: 14px;
  flex: 1;
}

.summary-metric {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.summary-metric__label {
  font-size: 0.7rem;
  letter-spacing: 0.1em;
  text-transform: uppercase;
  color: rgba(255, 240, 220, 0.7);
}

.summary-metric__value {
  font-size: 1.1rem;
  font-weight: 600;
  color: #fff7ed;
}

.dashboard-core {
  display: grid;
  grid-template-columns: minmax(260px, 1fr) minmax(360px, 1.3fr) minmax(220px, 0.9fr);
  gap: clamp(18px, 3vw, 28px);
  align-items: stretch;
}

.core-column {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.metric-panel {
  background: var(--dashboard-surface-alt);
  border-radius: 22px;
  padding: 20px 22px;
  border: 1px solid rgba(148, 163, 184, 0.12);
  box-shadow: inset 0 0 0 1px rgba(148, 163, 184, 0.05);
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.metric-panel__header {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.metric-panel__header h3 {
  margin: 0;
  font-size: 1.1rem;
  font-weight: 600;
  color: #f8fafc;
}

.metric-panel__header span {
  font-size: 0.82rem;
  color: var(--dashboard-muted);
}

.metric-list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.metric-list li {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 0.92rem;
  color: rgba(248, 250, 252, 0.85);
}

.metric-list strong {
  font-weight: 600;
  color: #ffffff;
}

.metric-panel__footer {
  margin: 0;
  font-size: 0.8rem;
  line-height: 1.5;
  color: var(--dashboard-muted);
}

.quality-pill {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 80px;
  padding: 4px 10px;
  border-radius: 999px;
  font-size: 0.78rem;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.08em;
}

.quality-pill--excellent {
  background: rgba(34, 197, 94, 0.18);
  color: #bbf7d0;
}

.quality-pill--good {
  background: rgba(59, 130, 246, 0.18);
  color: #bfdbfe;
}

.quality-pill--fair {
  background: rgba(250, 204, 21, 0.22);
  color: #fef3c7;
}

.quality-pill--poor {
  background: rgba(248, 113, 113, 0.22);
  color: #fee2e2;
}

.quality-pill--neutral {
  background: rgba(148, 163, 184, 0.22);
  color: rgba(226, 232, 240, 0.8);
}

.core-visual {
  display: flex;
  align-items: stretch;
}

.dashboard-visual-card {
  position: relative;
  flex: 1;
  background: linear-gradient(145deg, rgba(12, 14, 18, 0.92), rgba(20, 22, 28, 0.95));
  border-radius: 26px;
  padding: clamp(18px, 3vw, 28px);
  border: 1px solid rgba(148, 163, 184, 0.14);
  box-shadow: 0 22px 50px rgba(0, 0, 0, 0.35);
}

.dashboard-visual-card__header {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  gap: 12px;
  margin-bottom: 16px;
}

.dashboard-visual-card__header h3 {
  margin: 0;
  font-size: 1.2rem;
  color: #f8fafc;
}

.dashboard-visual-card__header span {
  font-size: 0.8rem;
  color: var(--dashboard-muted);
}

.core-sidebar {
  display: flex;
  align-items: stretch;
}

.metrics-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(120px, 1fr));
  gap: 12px;
}

.metrics-tile {
  background: rgba(17, 19, 24, 0.88);
  border-radius: 18px;
  padding: 16px;
  border: 1px solid rgba(148, 163, 184, 0.14);
  display: flex;
  flex-direction: column;
  gap: 6px;
  box-shadow: inset 0 0 0 1px rgba(148, 163, 184, 0.06);
}

.metrics-tile__label {
  font-size: 0.7rem;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  color: var(--dashboard-muted);
}

.metrics-tile__value {
  font-size: 1.05rem;
  font-weight: 600;
  color: #fdfdfd;
}

.metrics-tile__hint {
  font-size: 0.75rem;
  color: rgba(248, 250, 252, 0.55);
}

.dashboard-club-table,
.dashboard-analytics {
  background: var(--dashboard-surface);
  border-radius: 28px;
  border: 1px solid var(--dashboard-border);
  padding: clamp(20px, 3vw, 32px);
  box-shadow: 0 18px 50px rgba(0, 0, 0, 0.32);
  display: flex;
  flex-direction: column;
  gap: clamp(18px, 2.5vw, 26px);
}

.section-header {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.section-subtitle {
  font-size: 0.9rem;
  color: var(--dashboard-muted);
  margin: 0;
}

.club-table__wrapper {
  overflow-x: auto;
  border-radius: 16px;
  border: 1px solid rgba(148, 163, 184, 0.18);
}

.club-table {
  width: 100%;
  border-collapse: collapse;
  min-width: 520px;
}

.club-table thead th {
  padding: 14px 16px;
  text-transform: uppercase;
  font-size: 0.75rem;
  letter-spacing: 0.08em;
  font-weight: 600;
  background: rgba(14, 16, 21, 0.85);
  color: rgba(248, 250, 252, 0.7);
  border-bottom: 1px solid rgba(148, 163, 184, 0.2);
  text-align: left;
}

.club-table tbody td {
  padding: 14px 16px;
  border-bottom: 1px solid rgba(148, 163, 184, 0.12);
  color: #f1f5f9;
}

.club-table tbody tr:last-child td {
  border-bottom: none;
}

.analytics-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: clamp(16px, 2.5vw, 24px);
}

.analytics-card {
  background: var(--dashboard-surface-alt);
  border-radius: 22px;
  padding: clamp(14px, 2.5vw, 22px);
  border: 1px solid rgba(148, 163, 184, 0.12);
  box-shadow: inset 0 0 0 1px rgba(148, 163, 184, 0.04);
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.analytics-card--wide {
  grid-column: span 2;
}

.analytics-card__header {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.analytics-card__header h3 {
  margin: 0;
  font-size: 1rem;
  color: #f8fafc;
  font-weight: 600;
}

.analytics-card__header span {
  font-size: 0.82rem;
  color: var(--dashboard-muted);
}

@media (max-width: 1100px) {
  .dashboard-core {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .core-sidebar {
    grid-column: span 2;
  }

  .metrics-grid {
    grid-template-columns: repeat(auto-fit, minmax(140px, 1fr));
  }

  .analytics-card--wide {
    grid-column: span 2;
  }
}

@media (max-width: 768px) {
  .session-view-container {
    padding: 24px 18px 64px;
  }

  .dashboard-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .dashboard-summary {
    flex-direction: column;
    align-items: flex-start;
  }

  .dashboard-table-card__toolbar {
    flex-direction: column;
    align-items: flex-start;
  }

  .dashboard-core {
    grid-template-columns: 1fr;
  }

  .core-sidebar,
  .analytics-card--wide {
    grid-column: span 1;
  }
}
</style>
