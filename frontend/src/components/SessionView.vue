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
          <div class="alert alert-info mb-2">
            <strong>Session ID:</strong> {{ session.id }} | 
            <strong>Shots:</strong> {{ shots.length }}
          </div>
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
      
      <!-- Club Filter for Visualizations -->
      <div class="mb-4">
        <div class="input-group">
          <span class="input-group-text">Filter visualizations by club:</span>
          <select class="form-select" v-model="clubFilter">
            <option value="">All Clubs</option>
            <option v-for="(_, club) in stats?.clubCounts" :key="club" :value="club">
              {{ club }}
            </option>
          </select>
        </div>
      </div>
      
      <!-- Shot Visualization Section -->
      <div class="mb-4">
        <div class="card">
          <div class="card-header bg-primary text-white">
            <h2 class="h5 mb-0">Shot Visualization</h2>
          </div>
          <div class="card-body">
            <div v-if="shots && shots.length > 0">
              <!-- Debug info for developers (can be removed in production) -->
              <div class="alert alert-info mb-3" v-if="shots.length > 0">
                <strong>Shot data loaded:</strong> {{shots.length}} shots available for visualization
              </div>
              
              <div class="row">
                <!-- Top Down View - Full width on smaller screens, half on larger -->
                <div class="col-xl-6 col-lg-6 col-md-12 mb-4">
                  <TopDownView :shots="shots" :clubFilter="clubFilter" />
                </div>
                
                <!-- Side View - Full width on smaller screens, half on larger -->
                <div class="col-xl-6 col-lg-6 col-md-12 mb-4">
                  <SideView :shots="shots" :clubFilter="clubFilter" />
                </div>
                
                <!-- For testing purposes - can be removed once main charts are working -->
                <div class="col-12 mt-2 mb-4">
                  <div class="card">
                    <div class="card-header">Test Chart</div>
                    <div class="card-body">
                      <SimplerChart />
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <div v-else class="text-center p-4">
              <i class="bi bi-exclamation-circle text-warning" style="font-size: 2rem;"></i>
              <p class="mt-3">No shot data available to visualize. Please upload a session with valid shot data.</p>
            </div>
          </div>
        </div>
      </div>
      
      <!-- Stats Cards Row -->
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
              <h2 class="display-4">{{ stats.avgCarryDistance || 0 }}</h2>
              <p class="text-muted">yards</p>
            </div>
          </div>
        </div>
        
        <div class="col-md-3 col-sm-6 mb-3">
          <div class="card h-100">
            <div class="card-body text-center">
              <h5 class="card-title">Avg. Total</h5>
              <h2 class="display-4">{{ stats.avgTotalDistance || 0 }}</h2>
              <p class="text-muted">yards</p>
            </div>
          </div>
        </div>
        
        <div class="col-md-3 col-sm-6 mb-3">
          <div class="card h-100">
            <div class="card-body text-center">
              <h5 class="card-title">Avg. Ball Speed</h5>
              <h2 class="display-4">{{ stats.avgBallSpeed || 0 }}</h2>
              <p class="text-muted">mph</p>
            </div>
          </div>
        </div>
      </div>
      
      <!-- Club Stats Section -->
      <div class="card mb-4" v-if="stats && stats.clubStats">
        <div class="card-header d-flex justify-content-between align-items-center">
          <h2 class="h5 mb-0">Club Performance</h2>
          <div class="btn-group btn-group-sm">
            <button class="btn btn-outline-primary" :class="{ active: selectedMetric === 'avgCarry' }" 
                    @click="selectedMetric = 'avgCarry'">Carry Distance</button>
            <button class="btn btn-outline-primary" :class="{ active: selectedMetric === 'avgTotal' }"
                    @click="selectedMetric = 'avgTotal'">Total Distance</button>
            <button class="btn btn-outline-primary" :class="{ active: selectedMetric === 'avgBallSpeed' }"
                    @click="selectedMetric = 'avgBallSpeed'">Ball Speed</button>
          </div>
        </div>
        <div class="card-body">
          <!-- Club Performance Chart -->
          <div class="row mb-4">
            <div class="col-12">
              <ClubStatsChart :stats="stats" :metric="selectedMetric" />
            </div>
          </div>
          
          <!-- Club Performance Table -->
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
                  <td class="text-center">{{ stats.clubStats[club]?.avgCarry || 0 }}</td>
                  <td class="text-center">{{ stats.clubStats[club]?.avgTotal || 0 }}</td>
                  <td class="text-center">{{ stats.clubStats[club]?.avgBallSpeed || 0 }}</td>
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
                <tr v-for="shot in filteredShots" :key="shot.id">
                  <td>{{ shot.shotNumber }}</td>
                  <td>{{ shot.club }}</td>
                  <td class="text-center">{{ shot.carryDistance }}</td>
                  <td class="text-center">{{ shot.totalDistance }}</td>
                  <td class="text-center">{{ shot.ballSpeed }}</td>
                  <td class="text-center">{{ shot.clubHeadSpeed }}</td>
                  <td class="text-center">{{ shot.launchAngle }}</td>
                  <td class="text-center">{{ formatNumber(shot.spinRate) }}</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue';
import { useRoute } from 'vue-router';
import axios from 'axios';
import TopDownView from './TopDownView.vue';
import SideView from './SideView.vue';
import SimplerChart from './SimplerChart.vue';
import ClubStatsChart from './ClubStatsChart.vue';

const route = useRoute();
const sessionId = computed(() => route.params.id);

const session = ref(null);
const shots = ref([]);
const stats = ref(null);
const loading = ref(true);
const error = ref(null);
const clubFilter = ref('');
const selectedMetric = ref('avgCarry');

// Compute filtered shots based on club filter
const filteredShots = computed(() => {
  if (!clubFilter.value) return shots.value;
  return shots.value.filter(shot => shot.club === clubFilter.value);
});

const fetchSessionData = async () => {
  loading.value = true;
  error.value = null;
  
  try {
    console.log(`Fetching data for session ID: ${sessionId.value}`);
    
    // Fetch session details
    const sessionResponse = await axios.get(`http://localhost:8080/api/sessions/${sessionId.value}`);
    session.value = sessionResponse.data;
    console.log("Session data loaded:", session.value);
    
    // Fetch session shots
    const shotsResponse = await axios.get(`http://localhost:8080/api/sessions/${sessionId.value}/shots`);
    
    if (shotsResponse.data && Array.isArray(shotsResponse.data)) {
      shots.value = shotsResponse.data;
      console.log(`Shot data loaded: ${shots.value.length} shots`);
      
      // Log some sample shot data to help debug visualization issues
      if (shots.value.length > 0) {
        console.log("Sample shot data (first shot):", shots.value[0]);
        
        // Check for missing critical fields
        const missingFields = [];
        
        // Count shots with key visualization fields
        const shotsWithCarryDistance = shots.value.filter(s => s.carryDistance !== undefined && s.carryDistance !== null).length;
        const shotsWithTotalDistance = shots.value.filter(s => s.totalDistance !== undefined && s.totalDistance !== null).length;
        const shotsWithLateralDistance = shots.value.filter(s => s.totalLateralDistance !== undefined && s.totalLateralDistance !== null).length;
        const shotsWithApex = shots.value.filter(s => s.apex !== undefined && s.apex !== null).length;
        
        console.log(`Fields coverage: carryDistance: ${shotsWithCarryDistance}/${shots.value.length}, ` +
                    `totalDistance: ${shotsWithTotalDistance}/${shots.value.length}, ` +
                    `lateralDistance: ${shotsWithLateralDistance}/${shots.value.length}, ` +
                    `apex: ${shotsWithApex}/${shots.value.length}`);
      }
    } else {
      console.warn("No shot data received or invalid data format");
      shots.value = [];
    }
    
    // Fetch session stats
    const statsResponse = await axios.get(`http://localhost:8080/api/sessions/${sessionId.value}/stats`);
    stats.value = statsResponse.data;
    console.log("Stats data loaded:", stats.value);
    
  } catch (err) {
    console.error('Error fetching session data:', err);
    error.value = 'Failed to load session data. Please try again later.';
  } finally {
    loading.value = false;
  }
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
    console.log(`Session ID changed to: ${newId}`);
    fetchSessionData();
  }
}, { immediate: true });

// When the shots are loaded, log info to help with debugging
watch(() => shots.value, (newShots) => {
  if (newShots && newShots.length > 0) {
    console.log(`Shots data updated: ${newShots.length} shots available for visualization`);
  }
});

onMounted(() => {
  console.log("SessionView component mounted");
  fetchSessionData();
});
</script>

<style scoped>
.session-view-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}
</style>