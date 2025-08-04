package com.example.customer.service;

import com.example.customer.entity.Customer;
import com.example.customer.entity.CustomerAudit;
import com.example.customer.repository.CustomerRepository;
import com.example.customer.repository.CustomerAuditRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class CustomerService {
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private CustomerAuditRepository customerAuditRepository;
    
    // Customer CRUD Operations
    
    public Page<Customer> getAllCustomers(Pageable pageable) {
        return customerRepository.findAll(pageable);
    }
    
    public Optional<Customer> getCustomerById(Long id) {
        return customerRepository.findById(id);
    }
    
    public Customer createCustomer(Customer customer) {
        // Validation could be added here
        return customerRepository.save(customer);
    }
    
    public Customer updateCustomer(Long id, Customer updatedCustomer) {
        return customerRepository.findById(id)
            .map(customer -> {
                customer.setFirstName(updatedCustomer.getFirstName());
                customer.setLastName(updatedCustomer.getLastName());
                customer.setPhone(updatedCustomer.getPhone());
                customer.setEmail(updatedCustomer.getEmail());
                customer.setAddress(updatedCustomer.getAddress());
                return customerRepository.save(customer);
            })
            .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));
    }
    
    public void deleteCustomer(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new RuntimeException("Customer not found with id: " + id);
        }
        customerRepository.deleteById(id);
    }
    
    // Search Operations
    
    public Page<Customer> searchCustomersByName(String name, Pageable pageable) {
        return customerRepository.findByNameContainingIgnoreCase(name, pageable);
    }
    
    public Page<Customer> searchCustomers(String firstName, String lastName, 
                                        String email, String phone, Pageable pageable) {
        return customerRepository.findBySearchCriteria(firstName, lastName, email, phone, pageable);
    }
    
    public Customer findByEmail(String email) {
        return customerRepository.findByEmailIgnoreCase(email);
    }
    
    public Customer findByPhone(String phone) {
        return customerRepository.findByPhone(phone);
    }
    
    // Audit Operations
    
    public Page<CustomerAudit> getCustomerAuditHistory(Long customerId, Pageable pageable) {
        return customerAuditRepository.findByCustomerIdOrderByChangedAtDesc(customerId, pageable);
    }
    
    public Page<CustomerAudit> getRecentChanges(Pageable pageable) {
        return customerAuditRepository.findRecentChanges(pageable);
    }
    
    public Page<CustomerAudit> getChangesByAction(String action, Pageable pageable) {
        return customerAuditRepository.findByActionOrderByChangedAtDesc(action, pageable);
    }
    
    public Page<CustomerAudit> getChangesByUser(String changedBy, Pageable pageable) {
        return customerAuditRepository.findByChangedByOrderByChangedAtDesc(changedBy, pageable);
    }
}
