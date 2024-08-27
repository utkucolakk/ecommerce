package com.cornershop.ecommerce.config;

import com.cornershop.ecommerce.model.Customer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class UserInfoDetails implements UserDetails {

    private String email;

    private String password;

    private List<GrantedAuthority> authorityList;

    public UserInfoDetails(Customer customerInfo) {
        email = customerInfo.getEmail();
        password = customerInfo.getPassword();
        authorityList = Arrays.stream(customerInfo.getRoles().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorityList;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }
}
