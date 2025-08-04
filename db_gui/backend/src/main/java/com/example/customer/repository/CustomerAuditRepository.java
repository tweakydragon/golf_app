package com.example.customer.repository;

import com.example.customer.entity.CustomerAudit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerAuditRepository extends JpaRepository<CustomerAudit, Long> {
    
    // Find audit history for a specific customer
    Page<CustomerAudit> findByCustomerIdOrderByChangedAtDesc(Long customerId, Pageable pageable);
    
    // Find recent changes (last N changes)
    @Query("SELECT ca FROM CustomerAudit ca ORDER BY ca.changedAt DESC")
    Page<CustomerAudit> findRecentChanges(Pageable pageable);
    
    // Find changes by action type
    Page<CustomerAudit> findByActionOrderByChangedAtDesc(String action, Pageable pageable);
    
    // Find changes by user
    Page<CustomerAudit> findByChangedByOrderByChangedAtDesc(String changedBy, Pageable pageable);
}
