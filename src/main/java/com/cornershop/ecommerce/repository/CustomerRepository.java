package com.cornershop.ecommerce.repository;

import com.cornershop.ecommerce.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Long, Customer> {
}
