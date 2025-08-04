<template>
  <div class="audit-log">
    <!-- Page Header -->
    <div class="row mb-4">
      <div class="col">
        <h1 class="h2 mb-3">Audit Log</h1>
        <p class="text-muted">Track all changes made to customer records</p>
      </div>
    </div>

    <!-- Alert Messages -->
    <div v-if="error" class="alert alert-danger alert-dismissible fade show" role="alert">
      {{ error }}
      <button type="button" class="btn-close" @click="$store.dispatch('clearMessages')"></button>
    </div>

    <!-- Filters -->
    <div class="card mb-4">
      <div class="card-header">
        <h5 class="card-title mb-0">Filters</h5>
      </div>
      <div class="card-body">
        <div class="row g-3">
          <div class="col-md-3">
            <label class="form-label">Action Type</label>
            <select class="form-select" v-model="filters.action" @change="applyFilters">
              <option value="">All Actions</option>
              <option value="INSERT">Insert</option>
              <option value="UPDATE">Update</option>
              <option value="DELETE">Delete</option>
            </select>
          </div>
          <div class="col-md-3">
            <label class="form-label">Page Size</label>
            <select class="form-select" v-model="pageSize" @change="changePageSize">
              <option value="10">10 records</option>
              <option value="25">25 records</option>
              <option value="50">50 records</option>
              <option value="100">100 records</option>
            </select>
          </div>
          <div class="col-md-6">
            <label class="form-label">&nbsp;</label>
            <div class="d-flex gap-2">
              <button class="btn btn-outline-primary" @click="loadRecentChanges">
                <i class="bi bi-arrow-clockwise"></i> Refresh
              </button>
              <router-link to="/" class="btn btn-outline-secondary">
                <i class="bi bi-arrow-left"></i> Back to Customers
              </router-link>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Summary Cards -->
    <div class="row mb-4">
      <div class="col-md-4">
        <div class="card text-bg-success">
          <div class="card-body">
            <div class="d-flex justify-content-between">
              <div>
                <h6 class="card-title">Total Inserts</h6>
                <h3 class="mb-0">{{ auditStats.inserts }}</h3>
              </div>
              <div class="align-self-center">
                <i class="bi bi-plus-circle fs-1"></i>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="col-md-4">
        <div class="card text-bg-warning">
          <div class="card-body">
            <div class="d-flex justify-content-between">
              <div>
                <h6 class="card-title">Total Updates</h6>
                <h3 class="mb-0">{{ auditStats.updates }}</h3>
              </div>
              <div class="align-self-center">
                <i class="bi bi-pencil-square fs-1"></i>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="col-md-4">
        <div class="card text-bg-danger">
          <div class="card-body">
            <div class="d-flex justify-content-between">
              <div>
                <h6 class="card-title">Total Deletes</h6>
                <h3 class="mb-0">{{ auditStats.deletes }}</h3>
              </div>
              <div class="align-self-center">
                <i class="bi bi-trash fs-1"></i>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Audit Log Table -->
    <div class="card">
      <div class="card-header">
        <div class="d-flex justify-content-between align-items-center">
          <h5 class="card-title mb-0">Change History</h5>
          <span class="badge bg-secondary">{{ auditLogs.length }} records</span>
        </div>
      </div>
      <div class="card-body p-0">
        <div class="table-responsive">
          <table class="table table-striped table-hover mb-0">
            <thead>
              <tr>
                <th scope="col" width="120">Date & Time</th>
                <th scope="col" width="100">Action</th>
                <th scope="col" width="120">Customer ID</th>
                <th scope="col">Changes</th>
                <th scope="col" width="100">Changed By</th>
                <th scope="col" width="100">Actions</th>
              </tr>
            </thead>
            <tbody>
              <tr v-if="auditLogs.length === 0 && !$store.getters.isLoading">
                <td colspan="6" class="text-center py-4 text-muted">
                  <div>
                    <i class="bi bi-clock-history fs-1"></i>
                    <p class="mt-2 mb-0">No audit records found</p>
                    <p class="small">Changes will appear here as they are made</p>
                  </div>
                </td>
              </tr>
              <tr v-for="audit in auditLogs" :key="audit.id">
                <td>
                  <small class="text-muted">{{ formatDate(audit.changedAt) }}</small>
                </td>
                <td>
                  <span class="badge" :class="getActionBadgeClass(audit.action)">
                    {{ audit.action }}
                  </span>
                </td>
                <td>
                  <router-link 
                    :to="`/customer/${audit.customerId}`"
                    class="text-decoration-none fw-medium">
                    #{{ audit.customerId }}
                  </router-link>
                </td>
                <td>
                  <div class="change-summary">
                    <div v-if="audit.action === 'INSERT'">
                      <span class="text-success">
                        <i class="bi bi-plus-circle"></i>
                        New customer created: {{ getCustomerName(audit.newValues) }}
                      </span>
                    </div>
                    <div v-else-if="audit.action === 'DELETE'">
                      <span class="text-danger">
                        <i class="bi bi-trash"></i>
                        Customer deleted: {{ getCustomerName(audit.oldValues) }}
                      </span>
                    </div>
                    <div v-else-if="audit.action === 'UPDATE'">
                      <span class="text-warning">
                        <i class="bi bi-pencil"></i>
                        Updated: {{ getChangedFields(audit.oldValues, audit.newValues) }}
                      </span>
                    </div>
                  </div>
                </td>
                <td>
                  <span class="badge bg-light text-dark">{{ audit.changedBy }}</span>
                </td>
                <td>
                  <button 
                    class="btn btn-outline-info btn-sm"
                    @click="showAuditDetails(audit)"
                    title="View Details">
                    <i class="bi bi-eye"></i>
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <!-- Pagination -->
    <nav v-if="pagination.totalPages > 1" class="mt-4">
      <ul class="pagination">
        <li class="page-item" :class="{ disabled: pagination.page === 0 }">
          <button class="page-link" @click="changePage(pagination.page - 1)" :disabled="pagination.page === 0">
            Previous
          </button>
        </li>
        
        <li 
          v-for="page in visiblePages" 
          :key="page"
          class="page-item" 
          :class="{ active: page === pagination.page }">
          <button class="page-link" @click="changePage(page)">
            {{ page + 1 }}
          </button>
        </li>
        
        <li class="page-item" :class="{ disabled: pagination.page >= pagination.totalPages - 1 }">
          <button 
            class="page-link" 
            @click="changePage(pagination.page + 1)" 
            :disabled="pagination.page >= pagination.totalPages - 1">
            Next
          </button>
        </li>
      </ul>
    </nav>

    <!-- Audit Detail Modal -->
    <div 
      class="modal fade" 
      id="auditDetailModal" 
      tabindex="-1" 
      ref="auditDetailModal">
      <div class="modal-dialog modal-lg">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Audit Record Details</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
          </div>
          <div class="modal-body" v-if="selectedAudit">
            <div class="row g-3">
              <div class="col-md-6">
                <label class="form-label fw-semibold">Customer ID</label>
                <p class="form-control-plaintext">{{ selectedAudit.customerId }}</p>
              </div>
              <div class="col-md-6">
                <label class="form-label fw-semibold">Action</label>
                <p class="form-control-plaintext">
                  <span class="badge" :class="getActionBadgeClass(selectedAudit.action)">
                    {{ selectedAudit.action }}
                  </span>
                </p>
              </div>
              <div class="col-md-6">
                <label class="form-label fw-semibold">Changed By</label>
                <p class="form-control-plaintext">{{ selectedAudit.changedBy }}</p>
              </div>
              <div class="col-md-6">
                <label class="form-label fw-semibold">Changed At</label>
                <p class="form-control-plaintext">{{ formatDate(selectedAudit.changedAt) }}</p>
              </div>
              
              <!-- Old Values -->
              <div class="col-12" v-if="selectedAudit.oldValues">
                <label class="form-label fw-semibold">Previous Values</label>
                <div class="border rounded p-3 bg-light">
                  <pre class="mb-0"><code>{{ formatJSON(selectedAudit.oldValues) }}</code></pre>
                </div>
              </div>
              
              <!-- New Values -->
              <div class="col-12" v-if="selectedAudit.newValues">
                <label class="form-label fw-semibold">New Values</label>
                <div class="border rounded p-3 bg-light">
                  <pre class="mb-0"><code>{{ formatJSON(selectedAudit.newValues) }}</code></pre>
                </div>
              </div>
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import moment from 'moment'

export default {
  name: 'AuditLog',
  
  data() {
    return {
      filters: {
        action: ''
      },
      pageSize: 20,
      selectedAudit: null,
      auditStats: {
        inserts: 0,
        updates: 0,
        deletes: 0
      }
    }
  },
  
  computed: {
    ...mapGetters(['auditLogs', 'pagination', 'error']),
    
    visiblePages() {
      const current = this.pagination.page
      const total = this.pagination.totalPages
      const maxVisible = 5
      
      let start = Math.max(0, current - Math.floor(maxVisible / 2))
      let end = Math.min(total - 1, start + maxVisible - 1)
      
      if (end - start < maxVisible - 1) {
        start = Math.max(0, end - maxVisible + 1)
      }
      
      const pages = []
      for (let i = start; i <= end; i++) {
        pages.push(i)
      }
      return pages
    }
  },
  
  mounted() {
    this.loadRecentChanges()
    this.calculateStats()
  },
  
  methods: {
    async loadRecentChanges() {
      await this.$store.dispatch('fetchRecentAudit', {
        page: 0,
        size: this.pageSize
      })
      this.calculateStats()
    },
    
    async applyFilters() {
      if (this.filters.action) {
        // Note: This would require a new API endpoint for filtering by action
        // For now, we'll just load all recent changes
        await this.loadRecentChanges()
      } else {
        await this.loadRecentChanges()
      }
    },
    
    async changePageSize() {
      await this.$store.dispatch('fetchRecentAudit', {
        page: 0,
        size: this.pageSize
      })
    },
    
    async changePage(page) {
      await this.$store.dispatch('fetchRecentAudit', {
        page,
        size: this.pageSize
      })
    },
    
    calculateStats() {
      this.auditStats = {
        inserts: this.auditLogs.filter(log => log.action === 'INSERT').length,
        updates: this.auditLogs.filter(log => log.action === 'UPDATE').length,
        deletes: this.auditLogs.filter(log => log.action === 'DELETE').length
      }
    },
    
    showAuditDetails(audit) {
      this.selectedAudit = audit
      const modal = new window.bootstrap.Modal(this.$refs.auditDetailModal)
      modal.show()
    },
    
    formatDate(dateString) {
      if (!dateString) return '—'
      return moment(dateString).format('MMM D, YYYY [at] h:mm A')
    },
    
    formatJSON(jsonString) {
      if (!jsonString) return ''
      try {
        const obj = typeof jsonString === 'string' ? JSON.parse(jsonString) : jsonString
        return JSON.stringify(obj, null, 2)
      } catch (error) {
        return jsonString
      }
    },
    
    getActionBadgeClass(action) {
      switch (action) {
        case 'INSERT': return 'bg-success'
        case 'UPDATE': return 'bg-warning text-dark'
        case 'DELETE': return 'bg-danger'
        default: return 'bg-secondary'
      }
    },
    
    getCustomerName(valuesString) {
      if (!valuesString) return 'Unknown'
      try {
        const values = typeof valuesString === 'string' ? JSON.parse(valuesString) : valuesString
        return `${values.firstName || ''} ${values.lastName || ''}`.trim() || 'Unknown'
      } catch (error) {
        return 'Unknown'
      }
    },
    
    getChangedFields(oldValuesString, newValuesString) {
      if (!oldValuesString || !newValuesString) return 'Unknown changes'
      
      try {
        const oldValues = typeof oldValuesString === 'string' ? JSON.parse(oldValuesString) : oldValuesString
        const newValues = typeof newValuesString === 'string' ? JSON.parse(newValuesString) : newValuesString
        
        const changedFields = []
        const fieldsToCheck = ['firstName', 'lastName', 'email', 'phone', 'address']
        
        fieldsToCheck.forEach(field => {
          if (oldValues[field] !== newValues[field]) {
            changedFields.push(field)
          }
        })
        
        return changedFields.length > 0 ? changedFields.join(', ') : 'No changes detected'
      } catch (error) {
        return 'Unable to parse changes'
      }
    }
  }
}
</script>

<style scoped>
.form-control-plaintext {
  padding: 0.375rem 0;
  margin-bottom: 0;
  background-color: transparent;
  border: solid transparent;
}

.change-summary {
  font-size: 0.9rem;
}

.card {
  box-shadow: 0 0.125rem 0.25rem rgba(0, 0, 0, 0.075);
}

pre {
  font-size: 0.85rem;
  max-height: 300px;
  overflow-y: auto;
}

.text-bg-success .card-title,
.text-bg-warning .card-title,
.text-bg-danger .card-title {
  color: rgba(255, 255, 255, 0.9);
}

@media (max-width: 768px) {
  .table-responsive {
    font-size: 0.875rem;
  }
  
  .modal-dialog {
    margin: 1rem;
  }
}
</style>
