import { createStore } from 'vuex'
import axios from 'axios'

// Configure axios base URL
const API_BASE_URL = process.env.VUE_APP_API_BASE_URL || 'http://localhost:8080/api'
axios.defaults.baseURL = API_BASE_URL

export default createStore({
  state: {
    loading: false,
    customers: [],
    currentCustomer: null,
    auditLogs: [],
    pagination: {
      page: 0,
      size: 10,
      totalElements: 0,
      totalPages: 0
    },
    searchParams: {
      firstName: '',
      lastName: '',
      email: '',
      phone: ''
    },
    error: null,
    success: null
  },
  
  mutations: {
    SET_LOADING(state, loading) {
      state.loading = loading
    },
    
    SET_CUSTOMERS(state, data) {
      state.customers = data.content || []
      state.pagination = {
        page: data.number || 0,
        size: data.size || 10,
        totalElements: data.totalElements || 0,
        totalPages: data.totalPages || 0
      }
    },
    
    SET_CURRENT_CUSTOMER(state, customer) {
      state.currentCustomer = customer
    },
    
    SET_AUDIT_LOGS(state, data) {
      state.auditLogs = data.content || []
    },
    
    SET_SEARCH_PARAMS(state, params) {
      state.searchParams = { ...state.searchParams, ...params }
    },
    
    SET_ERROR(state, error) {
      state.error = error
      state.success = null
    },
    
    SET_SUCCESS(state, message) {
      state.success = message
      state.error = null
    },
    
    CLEAR_MESSAGES(state) {
      state.error = null
      state.success = null
    },
    
    ADD_CUSTOMER(state, customer) {
      state.customers.unshift(customer)
    },
    
    UPDATE_CUSTOMER(state, updatedCustomer) {
      const index = state.customers.findIndex(c => c.id === updatedCustomer.id)
      if (index !== -1) {
        state.customers.splice(index, 1, updatedCustomer)
      }
    },
    
    REMOVE_CUSTOMER(state, customerId) {
      state.customers = state.customers.filter(c => c.id !== customerId)
    }
  },
  
  actions: {
    async fetchCustomers({ commit }, { page = 0, size = 10, sortBy = 'id', sortDir = 'asc' } = {}) {
      commit('SET_LOADING', true)
      try {
        const response = await axios.get('/customers', {
          params: { page, size, sortBy, sortDir }
        })
        commit('SET_CUSTOMERS', response.data)
        commit('CLEAR_MESSAGES')
      } catch (error) {
        commit('SET_ERROR', 'Failed to fetch customers: ' + (error.response?.data?.message || error.message))
      } finally {
        commit('SET_LOADING', false)
      }
    },
    
    async searchCustomers({ commit, state }, { page = 0, size = 10 } = {}) {
      commit('SET_LOADING', true)
      try {
        const params = { page, size }
        
        // Add non-empty search parameters
        if (state.searchParams.firstName) params.firstName = state.searchParams.firstName
        if (state.searchParams.lastName) params.lastName = state.searchParams.lastName
        if (state.searchParams.email) params.email = state.searchParams.email
        if (state.searchParams.phone) params.phone = state.searchParams.phone
        
        const response = await axios.get('/customers/search', { params })
        commit('SET_CUSTOMERS', response.data)
        commit('CLEAR_MESSAGES')
      } catch (error) {
        commit('SET_ERROR', 'Failed to search customers: ' + (error.response?.data?.message || error.message))
      } finally {
        commit('SET_LOADING', false)
      }
    },
    
    async fetchCustomer({ commit }, id) {
      commit('SET_LOADING', true)
      try {
        const response = await axios.get(`/customers/${id}`)
        commit('SET_CURRENT_CUSTOMER', response.data)
        commit('CLEAR_MESSAGES')
        return response.data
      } catch (error) {
        commit('SET_ERROR', 'Failed to fetch customer: ' + (error.response?.data?.message || error.message))
        throw error
      } finally {
        commit('SET_LOADING', false)
      }
    },
    
    async createCustomer({ commit }, customerData) {
      commit('SET_LOADING', true)
      try {
        const response = await axios.post('/customers', customerData)
        commit('ADD_CUSTOMER', response.data)
        commit('SET_SUCCESS', 'Customer created successfully')
        return response.data
      } catch (error) {
        commit('SET_ERROR', 'Failed to create customer: ' + (error.response?.data?.message || error.message))
        throw error
      } finally {
        commit('SET_LOADING', false)
      }
    },
    
    async updateCustomer({ commit }, { id, customerData }) {
      commit('SET_LOADING', true)
      try {
        const response = await axios.put(`/customers/${id}`, customerData)
        commit('UPDATE_CUSTOMER', response.data)
        commit('SET_CURRENT_CUSTOMER', response.data)
        commit('SET_SUCCESS', 'Customer updated successfully')
        return response.data
      } catch (error) {
        commit('SET_ERROR', 'Failed to update customer: ' + (error.response?.data?.message || error.message))
        throw error
      } finally {
        commit('SET_LOADING', false)
      }
    },
    
    async deleteCustomer({ commit }, id) {
      commit('SET_LOADING', true)
      try {
        await axios.delete(`/customers/${id}`)
        commit('REMOVE_CUSTOMER', id)
        commit('SET_SUCCESS', 'Customer deleted successfully')
      } catch (error) {
        commit('SET_ERROR', 'Failed to delete customer: ' + (error.response?.data?.message || error.message))
        throw error
      } finally {
        commit('SET_LOADING', false)
      }
    },
    
    async fetchCustomerAudit({ commit }, { customerId, page = 0, size = 10 }) {
      commit('SET_LOADING', true)
      try {
        const response = await axios.get(`/customers/${customerId}/audit`, {
          params: { page, size }
        })
        commit('SET_AUDIT_LOGS', response.data)
        commit('CLEAR_MESSAGES')
      } catch (error) {
        commit('SET_ERROR', 'Failed to fetch audit history: ' + (error.response?.data?.message || error.message))
      } finally {
        commit('SET_LOADING', false)
      }
    },
    
    async fetchRecentAudit({ commit }, { page = 0, size = 20 } = {}) {
      commit('SET_LOADING', true)
      try {
        const response = await axios.get('/customers/audit/recent', {
          params: { page, size }
        })
        commit('SET_AUDIT_LOGS', response.data)
        commit('CLEAR_MESSAGES')
      } catch (error) {
        commit('SET_ERROR', 'Failed to fetch recent changes: ' + (error.response?.data?.message || error.message))
      } finally {
        commit('SET_LOADING', false)
      }
    },
    
    updateSearchParams({ commit }, params) {
      commit('SET_SEARCH_PARAMS', params)
    },
    
    clearMessages({ commit }) {
      commit('CLEAR_MESSAGES')
    }
  },
  
  getters: {
    isLoading: state => state.loading,
    customers: state => state.customers,
    currentCustomer: state => state.currentCustomer,
    auditLogs: state => state.auditLogs,
    pagination: state => state.pagination,
    searchParams: state => state.searchParams,
    error: state => state.error,
    success: state => state.success,
    hasSearchParams: state => {
      return !!(state.searchParams.firstName || 
                state.searchParams.lastName || 
                state.searchParams.email || 
                state.searchParams.phone)
    }
  }
})
