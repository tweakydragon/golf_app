<template>
  <div class="customer-list">
    <!-- Page Header -->
    <div class="row mb-4">
      <div class="col">
        <h1 class="h2 mb-3">Customer Management</h1>
        
        <!-- Search and Actions Bar -->
        <div class="card">
          <div class="card-header">
            <div class="row align-items-center">
              <div class="col">
                <h5 class="card-title mb-0">Search & Filter</h5>
              </div>
              <div class="col-auto">
                <router-link to="/customer/new" class="btn btn-primary">
                  <i class="bi bi-plus-circle"></i> Add Customer
                </router-link>
              </div>
            </div>
          </div>
          <div class="card-body">
            <!-- Search Form -->
            <form @submit.prevent="handleSearch" class="row g-3">
              <div class="col-md-3">
                <label for="firstName" class="form-label">First Name</label>
                <input 
                  type="text" 
                  class="form-control" 
                  id="firstName"
                  v-model="searchForm.firstName"
                  placeholder="Enter first name">
              </div>
              <div class="col-md-3">
                <label for="lastName" class="form-label">Last Name</label>
                <input 
                  type="text" 
                  class="form-control" 
                  id="lastName"
                  v-model="searchForm.lastName"
                  placeholder="Enter last name">
              </div>
              <div class="col-md-3">
                <label for="email" class="form-label">Email</label>
                <input 
                  type="email" 
                  class="form-control" 
                  id="email"
                  v-model="searchForm.email"
                  placeholder="Enter email">
              </div>
              <div class="col-md-3">
                <label for="phone" class="form-label">Phone</label>
                <input 
                  type="text" 
                  class="form-control" 
                  id="phone"
                  v-model="searchForm.phone"
                  placeholder="Enter phone">
              </div>
              <div class="col-12">
                <div class="btn-group" role="group">
                  <button type="submit" class="btn btn-outline-primary">
                    <i class="bi bi-search"></i> Search
                  </button>
                  <button type="button" class="btn btn-outline-secondary" @click="clearSearch">
                    <i class="bi bi-x-circle"></i> Clear
                  </button>
                  <button type="button" class="btn btn-outline-info" @click="loadAllCustomers">
                    <i class="bi bi-arrow-clockwise"></i> Show All
                  </button>
                </div>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>

    <!-- Alert Messages -->
    <div v-if="error" class="alert alert-danger alert-dismissible fade show" role="alert">
      {{ error }}
      <button type="button" class="btn-close" @click="$store.dispatch('clearMessages')"></button>
    </div>
    
    <div v-if="success" class="alert alert-success alert-dismissible fade show" role="alert">
      {{ success }}
      <button type="button" class="btn-close" @click="$store.dispatch('clearMessages')"></button>
    </div>

    <!-- Results Info -->
    <div class="row mb-3" v-if="customers.length > 0 || hasSearchParams">
      <div class="col">
        <div class="d-flex justify-content-between align-items-center">
          <span class="text-muted">
            Showing {{ customers.length }} of {{ pagination.totalElements }} customers
            <span v-if="hasSearchParams" class="badge bg-info ms-2">Filtered</span>
          </span>
          <div class="btn-group btn-group-sm">
            <button 
              class="btn btn-outline-secondary"
              :class="{ active: pageSize === 10 }"
              @click="changePageSize(10)">10</button>
            <button 
              class="btn btn-outline-secondary"
              :class="{ active: pageSize === 25 }"
              @click="changePageSize(25)">25</button>
            <button 
              class="btn btn-outline-secondary"
              :class="{ active: pageSize === 50 }"
              @click="changePageSize(50)">50</button>
          </div>
        </div>
      </div>
    </div>

    <!-- Customer Table -->
    <div class="card">
      <div class="card-body p-0">
        <div class="table-responsive">
          <table class="table table-striped table-hover mb-0">
            <thead>
              <tr>
                <th scope="col">Name</th>
                <th scope="col">Email</th>
                <th scope="col">Phone</th>
                <th scope="col">Address</th>
                <th scope="col">Last Updated</th>
                <th scope="col" width="150">Actions</th>
              </tr>
            </thead>
            <tbody>
              <tr v-if="customers.length === 0 && !$store.getters.isLoading">
                <td colspan="6" class="text-center py-4 text-muted">
                  <div>
                    <i class="bi bi-inbox fs-1"></i>
                    <p class="mt-2 mb-0">No customers found</p>
                    <p class="small">Try adjusting your search criteria or add a new customer</p>
                  </div>
                </td>
              </tr>
              <tr v-for="customer in customers" :key="customer.id">
                <td>
                  <router-link 
                    :to="`/customer/${customer.id}`" 
                    class="text-decoration-none fw-medium">
                    {{ customer.firstName }} {{ customer.lastName }}
                  </router-link>
                </td>
                <td>
                  <a v-if="customer.email" :href="`mailto:${customer.email}`" class="text-decoration-none">
                    {{ customer.email }}
                  </a>
                  <span v-else class="text-muted">—</span>
                </td>
                <td>
                  <a v-if="customer.phone" :href="`tel:${customer.phone}`" class="text-decoration-none">
                    {{ customer.phone }}
                  </a>
                  <span v-else class="text-muted">—</span>
                </td>
                <td>
                  <span v-if="customer.address" class="text-truncate d-inline-block" style="max-width: 200px;">
                    {{ customer.address }}
                  </span>
                  <span v-else class="text-muted">—</span>
                </td>
                <td>
                  <small class="text-muted">{{ formatDate(customer.updatedAt) }}</small>
                </td>
                <td>
                  <div class="btn-group btn-group-sm" role="group">
                    <router-link 
                      :to="`/customer/${customer.id}`" 
                      class="btn btn-outline-primary"
                      title="View Details">
                      <i class="bi bi-eye"></i>
                    </router-link>
                    <router-link 
                      :to="`/customer/${customer.id}/edit`" 
                      class="btn btn-outline-secondary"
                      title="Edit">
                      <i class="bi bi-pencil"></i>
                    </router-link>
                    <button 
                      class="btn btn-outline-danger"
                      title="Delete"
                      @click="confirmDelete(customer)">
                      <i class="bi bi-trash"></i>
                    </button>
                  </div>
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

    <!-- Delete Confirmation Modal -->
    <div 
      class="modal fade" 
      id="deleteModal" 
      tabindex="-1" 
      ref="deleteModal">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Confirm Delete</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
          </div>
          <div class="modal-body">
            <p>Are you sure you want to delete <strong>{{ customerToDelete?.firstName }} {{ customerToDelete?.lastName }}</strong>?</p>
            <p class="text-muted small">This action cannot be undone.</p>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
            <button type="button" class="btn btn-danger" @click="deleteCustomer">Delete</button>
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
  name: 'CustomerList',
  
  data() {
    return {
      searchForm: {
        firstName: '',
        lastName: '',
        email: '',
        phone: ''
      },
      pageSize: 10,
      customerToDelete: null
    }
  },
  
  computed: {
    ...mapGetters(['customers', 'pagination', 'hasSearchParams', 'error', 'success']),
    
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
    this.loadAllCustomers()
  },
  
  methods: {
    async loadAllCustomers() {
      await this.$store.dispatch('fetchCustomers', { 
        page: 0, 
        size: this.pageSize 
      })
    },
    
    async handleSearch() {
      // Update store search params
      this.$store.dispatch('updateSearchParams', this.searchForm)
      
      // Perform search
      await this.$store.dispatch('searchCustomers', { 
        page: 0, 
        size: this.pageSize 
      })
    },
    
    clearSearch() {
      this.searchForm = {
        firstName: '',
        lastName: '',
        email: '',
        phone: ''
      }
      this.$store.dispatch('updateSearchParams', this.searchForm)
      this.loadAllCustomers()
    },
    
    async changePage(page) {
      if (this.hasSearchParams) {
        await this.$store.dispatch('searchCustomers', { 
          page, 
          size: this.pageSize 
        })
      } else {
        await this.$store.dispatch('fetchCustomers', { 
          page, 
          size: this.pageSize 
        })
      }
    },
    
    async changePageSize(size) {
      this.pageSize = size
      if (this.hasSearchParams) {
        await this.$store.dispatch('searchCustomers', { 
          page: 0, 
          size 
        })
      } else {
        await this.$store.dispatch('fetchCustomers', { 
          page: 0, 
          size 
        })
      }
    },
    
    confirmDelete(customer) {
      this.customerToDelete = customer
      const modal = new window.bootstrap.Modal(this.$refs.deleteModal)
      modal.show()
    },
    
    async deleteCustomer() {
      try {
        await this.$store.dispatch('deleteCustomer', this.customerToDelete.id)
        
        // Hide modal
        const modal = window.bootstrap.Modal.getInstance(this.$refs.deleteModal)
        modal.hide()
        
        // Reload current page
        await this.changePage(this.pagination.page)
        
      } catch (error) {
        console.error('Failed to delete customer:', error)
      }
    },
    
    formatDate(dateString) {
      if (!dateString) return '—'
      return moment(dateString).format('MMM D, YYYY [at] h:mm A')
    }
  }
}
</script>

<style scoped>
.text-truncate {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.btn-group-sm > .btn {
  padding: 0.25rem 0.5rem;
  font-size: 0.875rem;
}

.table th {
  background-color: #f8f9fa;
  border-bottom: 2px solid #dee2e6;
  font-weight: 600;
}

.card {
  box-shadow: 0 0.125rem 0.25rem rgba(0, 0, 0, 0.075);
}

@media (max-width: 768px) {
  .table-responsive {
    font-size: 0.875rem;
  }
  
  .btn-group {
    flex-direction: column;
  }
  
  .btn-group > .btn {
    margin-bottom: 0.25rem;
  }
}
</style>
