package com.cornershop.ecommerce.controller;

import com.cornershop.ecommerce.model.Product;
import com.cornershop.ecommerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping(value = "/create", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Product> createProduct(@RequestPart("file") MultipartFile file,
                                                 @RequestPart("product") Product product) {

        return new ResponseEntity<>(productService.createProduct(file, product), HttpStatus.CREATED);

    }

    @GetMapping("/category/{categoryId}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<List<Product>> getProductList(@PathVariable(value = "categoryId") Long categoryId) {
        return new ResponseEntity<>(productService.getProductListByCategoryId(categoryId), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<Product> getProduct(@PathVariable(value = "id") Long id){
        return new ResponseEntity<>(productService.getProduct(id), HttpStatus.OK);
    }

    @PutMapping(value = "/update", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Product> updateProduct(@RequestPart(value = "file", required = false)MultipartFile file,
                                                 @ModelAttribute Product product) {

        return new ResponseEntity<>(productService.createProduct(file, product), HttpStatus.OK);
    }

    @PutMapping("active/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> activeProduct(@PathVariable("id") Long id) {
        productService.activeOrDeActiveProduct(id, true);
        return new ResponseEntity<>(HttpStatus.OK);

    }

    @PutMapping("deActive/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deActiveProduct(@PathVariable("id") Long id) {
        productService.activeOrDeActiveProduct(id, false);
        return new ResponseEntity<>(HttpStatus.OK);

    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable("id") Long id) {
        productService.deleteProduct(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<Product>> getAllProductList() {
        return new ResponseEntity<>(productService.getAllProductList(),HttpStatus.OK);
    }
}
