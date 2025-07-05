<template>
  <div class="upload-container">
    <div class="card">
      <div class="card-header">
        <h2 class="h5 mb-0">Upload Golf Data</h2>
      </div>
      <div class="card-body">
        <div v-if="successMessage" class="alert alert-success" role="alert">
          {{ successMessage }}
          <div class="mt-3">
            <router-link :to="'/sessions/' + uploadedSessionId" class="btn btn-primary">
              View Session
            </router-link>
            <button class="btn btn-outline-primary ms-2" @click="resetForm">
              Upload Another
            </button>
          </div>
        </div>
        
        <div v-else>
          <p class="card-text mb-4">
            Upload your Garmin R10 CSV file to analyze your golf shots. 
            The data will be stored in the database for future reference.
          </p>
          
          <form @submit.prevent="handleSubmit">
            <!-- Title field -->
            <div class="mb-3">
              <label for="sessionTitle" class="form-label">Session Title *</label>
              <input
                type="text"
                class="form-control"
                id="sessionTitle"
                v-model="formData.title"
                :class="{'is-invalid': titleError}"
                placeholder="Enter a title for this session"
                required
              >
              <div v-if="titleError" class="invalid-feedback">
                {{ titleError }}
              </div>
            </div>
            
            <!-- Location field -->
            <div class="mb-3">
              <label for="sessionLocation" class="form-label">Location</label>
              <input
                type="text"
                class="form-control"
                id="sessionLocation"
                v-model="formData.location"
                placeholder="Where did you hit these shots? (optional)"
              >
            </div>
            
            <!-- Data source selection -->
            <div class="mb-3">
              <label for="dataSource" class="form-label">Data Source *</label>
              <select
                class="form-select"
                id="dataSource"
                v-model="formData.source"
              >
                <option value="GARMIN_R10">Garmin R10</option>
                <option value="AWESOME_GOLF">Awesome Golf</option>
              </select>
              <div class="form-text">
                Select the launch monitor that generated the CSV file.
              </div>
            </div>
            
            <!-- File upload -->
            <div class="mb-3">
              <label for="csvFile" class="form-label">CSV File *</label>
              <div class="input-group">
                <input
                  type="file"
                  class="form-control"
                  id="csvFile"
                  @change="handleFileChange"
                  :class="{'is-invalid': fileError}"
                  accept=".csv"
                  required
                >
                <label class="input-group-text" for="csvFile">
                  <i class="bi bi-upload"></i>
                </label>
                <div v-if="fileError" class="invalid-feedback">
                  {{ fileError }}
                </div>
              </div>
              <div class="form-text">
                Only .csv files from Garmin R10 and Awesome Golf are supported.
              </div>
            </div>
            
            <div class="d-flex justify-content-between">
              <router-link to="/" class="btn btn-outline-secondary">Cancel</router-link>
              <button
                type="submit"
                class="btn btn-primary"
                :disabled="isUploading"
              >
                <span v-if="isUploading" class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
                {{ isUploading ? 'Uploading...' : 'Upload & Analyze' }}
              </button>
            </div>
          </form>
          
          <!-- General error message -->
          <div v-if="error" class="alert alert-danger mt-4" role="alert">
            {{ error }}
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import axios from 'axios';

const formData = ref({
  title: '',
  location: '',
  file: null,
  source: 'GARMIN_R10'  // Default to Garmin R10
});

const titleError = ref('');
const fileError = ref('');
const error = ref('');
const isUploading = ref(false);
const successMessage = ref('');
const uploadedSessionId = ref(null);

const handleFileChange = (event) => {
  const file = event.target.files[0];
  if (!file) {
    formData.value.file = null;
    return;
  }
  
  // Validate file type
  if (file.type !== 'text/csv' && !file.name.toLowerCase().endsWith('.csv')) {
    fileError.value = 'Please select a valid CSV file.';
    formData.value.file = null;
    return;
  }
  
  // Validate file size (limit to 10MB)
  if (file.size > 10 * 1024 * 1024) {
    fileError.value = 'File is too large. Maximum file size is 10MB.';
    formData.value.file = null;
    return;
  }
  
  fileError.value = '';
  formData.value.file = file;
};

const validateForm = () => {
  let isValid = true;
  
  // Validate title
  if (!formData.value.title.trim()) {
    titleError.value = 'Please enter a session title.';
    isValid = false;
  } else if (formData.value.title.trim().length < 3) {
    titleError.value = 'Title must be at least 3 characters.';
    isValid = false;
  } else if (formData.value.title.trim().length > 100) {
    titleError.value = 'Title must be less than 100 characters.';
    isValid = false;
  } else {
    titleError.value = '';
  }
  
  // Validate file
  if (!formData.value.file) {
    fileError.value = 'Please select a CSV file.';
    isValid = false;
  } else {
    fileError.value = '';
  }
  
  return isValid;
};

const handleSubmit = async () => {
  if (!validateForm()) {
    return;
  }
  
  isUploading.value = true;
  error.value = '';
  successMessage.value = '';
  uploadedSessionId.value = null;
  
  const form = new FormData();
  form.append('title', formData.value.title.trim());
  form.append('location', formData.value.location.trim());
  form.append('file', formData.value.file);
  form.append('source', formData.value.source);
  
  try {
    const response = await axios.post('http://localhost:8080/api/sessions/upload', form, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    });
    
    if (response.data && response.data.id) {
      uploadedSessionId.value = response.data.id;
      successMessage.value = 'File uploaded and analyzed successfully!';
    } else {
      error.value = 'Unexpected response from server. Please try again.';
    }
  } catch (err) {
    console.error('Error uploading CSV:', err);
    
    if (err.response && err.response.data && err.response.data.error) {
      error.value = err.response.data.error;
    } else {
      error.value = 'Failed to upload and analyze the file. Please try again.';
    }
  } finally {
    isUploading.value = false;
  }
};

const resetForm = () => {
  formData.value = {
    title: '',
    location: '',
    file: null
  };
  titleError.value = '';
  fileError.value = '';
  error.value = '';
  successMessage.value = '';
  uploadedSessionId.value = null;
  
  // Reset file input value
  const fileInput = document.getElementById('csvFile');
  if (fileInput) {
    fileInput.value = '';
  }
};
</script>

<style scoped>
.upload-container {
  max-width: 700px;
  margin: 0 auto;
  padding: 20px;
}
</style>