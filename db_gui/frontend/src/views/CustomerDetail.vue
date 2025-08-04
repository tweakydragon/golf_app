<template>
  <div class="customer-detail">
    <!-- Breadcrumb -->
    <nav aria-label="breadcrumb" class="mb-4">
      <ol class="breadcrumb">
        <li class="breadcrumb-item">
          <router-link to="/" class="text-decoration-none">Customers</router-link>
        </li>
        <li class="breadcrumb-item active" aria-current="page">
          <span v-if="isNew">New Customer</span>
          <span v-else-if="editMode">Edit Customer</span>
          <span v-else>Customer Details</span>
        </li>
      </ol>
    </nav>

    <!-- Alert Messages -->
    <div v-if="error" class="alert alert-danger alert-dismissible fade show" role="alert">
      {{ error }}
      <button type="button" class="btn-close" @click="$store.dispatch('clearMessages')"></button>
    </div>
    
    <div v-if="success" class="alert alert-success alert-dismissible fade show" role="alert">
      {{ success }}
      <button type="button" class="btn-close" @click="$store.dispatch('clearMessages')"></button>
    </div>

    <div class="row">
      <!-- Main Content -->
      <div class="col-lg-8">
        <div class="card">
          <div class="card-header">
            <div class="row align-items-center">
              <div class="col">
                <h5 class="card-title mb-0">
                  <span v-if="isNew">New Customer</span>
                  <span v-else-if="editMode">Edit Customer</span>
                  <span v-else>Customer Information</span>
                </h5>
              </div>
              <div class="col-auto">
                <div class="btn-group" v-if="!isNew && !editMode">
                  <router-link 
                    :to="`/customer/${customer?.id}/edit`" 
                    class="btn btn-primary btn-sm">
                    <i class="bi bi-pencil"></i> Edit
                  </router-link>
                  <button 
                    class="btn btn-outline-danger btn-sm"
                    @click="confirmDelete">
                    <i class="bi bi-trash"></i> Delete
                  </button>
                </div>
              </div>
            </div>
          </div>
          <div class="card-body">
            <!-- View Mode -->
            <div v-if="!editMode && !isNew && customer" class="customer-view">
              <div class="row g-3">
                <div class="col-md-6">
                  <label class="form-label fw-semibold">First Name</label>
                  <p class="form-control-plaintext">{{ customer.firstName }}</p>
                </div>
                <div class="col-md-6">
                  <label class="form-label fw-semibold">Last Name</label>
                  <p class="form-control-plaintext">{{ customer.lastName }}</p>
                </div>
                <div class="col-md-6">
                  <label class="form-label fw-semibold">Email</label>
                  <p class="form-control-plaintext">
                    <a v-if="customer.email" :href="`mailto:${customer.email}`" class="text-decoration-none">
                      {{ customer.email }}
                    </a>
                    <span v-else class="text-muted">Not provided</span>
                  </p>
                </div>
                <div class="col-md-6">
                  <label class="form-label fw-semibold">Phone</label>
                  <p class="form-control-plaintext">
                    <a v-if="customer.phone" :href="`tel:${customer.phone}`" class="text-decoration-none">
                      {{ customer.phone }}
                    </a>
                    <span v-else class="text-muted">Not provided</span>
                  </p>
                </div>
                <div class="col-12">
                  <label class="form-label fw-semibold">Address</label>
                  <p class="form-control-plaintext">
                    <span v-if="customer.address">{{ customer.address }}</span>
                    <span v-else class="text-muted">Not provided</span>
                  </p>
                </div>
                <div class="col-md-6">
                  <label class="form-label fw-semibold">Created</label>
                  <p class="form-control-plaintext">
                    <small class="text-muted">{{ formatDate(customer.createdAt) }}</small>
                  </p>
                </div>
                <div class="col-md-6">
                  <label class="form-label fw-semibold">Last Updated</label>
                  <p class="form-control-plaintext">
                    <small class="text-muted">{{ formatDate(customer.updatedAt) }}</small>
                  </p>
                </div>
              </div>
            </div>

            <!-- Edit/Create Mode -->
            <form v-else @submit.prevent="handleSubmit" class="customer-form">
              <div class="row g-3">
                <div class="col-md-6">
                  <label for="firstName" class="form-label">First Name *</label>
                  <input 
                    type="text" 
                    class="form-control" 
                    id="firstName"
                    v-model="form.firstName"
                    :class="{ 'is-invalid': validationErrors.firstName }"
                    required>
                  <div v-if="validationErrors.firstName" class="invalid-feedback">
                    {{ validationErrors.firstName }}
                  </div>
                </div>
                <div class="col-md-6">
                  <label for="lastName" class="form-label">Last Name *</label>
                  <input 
                    type="text" 
                    class="form-control" 
                    id="lastName"
                    v-model="form.lastName"
                    :class="{ 'is-invalid': validationErrors.lastName }"
                    required>
                  <div v-if="validationErrors.lastName" class="invalid-feedback">
                    {{ validationErrors.lastName }}
                  </div>
                </div>
                <div class="col-md-6">
                  <label for="email" class="form-label">Email</label>
                  <input 
                    type="email" 
                    class="form-control" 
                    id="email"
                    v-model="form.email"
                    :class="{ 'is-invalid': validationErrors.email }">
                  <div v-if="validationErrors.email" class="invalid-feedback">
                    {{ validationErrors.email }}
                  </div>
                </div>
                <div class="col-md-6">
                  <label for="phone" class="form-label">Phone</label>
                  <input 
                    type="tel" 
                    class="form-control" 
                    id="phone"
                    v-model="form.phone"
                    :class="{ 'is-invalid': validationErrors.phone }">
                  <div v-if="validationErrors.phone" class="invalid-feedback">
                    {{ validationErrors.phone }}
                  </div>
                </div>
                <div class="col-12">
                  <label for="address" class="form-label">Address</label>
                  <textarea 
                    class="form-control" 
                    id="address"
                    v-model="form.address"
                    :class="{ 'is-invalid': validationErrors.address }"
                    rows="3"
                    placeholder="Enter full address"></textarea>
                  <div v-if="validationErrors.address" class="invalid-feedback">
                    {{ validationErrors.address }}
                  </div>
                </div>
                <div class="col-12">
                  <div class="btn-group" role="group">
                    <button type="submit" class="btn btn-primary" :disabled="$store.getters.isLoading">
                      <span v-if="$store.getters.isLoading" class="spinner-border spinner-border-sm me-2"></span>
                      <span v-if="isNew">Create Customer</span>
                      <span v-else>Update Customer</span>
                    </button>
                    <router-link 
                      :to="isNew ? '/' : `/customer/${customer?.id}`" 
                      class="btn btn-outline-secondary">
                      Cancel
                    </router-link>
                  </div>
                </div>
              </div>
            </form>
          </div>
        </div>
      </div>

      <!-- Sidebar -->
      <div class="col-lg-4">
        <!-- Quick Actions -->
        <div class="card mb-4" v-if="!isNew && customer">
          <div class="card-header">
            <h6 class="card-title mb-0">Quick Actions</h6>
          </div>
          <div class="card-body">
            <div class="d-grid gap-2">
              <router-link 
                v-if="!editMode"
                :to="`/customer/${customer.id}/edit`" 
                class="btn btn-outline-primary btn-sm">
                <i class="bi bi-pencil"></i> Edit Customer
              </router-link>
              <a 
                v-if="customer.email" 
                :href="`mailto:${customer.email}`" 
                class="btn btn-outline-info btn-sm">
                <i class="bi bi-envelope"></i> Send Email
              </a>
              <a 
                v-if="customer.phone" 
                :href="`tel:${customer.phone}`" 
                class="btn btn-outline-success btn-sm">
                <i class="bi bi-telephone"></i> Call
              </a>
              <button 
                class="btn btn-outline-secondary btn-sm"
                @click="loadAuditHistory">
                <i class="bi bi-clock-history"></i> View History
              </button>
            </div>
          </div>
        </div>

        <!-- Audit History -->
        <div class="card" v-if="!isNew && customer && auditLogs.length > 0">
          <div class="card-header">
            <h6 class="card-title mb-0">Recent Changes</h6>
          </div>
          <div class="card-body p-0">
            <div class="list-group list-group-flush">
              <div 
                v-for="audit in auditLogs.slice(0, 5)" 
                :key="audit.id"
                class="list-group-item">
                <div class="d-flex justify-content-between">
                  <span class="badge" :class="getActionBadgeClass(audit.action)">
                    {{ audit.action }}
                  </span>
                  <small class="text-muted">{{ formatDate(audit.changedAt) }}</small>
                </div>
                <div class="mt-1">
                  <small class="text-muted">by {{ audit.changedBy }}</small>
                </div>
              </div>
            </div>
            <div class="card-footer text-center">
              <router-link to="/audit" class="btn btn-sm btn-outline-primary">
                View All Changes
              </router-link>
            </div>
          </div>
        </div>
      </div>
    </div>

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
            <p>Are you sure you want to delete <strong>{{ customer?.firstName }} {{ customer?.lastName }}</strong>?</p>
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
  name: 'CustomerDetail',
  
  props: {
    id: {
      type: [String, Number],
      default: null
    },
    editMode: {
      type: Boolean,
      default: false
    },
    isNew: {
      type: Boolean,
      default: false
    }
  },
  
  data() {
    return {
      form: {
        firstName: '',
        lastName: '',
        email: '',
        phone: '',
        address: ''
      },
      validationErrors: {}
    }
  },
  
  computed: {
    ...mapGetters(['currentCustomer', 'auditLogs', 'error', 'success']),
    
    customer() {
      return this.currentCustomer
    }
  },
  
  async mounted() {
    if (!this.isNew && this.id) {
      try {
        await this.$store.dispatch('fetchCustomer', this.id)
        if (this.editMode && this.customer) {
          this.populateForm()
        }
      } catch (error) {
        this.$router.push('/')
      }
    }
  },
  
  watch: {
    customer: {
      handler(newCustomer) {
        if (newCustomer && this.editMode) {
          this.populateForm()
        }
      },
      immediate: true
    }
  },
  
  methods: {
    populateForm() {
      if (this.customer) {
        this.form = {
          firstName: this.customer.firstName || '',
          lastName: this.customer.lastName || '',
          email: this.customer.email || '',
          phone: this.customer.phone || '',
          address: this.customer.address || ''
        }
      }
    },
    
    validateForm() {
      this.validationErrors = {}
      
      if (!this.form.firstName?.trim()) {
        this.validationErrors.firstName = 'First name is required'
      } else if (this.form.firstName.length > 100) {
        this.validationErrors.firstName = 'First name must not exceed 100 characters'
      }
      
      if (!this.form.lastName?.trim()) {
        this.validationErrors.lastName = 'Last name is required'
      } else if (this.form.lastName.length > 100) {
        this.validationErrors.lastName = 'Last name must not exceed 100 characters'
      }
      
      if (this.form.email && this.form.email.length > 255) {
        this.validationErrors.email = 'Email must not exceed 255 characters'
      }
      
      if (this.form.phone && this.form.phone.length > 20) {
        this.validationErrors.phone = 'Phone must not exceed 20 characters'
      }
      
      if (this.form.address && this.form.address.length > 500) {
        this.validationErrors.address = 'Address must not exceed 500 characters'
      }
      
      return Object.keys(this.validationErrors).length === 0
    },
    
    async handleSubmit() {
      if (!this.validateForm()) {
        return
      }
      
      try {
        if (this.isNew) {
          const newCustomer = await this.$store.dispatch('createCustomer', this.form)
          this.$router.push(`/customer/${newCustomer.id}`)
        } else {
          await this.$store.dispatch('updateCustomer', {
            id: this.customer.id,
            customerData: this.form
          })
          if (!this.editMode) {
            this.$router.push(`/customer/${this.customer.id}`)
          } else {
            // Stay in edit mode but show success message
          }
        }
      } catch (error) {
        console.error('Failed to save customer:', error)
      }
    },
    
    confirmDelete() {
      const modal = new window.bootstrap.Modal(this.$refs.deleteModal)
      modal.show()
    },
    
    async deleteCustomer() {
      try {
        await this.$store.dispatch('deleteCustomer', this.customer.id)
        
        // Hide modal
        const modal = window.bootstrap.Modal.getInstance(this.$refs.deleteModal)
        modal.hide()
        
        // Navigate back to list
        this.$router.push('/')
        
      } catch (error) {
        console.error('Failed to delete customer:', error)
      }
    },
    
    async loadAuditHistory() {
      if (this.customer) {
        await this.$store.dispatch('fetchCustomerAudit', {
          customerId: this.customer.id,
          page: 0,
          size: 10
        })
      }
    },
    
    formatDate(dateString) {
      if (!dateString) return '—'
      return moment(dateString).format('MMM D, YYYY [at] h:mm A')
    },
    
    getActionBadgeClass(action) {
      switch (action) {
        case 'INSERT': return 'bg-success'
        case 'UPDATE': return 'bg-warning text-dark'
        case 'DELETE': return 'bg-danger'
        default: return 'bg-secondary'
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

.card {
  box-shadow: 0 0.125rem 0.25rem rgba(0, 0, 0, 0.075);
}

.list-group-item {
  border-left: none;
  border-right: none;
}

.list-group-item:first-child {
  border-top: none;
}

.list-group-item:last-child {
  border-bottom: none;
}

@media (max-width: 768px) {
  .btn-group {
    flex-direction: column;
    gap: 0.5rem;
  }
  
  .btn-group > * {
    width: 100%;
  }
}
</style>
