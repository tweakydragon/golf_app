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

const route = useRoute();
const sessionId = computed(() => route.params.id);

const session = ref(null);
const shots = ref([]);
const stats = ref(null);
const loading = ref(true);
const error = ref(null);
const clubFilter = ref('');

// Compute filtered shots based on club filter
const filteredShots = computed(() => {
  if (!clubFilter.value) return shots.value;
  return shots.value.filter(shot => shot.club === clubFilter.value);
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
</style>