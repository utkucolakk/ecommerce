package com.cornershop.ecommerce.dto;

import jakarta.persistence.Lob;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginDto {
    private String token;
    private Long customerId;
}
