import { createRouter, createWebHistory } from 'vue-router'
import Home from '../components/Home.vue'
import UploadCSV from '../components/UploadCSV.vue'
import SessionView from '../components/SessionView.vue'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home
  },
  {
    path: '/upload',
    name: 'UploadCSV',
    component: UploadCSV
  },
  {
    path: '/sessions/:id',
    name: 'SessionView',
    component: SessionView,
    props: true
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router