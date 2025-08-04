import { createRouter, createWebHistory } from 'vue-router'
import CustomerList from '../views/CustomerList.vue'
import CustomerDetail from '../views/CustomerDetail.vue'
import AuditLog from '../views/AuditLog.vue'

const routes = [
  {
    path: '/',
    name: 'CustomerList',
    component: CustomerList
  },
  {
    path: '/customer/:id',
    name: 'CustomerDetail',
    component: CustomerDetail,
    props: true
  },
  {
    path: '/customer/:id/edit',
    name: 'CustomerEdit',
    component: CustomerDetail,
    props: route => ({ id: route.params.id, editMode: true })
  },
  {
    path: '/customer/new',
    name: 'CustomerNew',
    component: CustomerDetail,
    props: { editMode: true, isNew: true }
  },
  {
    path: '/audit',
    name: 'AuditLog',
    component: AuditLog
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

export default router
