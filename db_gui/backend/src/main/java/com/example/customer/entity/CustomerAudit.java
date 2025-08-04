package com.example.customer.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "customer_audit", schema = "audit")
public class CustomerAudit {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "customer_id", nullable = false)
    private Long customerId;
    
    @Column(name = "action", nullable = false, length = 20)
    private String action; // INSERT, UPDATE, DELETE
    
    @Column(name = "changed_by", nullable = false, length = 100)
    private String changedBy;
    
    @Column(name = "changed_at", nullable = false)
    private LocalDateTime changedAt;
    
    @Column(name = "old_values", columnDefinition = "jsonb")
    private String oldValues;
    
    @Column(name = "new_values", columnDefinition = "jsonb")
    private String newValues;
    
    // Constructors
    public CustomerAudit() {}
    
    public CustomerAudit(Long customerId, String action, String changedBy, 
                        LocalDateTime changedAt, String oldValues, String newValues) {
        this.customerId = customerId;
        this.action = action;
        this.changedBy = changedBy;
        this.changedAt = changedAt;
        this.oldValues = oldValues;
        this.newValues = newValues;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    
    public String getChangedBy() { return changedBy; }
    public void setChangedBy(String changedBy) { this.changedBy = changedBy; }
    
    public LocalDateTime getChangedAt() { return changedAt; }
    public void setChangedAt(LocalDateTime changedAt) { this.changedAt = changedAt; }
    
    public String getOldValues() { return oldValues; }
    public void setOldValues(String oldValues) { this.oldValues = oldValues; }
    
    public String getNewValues() { return newValues; }
    public void setNewValues(String newValues) { this.newValues = newValues; }
    
    @Override
    public String toString() {
        return "CustomerAudit{" +
                "id=" + id +
                ", customerId=" + customerId +
                ", action='" + action + '\'' +
                ", changedBy='" + changedBy + '\'' +
                ", changedAt=" + changedAt +
                ", oldValues='" + oldValues + '\'' +
                ", newValues='" + newValues + '\'' +
                '}';
    }
}
