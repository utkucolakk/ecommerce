package com.cornershop.ecommerce.repository;

import com.cornershop.ecommerce.model.Product;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p WHERE p.categoryId = :categoryId")
    List<Product> findProductListByCategoryId(@Param("categoryId") Long categoryId);

    @Modifying
    @Transactional
    @Query("UPDATE Product p SET p.active = :active WHERE p.id = :id")
    void updateProductActive(@Param("active") Boolean isActive, @Param("id") Long id);

    @Query("SELECT p FROM Product p WHERE p.active = true")
    List<Product> getAllActiveProductList();

    @Query("SELECT p FROM Product p WHERE p.active = true AND p.id =:id")
    Optional<Product> getActiveProductById(@Param("id") Long id);
}
