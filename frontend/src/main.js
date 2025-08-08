import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
import router from './router'
import 'bootstrap/dist/css/bootstrap.min.css'
import * as bootstrap from 'bootstrap/dist/js/bootstrap.bundle.min.js'
import 'bootstrap-icons/font/bootstrap-icons.css'
import axios from 'axios'

// Make Bootstrap available globally
window.bootstrap = bootstrap

// Set axios default base URL to backend
axios.defaults.baseURL = 'http://localhost:8080'

// Create and mount app with router
const app = createApp(App)
app.use(router)
app.mount('#app')
