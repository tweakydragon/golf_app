package com.example.customer.repository;

import com.example.customer.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    
    // Find customers by name (case-insensitive)
    @Query("SELECT c FROM Customer c WHERE " +
           "LOWER(c.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR " +
           "LOWER(c.lastName) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<Customer> findByNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);
    
    // Find by email
    Customer findByEmailIgnoreCase(String email);
    
    // Find by phone
    Customer findByPhone(String phone);
    
    // Search customers by multiple criteria
    @Query("SELECT c FROM Customer c WHERE " +
           "(:firstName IS NULL OR LOWER(c.firstName) LIKE LOWER(CONCAT('%', :firstName, '%'))) AND " +
           "(:lastName IS NULL OR LOWER(c.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))) AND " +
           "(:email IS NULL OR LOWER(c.email) LIKE LOWER(CONCAT('%', :email, '%'))) AND " +
           "(:phone IS NULL OR c.phone LIKE CONCAT('%', :phone, '%'))")
    Page<Customer> findBySearchCriteria(
        @Param("firstName") String firstName,
        @Param("lastName") String lastName,
        @Param("email") String email,
        @Param("phone") String phone,
        Pageable pageable
    );
}
