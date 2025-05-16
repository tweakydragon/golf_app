<template>
  <div class="home-container">
    <div class="header mb-4">
      <h1 class="display-5 text-center">Golf Shot Analysis</h1>
      <p class="text-muted text-center">View your golf shot data or upload new sessions</p>
    </div>

    <!-- Recent sessions section -->
    <div class="card mb-4">
      <div class="card-header d-flex justify-content-between align-items-center">
        <h2 class="h5 mb-0">Recent Sessions</h2>
        <router-link to="/upload" class="btn btn-primary btn-sm">Upload New</router-link>
      </div>
      
      <div class="card-body">
        <!-- Loading state -->
        <div v-if="loading" class="text-center p-5">
          <div class="spinner-border text-primary" role="status">
            <span class="visually-hidden">Loading...</span>
          </div>
          <p class="mt-3">Loading sessions...</p>
        </div>
        
        <!-- Error state -->
        <div v-else-if="error" class="alert alert-danger">
          {{ error }}
        </div>
        
        <!-- Empty state -->
        <div v-else-if="sessions.length === 0" class="text-center p-5">
          <div class="mb-4">
            <i class="bi bi-file-earmark-excel" style="font-size: 3rem;"></i>
          </div>
          <h3>No Sessions Found</h3>
          <p>Upload your first Garmin R10 CSV file to get started.</p>
          <router-link to="/upload" class="btn btn-primary">Upload CSV</router-link>
        </div>
        
        <!-- Session listing -->
        <div v-else>
          <!-- Search bar -->
          <div class="mb-4">
            <div class="input-group">
              <span class="input-group-text"><i class="bi bi-search"></i></span>
              <input
                type="text"
                class="form-control"
                placeholder="Search sessions by title..."
                v-model="searchQuery"
                @input="searchSessions"
              >
            </div>
          </div>
          
          <div class="list-group">
            <router-link
              v-for="session in sessions"
              :key="session.id"
              :to="'/sessions/' + session.id"
              class="list-group-item list-group-item-action"
            >
              <div class="d-flex w-100 justify-content-between">
                <h5 class="mb-1">{{ session.title }}</h5>
                <small>{{ formatDate(session.uploadDate) }}</small>
              </div>
              <p class="mb-1 text-muted">{{ session.location || 'No location specified' }}</p>
              <small>{{ session.shots?.length || 0 }} shots</small>
            </router-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';

const sessions = ref([]);
const loading = ref(true);
const error = ref(null);
const searchQuery = ref('');
let searchTimeout = null;

const fetchSessions = async () => {
  loading.value = true;
  error.value = null;
  
  try {
    const response = await axios.get('http://localhost:8080/api/sessions');
    sessions.value = response.data;
  } catch (err) {
    console.error('Error fetching sessions:', err);
    error.value = 'Failed to load sessions. Please try again later.';
  } finally {
    loading.value = false;
  }
};

const searchSessions = () => {
  // Debounce the search to avoid too many API calls
  if (searchTimeout) clearTimeout(searchTimeout);
  
  searchTimeout = setTimeout(async () => {
    loading.value = true;
    error.value = null;
    
    try {
      let endpoint = 'http://localhost:8080/api/sessions';
      
      // If search term exists, use the search endpoint
      if (searchQuery.value.trim()) {
        endpoint = `http://localhost:8080/api/sessions/search?title=${encodeURIComponent(searchQuery.value.trim())}`;
      }
      
      const response = await axios.get(endpoint);
      sessions.value = response.data;
    } catch (err) {
      console.error('Error searching sessions:', err);
      error.value = 'Failed to search sessions. Please try again later.';
    } finally {
      loading.value = false;
    }
  }, 300); // 300ms debounce
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

onMounted(() => {
  fetchSessions();
});
</script>

<style scoped>
.home-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}
</style>