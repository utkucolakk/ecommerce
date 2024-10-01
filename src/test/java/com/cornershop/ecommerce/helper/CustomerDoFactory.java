package com.cornershop.ecommerce.helper;

import com.cornershop.ecommerce.model.Customer;

public class CustomerDoFactory {

    public Customer getCustomer() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setEmail("test@gmail.com");
        customer.setFirstName("test");
        customer.setRoles("ROLE_USER");

        return customer;
    }
}