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
    <div v-else-if="session">
      <!-- Session Header -->
      <div class="d-flex justify-content-between align-items-start mb-4">
        <div>
          <h1 class="h2">{{ session.title }}</h1>
          <p class="text-muted">
            {{ session.location || 'No location specified' }} | 
            Uploaded {{ formatDate(session.uploadDate) }} |
            Source: {{ formatSourceType(session.sourceType) }}
          </p>
        </div>
        <div>
          <router-link to="/" class="btn btn-outline-primary">
            <i class="bi bi-arrow-left"></i> Back
          </router-link>
        </div>
      </div>
      
      <!-- Data Visualization Tabs -->
      <div class="mb-4">
        <ul class="nav nav-tabs" id="analysisTab" role="tablist">
          <li class="nav-item" role="presentation">
            <button class="nav-link active" id="overview-tab" data-bs-toggle="tab" data-bs-target="#overview" type="button" role="tab">
              <i class="bi bi-graph-up"></i> Overview
            </button>
          </li>
          <li class="nav-item" role="presentation">
            <button class="nav-link" id="distance-tab" data-bs-toggle="tab" data-bs-target="#distance" type="button" role="tab">
              <i class="bi bi-bullseye"></i> Distance Analysis
            </button>
          </li>
          <li class="nav-item" role="presentation">
            <button class="nav-link" id="accuracy-tab" data-bs-toggle="tab" data-bs-target="#accuracy" type="button" role="tab">
              <i class="bi bi-crosshair"></i> Accuracy & Dispersion
            </button>
          </li>
          <li class="nav-item" role="presentation">
            <button class="nav-link" id="performance-tab" data-bs-toggle="tab" data-bs-target="#performance" type="button" role="tab">
              <i class="bi bi-speedometer2"></i> Performance Metrics
            </button>
          </li>
          <li class="nav-item" role="presentation">
            <button class="nav-link" id="shots-tab" data-bs-toggle="tab" data-bs-target="#shots" type="button" role="tab">
              <i class="bi bi-table"></i> Shot Details
            </button>
          </li>
        </ul>
      </div>

      <div class="tab-content" id="analysisTabContent">
        <!-- Overview Tab -->
        <div class="tab-pane fade show active" id="overview" role="tabpanel">
          <!-- Stats Cards Row -->
          <div class="row mb-4" v-if="stats">
            <div class="col-md-3 col-sm-6 mb-3">
              <div class="card h-100 text-center">
                <div class="card-body">
                  <i class="bi bi-bullseye text-primary mb-2" style="font-size: 2rem;"></i>
                  <h5 class="card-title">Total Shots</h5>
                  <h2 class="display-6 text-primary">{{ stats.totalShots || 0 }}</h2>
                </div>
              </div>
            </div>
            
            <div class="col-md-3 col-sm-6 mb-3">
              <div class="card h-100 text-center">
                <div class="card-body">
                  <i class="bi bi-arrow-up-right text-success mb-2" style="font-size: 2rem;"></i>
                  <h5 class="card-title">Avg. Carry</h5>
                  <h2 class="display-6 text-success">{{ (stats.avgCarryDistance || 0).toFixed(1) }}</h2>
                  <p class="text-muted">yards</p>
                </div>
              </div>
            </div>
            
            <div class="col-md-3 col-sm-6 mb-3">
              <div class="card h-100 text-center">
                <div class="card-body">
                  <i class="bi bi-graph-up text-info mb-2" style="font-size: 2rem;"></i>
                  <h5 class="card-title">Avg. Total</h5>
                  <h2 class="display-6 text-info">{{ (stats.avgTotalDistance || 0).toFixed(1) }}</h2>
                  <p class="text-muted">yards</p>
                </div>
              </div>
            </div>
            
            <div class="col-md-3 col-sm-6 mb-3">
              <div class="card h-100 text-center">
                <div class="card-body">
                  <i class="bi bi-speedometer2 text-warning mb-2" style="font-size: 2rem;"></i>
                  <h5 class="card-title">Avg. Ball Speed</h5>
                  <h2 class="display-6 text-warning">{{ (stats.avgBallSpeed || 0).toFixed(1) }}</h2>
                  <p class="text-muted">mph</p>
                </div>
              </div>
            </div>
          </div>

          <!-- Session Insights Summary -->
          <div class="row mb-4" v-if="stats && shots.length > 0">
            <div class="col-12">
              <div class="card bg-light border-primary">
                <div class="card-header bg-primary text-white">
                  <h5 class="mb-0"><i class="bi bi-lightbulb"></i> Session Insights</h5>
                </div>
                <div class="card-body">
                  <div class="row">
                    <div class="col-md-3 mb-2">
                      <div class="insight-item">
                        <strong>Best Shot:</strong>
                        <span>{{ bestShot?.totalDistance ? (bestShot.totalDistance).toFixed(1) + ' yds' : 'N/A' }}</span>
                      </div>
                    </div>
                    <div class="col-md-3 mb-2">
                      <div class="insight-item">
                        <strong>Consistency:</strong>
                        <span>{{ distanceConsistency }}</span>
                      </div>
                    </div>
                    <div class="col-md-3 mb-2">
                      <div class="insight-item">
                        <strong>Avg. Smash Factor:</strong>
                        <span>{{ averageSmashFactor }}</span>
                      </div>
                    </div>
                    <div class="col-md-3 mb-2">
                      <div class="insight-item">
                        <strong>Data Quality:</strong>
                        <span class="badge" :class="dataQualityBadge">{{ dataQualityText }}</span>
                      </div>
                    </div>
                  </div>
                  <div class="row mt-2">
                    <div class="col-12">
                      <small class="text-muted">
                        <i class="bi bi-info-circle"></i>
                        {{ sessionSummaryText }}
                      </small>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Club Performance Overview -->
          <div class="row">
            <div class="col-lg-6 mb-4">
              <div class="card h-100">
                <div class="card-header">
                  <h5 class="mb-0"><i class="bi bi-bar-chart"></i> Club Usage Distribution</h5>
                </div>
                <div class="card-body">
                  <ClubUsageChart :clubCounts="stats?.clubCounts || {}" />
                </div>
              </div>
            </div>
            
            <div class="col-lg-6 mb-4">
              <div class="card h-100">
                <div class="card-header">
                  <h5 class="mb-0"><i class="bi bi-graph-up"></i> Average Distance by Club</h5>
                </div>
                <div class="card-body">
                  <ClubDistanceChart :clubStats="stats?.clubStats || {}" />
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Distance Analysis Tab -->
        <div class="tab-pane fade" id="distance" role="tabpanel">
          <div class="row">
            <div class="col-lg-12 mb-4">
              <div class="card">
                <div class="card-header">
                  <h5 class="mb-0"><i class="bi bi-graph-up"></i> Distance Progression</h5>
                </div>
                <div class="card-body">
                  <DistanceProgressionChart :shots="shots" />
                </div>
              </div>
            </div>
          </div>
          
          <div class="row">
            <div class="col-lg-6 mb-4">
              <div class="card">
                <div class="card-header">
                  <h5 class="mb-0"><i class="bi bi-bullseye"></i> Carry vs Total Distance</h5>
                </div>
                <div class="card-body">
                  <CarryVsTotalChart :shots="shots" />
                </div>
              </div>
            </div>
            
            <div class="col-lg-6 mb-4">
              <div class="card">
                <div class="card-header">
                  <h5 class="mb-0"><i class="bi bi-bar-chart"></i> Distance Distribution</h5>
                </div>
                <div class="card-body">
                  <DistanceDistributionChart :shots="shots" />
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Accuracy & Dispersion Tab -->
        <div class="tab-pane fade" id="accuracy" role="tabpanel">
          <div class="row">
            <div class="col-lg-8 mb-4">
              <div class="card">
                <div class="card-header">
                  <h5 class="mb-0"><i class="bi bi-crosshair"></i> Shot Dispersion Pattern</h5>
                </div>
                <div class="card-body">
                  <ShotDispersionChart :shots="shots" />
                </div>
              </div>
            </div>
            
            <div class="col-lg-4 mb-4">
              <div class="card">
                <div class="card-header">
                  <h5 class="mb-0"><i class="bi bi-target"></i> Accuracy Stats</h5>
                </div>
                <div class="card-body">
                  <AccuracyStatsCard :shots="shots" />
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Performance Metrics Tab -->
        <div class="tab-pane fade" id="performance" role="tabpanel">
          <div class="row">
            <div class="col-lg-6 mb-4">
              <div class="card">
                <div class="card-header">
                  <h5 class="mb-0"><i class="bi bi-speedometer2"></i> Ball Speed vs Club Speed</h5>
                </div>
                <div class="card-body">
                  <BallVsClubSpeedChart :shots="shots" />
                </div>
              </div>
            </div>
            
            <div class="col-lg-6 mb-4">
              <div class="card">
                <div class="card-header">
                  <h5 class="mb-0"><i class="bi bi-arrow-up"></i> Launch Angle vs Spin Rate</h5>
                </div>
                <div class="card-body">
                  <LaunchAngleSpinChart :shots="shots" />
                </div>
              </div>
            </div>
          </div>
          
          <div class="row" v-if="hasAwesomeGolfData">
            <div class="col-lg-6 mb-4">
              <div class="card">
                <div class="card-header">
                  <h5 class="mb-0"><i class="bi bi-lightning"></i> Smash Factor Analysis</h5>
                </div>
                <div class="card-body">
                  <SmashFactorChart :shots="shots" />
                </div>
              </div>
            </div>
            
            <div class="col-lg-6 mb-4">
              <div class="card">
                <div class="card-header">
                  <h5 class="mb-0"><i class="bi bi-graph-down"></i> Descent Angle Analysis</h5>
                </div>
                <div class="card-body">
                  <DescentAngleChart :shots="shots" />
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Shot Details Tab -->
        <div class="tab-pane fade" id="shots" role="tabpanel">
      <div class="row mb-4" v-if="stats">
        <div class="col-md-3 col-sm-6 mb-3">
          <div class="card h-100">
            <div class="card-body text-center">
              <h5 class="card-title">Total Shots</h5>
              <h2 class="display-4">{{ stats.totalShots || 0 }}</h2>
            </div>
          </div>
        </div>
        
        <div class="col-md-3 col-sm-6 mb-3">
          <div class="card h-100">
            <div class="card-body text-center">
              <h5 class="card-title">Avg. Carry</h5>
              <h2 class="display-4">{{ (stats.avgCarryDistance || 0).toFixed(1) }}</h2>
              <p class="text-muted">yards</p>
            </div>
          </div>
        </div>
        
        <div class="col-md-3 col-sm-6 mb-3">
          <div class="card h-100">
            <div class="card-body text-center">
              <h5 class="card-title">Avg. Total</h5>
              <h2 class="display-4">{{ (stats.avgTotalDistance || 0).toFixed(1) }}</h2>
              <p class="text-muted">yards</p>
            </div>
          </div>
        </div>
        
        <div class="col-md-3 col-sm-6 mb-3">
          <div class="card h-100">
            <div class="card-body text-center">
              <h5 class="card-title">Avg. Ball Speed</h5>
              <h2 class="display-4">{{ (stats.avgBallSpeed || 0).toFixed(1) }}</h2>
              <p class="text-muted">mph</p>
            </div>
          </div>
        </div>
      </div>
      
      <!-- Club Stats Section -->
      <div class="card mb-4" v-if="stats && stats.clubStats">
        <div class="card-header">
          <h2 class="h5 mb-0">Club Performance</h2>
        </div>
        <div class="card-body">
          <div class="table-responsive">
            <table class="table table-hover">
              <thead>
                <tr>
                  <th>Club</th>
                  <th class="text-center">Shots</th>
                  <th class="text-center">Avg. Carry (yds)</th>
                  <th class="text-center">Avg. Total (yds)</th>
                  <th class="text-center">Avg. Ball Speed (mph)</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="(clubCount, club) in stats.clubCounts" :key="club">
                  <td><strong>{{ club }}</strong></td>
                  <td class="text-center">{{ clubCount }}</td>
                  <td class="text-center">{{ (stats.clubStats[club]?.avgCarry || 0).toFixed(1) }}</td>
                  <td class="text-center">{{ (stats.clubStats[club]?.avgTotal || 0).toFixed(1) }}</td>
                  <td class="text-center">{{ (stats.clubStats[club]?.avgBallSpeed || 0).toFixed(1) }}</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
      
      <!-- Shot Data Table -->
      <div class="card">
        <div class="card-header d-flex justify-content-between align-items-center">
          <h2 class="h5 mb-0">Shot Details</h2>
          <div>
            <div class="input-group input-group-sm">
              <span class="input-group-text">Filter by club:</span>
              <select class="form-select" v-model="clubFilter">
                <option value="">All Clubs</option>
                <option v-for="(_, club) in stats?.clubCounts" :key="club" :value="club">
                  {{ club }}
                </option>
              </select>
            </div>
          </div>
        </div>
        <div class="card-body p-0">
          <div class="table-responsive">
            <table class="table table-striped table-hover mb-0">
              <thead>
                <tr>
                  <th>Shot #</th>
                  <th>Club</th>
                  <th class="text-center">Carry (yds)</th>
                  <th class="text-center">Total (yds)</th>
                  <th class="text-center">Ball Speed (mph)</th>
                  <th class="text-center">Club Speed (mph)</th>
                  <th class="text-center">Launch Angle (°)</th>
                  <th class="text-center">Spin Rate (rpm)</th>
                </tr>
              </thead>
              <tbody>
                <tr 
                  v-for="shot in filteredShots" 
                  :key="shot.id"
                  class="shot-row"
                  @click="openShotDetail(shot)"
                  style="cursor: pointer;"
                  @mouseover="$event.currentTarget.style.backgroundColor = '#f8f9fa'"
                  @mouseout="$event.currentTarget.style.backgroundColor = ''"
                >
                  <td>
                    <i class="bi bi-eye text-primary me-1"></i>{{ shot.shotNumber }}
                  </td>
                  <td>{{ shot.club }}</td>
                  <td class="text-center">{{ (shot.carryDistance || 0).toFixed(1) }}</td>
                  <td class="text-center">{{ (shot.totalDistance || 0).toFixed(1) }}</td>
                  <td class="text-center">{{ (shot.ballSpeed || 0).toFixed(1) }}</td>
                  <td class="text-center">{{ (shot.clubHeadSpeed || 0).toFixed(1) }}</td>
                  <td class="text-center">{{ (shot.launchAngle || 0).toFixed(1) }}</td>
                  <td class="text-center">{{ formatNumber(shot.spinRate) }}</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
      </div>
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
import { ref, onMounted, computed, watch, nextTick } from 'vue';
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

const route = useRoute();
const sessionId = computed(() => route.params.id);

const session = ref(null);
const shots = ref([]);
const stats = ref(null);
const loading = ref(true);
const error = ref(null);
const clubFilter = ref('');
const selectedShot = ref(null);

// Compute filtered shots based on club filter
const filteredShots = computed(() => {
  if (!clubFilter.value) return shots.value;
  return shots.value.filter(shot => shot.club === clubFilter.value);
});

// Check if session has Awesome Golf data (extended metrics)
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
    case 'Excellent': return 'bg-success';
    case 'Good': return 'bg-primary';
    case 'Fair': return 'bg-warning';
    case 'Poor': return 'bg-danger';
    default: return 'bg-secondary';
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
    session.value = sessionResponse.data;
    
    // Fetch session shots
    const shotsResponse = await axios.get(`http://localhost:8080/api/sessions/${sessionId.value}/shots`);
    shots.value = shotsResponse.data;
    
    // Fetch session stats
    const statsResponse = await axios.get(`http://localhost:8080/api/sessions/${sessionId.value}/stats`);
    stats.value = statsResponse.data;
  } catch (err) {
    console.error('Error fetching session data:', err);
    error.value = 'Failed to load session data. Please try again later.';
  } finally {
    loading.value = false;
  }
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
  if (value === null || value === undefined) return '';
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
});
</script>

<style scoped>
.session-view-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.insight-item {
  font-size: 0.9rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.25rem 0;
}

.insight-item strong {
  color: #495057;
  margin-right: 0.5rem;
}
</style>