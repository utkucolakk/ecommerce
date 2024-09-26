package com.cornershop.ecommerce.helper;

import com.cornershop.ecommerce.model.Product;

public class ProductDoFactory {

    public Product getProductWithId(Long productId) {
        Product product = new Product();
        product.setId(productId);
        product.setActive(true);
        product.setUnitsInStock(5L);
        product.setName("macbook");
        product.setPrice(50002D);
        product.setCategoryId(1L);
        product.setImage("uploads/macbook.txt");

        return product;
    }
}
