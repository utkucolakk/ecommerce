package com.cornershop.ecommerce.exception;

public class CategoryDuplicateException  extends RuntimeException{

    public CategoryDuplicateException(String message) {
        super(message);
    }
}
