package com.cornershop.ecommerce.helper;

import com.cornershop.ecommerce.model.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductDOFactory {

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

    public List<Product> getProductListWithId(Long categoryId) {
        Product product1 = new Product();
        product1.setId(1L);
        product1.setActive(true);
        product1.setUnitsInStock(5L);
        product1.setName("macbook");
        product1.setPrice(50002D);
        product1.setCategoryId(categoryId);
        product1.setImage("uploads/macbook.png");

        Product product2 = new Product();
        product2.setId(2L);
        product2.setActive(true);
        product2.setUnitsInStock(2L);
        product2.setName("iphone");
        product2.setPrice(390D);
        product2.setCategoryId(categoryId);
        product2.setImage("uploads/iphone.png");

        List<Product> productList = new ArrayList<>();

        productList.add(product1);
        productList.add(product2);

        return productList;
    }
}
