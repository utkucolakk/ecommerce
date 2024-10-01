package com.cornershop.ecommerce.service;

import com.cornershop.ecommerce.exception.ProductNotFoundException;
import com.cornershop.ecommerce.helper.ProductDOFactory;
import com.cornershop.ecommerce.model.Product;
import com.cornershop.ecommerce.repository.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    private ProductDOFactory productDOFactory;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        this.productDOFactory = new ProductDOFactory();
    }

    @Test
    void createProduct_successful() {
        MockMultipartFile firstFile = new MockMultipartFile("data", "filename.txt", "text/plain", "some xml".getBytes());
        Product product = new Product();
        product.setActive(true);
        product.setUnitsInStock(5L);
        product.setName("macbook");
        product.setPrice(5000D);
        product.setCategoryId(1L);
        product.setImage("uploads/macbook.txt");


        Product savedProduct = new Product();
        savedProduct.setId(1L);
        savedProduct.setActive(true);
        savedProduct.setUnitsInStock(5L);
        savedProduct.setName("macbook");
        savedProduct.setPrice(5000D);
        savedProduct.setCategoryId(1L);
        savedProduct.setImage("uploads/macbook.txt");

        when(productRepository.save(product)).thenReturn(savedProduct);

        Product response = productService.createProduct(firstFile, product);
        assertEquals(response.getName(), savedProduct.getName());
        assertEquals(response.getId(), savedProduct.getId());
        assertEquals(response.getImage(), savedProduct.getImage());
        verify(productRepository, times(1)).save(product);
        verify(productRepository, times(0)).findById(product.getId());
    }

    //TODO: createProduct else kısmı için test yaz.

    @Test
    void createProductUpdate_successful() {
        MockMultipartFile file = null;
        Product product = new Product();
        product.setId(1L);
        product.setActive(true);
        product.setUnitsInStock(5L);
        product.setName("macbook");
        product.setPrice(50002D);
        product.setCategoryId(1L);
        product.setImage("uploads/macbook.txt");

        Product savedProduct = new Product();
        savedProduct.setId(1L);
        savedProduct.setActive(true);
        savedProduct.setUnitsInStock(5L);
        savedProduct.setName("macbook");
        savedProduct.setPrice(5000D);
        savedProduct.setCategoryId(1L);
        savedProduct.setImage("uploads/macbook.txt");

        when(productRepository.findById(product.getId())).thenReturn(Optional.of(savedProduct));
        when(productRepository.save(product)).thenReturn(product);

        Product response = productService.createProduct(file, product);

        assertEquals(product.getImage(), response.getImage());
        assertEquals(product.getPrice(), response.getPrice());
        verify(productRepository, times(1)).findById(product.getId());
        verify(productRepository, times(1)).save(product);
    }

    //TODO: createProduct methodundaki "else" kısmı için new ProductNotFoundExceptiom hatası fırlatacak şekilde fail case testi yazın.

    @Test
    void createProductUpdate_fail() {
        MockMultipartFile file = null;
        Product product = new Product();
        product.setId(1L);

        when(productRepository.findById(product.getId())).thenReturn(Optional.empty());

        ProductNotFoundException thrown = Assertions.assertThrows(ProductNotFoundException.class,
                () -> productService.createProduct(file, product));

        assertEquals("product not found id : 1", thrown.getMessage());
        verify(productRepository, times(1)).findById(product.getId());
        verify(productRepository, times(0)).save(product);
    }

    @Test
    void deleteProduct_success() throws IOException {
        String filePath = "uploads/test.txt";
        File file =  new File(filePath);
        file.createNewFile();
        Long id = 1L;
        Product product = new Product();
        product.setId(id);
        product.setImage(filePath);
        when(productRepository.findById(id)).thenReturn(Optional.of(product));
        Mockito.doNothing().when(productRepository).deleteById(id);

        productService.deleteProduct(id);

        verify(productRepository, times(1)).findById(id);
        verify(productRepository, times(1)).deleteById(id);
    }

    @Test
    void getProductListByCategoryId_successful() {
        Long categoryId = 1L;
        List<Product> productList = productDOFactory.getProductListWithId(categoryId);

        when(productRepository.findProductListByCategoryId(categoryId)).thenReturn(productList);

        List<Product> response = productService.getProductListByCategoryId(categoryId);

        verify(productRepository, times(1)).findProductListByCategoryId(categoryId);
        assertEquals(categoryId, response.get(0).getCategoryId());
        assertEquals(categoryId, response.get(1).getCategoryId());
        assertEquals(productList.size(), response.size());
        assertEquals(productList.get(0).getPrice(), response.get(0).getPrice());
        assertEquals(productList.get(1).getPrice(), response.get(1).getPrice());
        assertEquals(productList.get(1).getImage(), response.get(1).getImage());
        assertEquals(productList.get(1).getUnitsInStock(), response.get(1).getUnitsInStock());
    }

    @Test
    void getProduct_successful() {
        Long productId = 1L;
        Product product = productDOFactory.getProductWithId(productId);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product)); // Optional<Product>

        Product response = productService.getProduct(productId);

        assertEquals(product.getPrice(), response.getPrice());
        assertEquals(product.getUnitsInStock(), response.getUnitsInStock());
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    void getProduct_fail() {
        Long productId = 1L;

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        ProductNotFoundException thrown = Assertions.assertThrows(ProductNotFoundException.class,
                () -> productService.getProduct(productId));

        assertEquals("Product Not Found id : "+productId, thrown.getMessage());
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    void activeOrDeActiveProduct_successful() {
        Long productId = 1L;
        boolean isActive = true;

        Mockito.doNothing().when(productRepository).updateProductActive(isActive, productId);

        productService.activeOrDeActiveProduct(productId, isActive);

        verify(productRepository, times(1)).updateProductActive(isActive, productId);
    }

    @Test
    void deleteProduct_fail() {
        Long productId = 1L;
        Product product = productDOFactory.getProductWithId(productId);
        product.setImage("test");

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        RuntimeException thrown = Assertions.assertThrows(RuntimeException.class,
                () -> productService.deleteProduct(productId));

        assertEquals("IO exception while deleting image of " + product.getName(), thrown.getMessage());
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, times(0)).deleteById(productId);
    }

    @Test
    void getAllProductList_successful() {
        Long categoryId = 3L;
        List<Product> productList = productDOFactory.getProductListWithId(categoryId);

        when(productRepository.getAllProductList()).thenReturn(productList);

        List<Product> response = productService.getAllProductList();

        assertEquals(productList.size(), response.size());
        assertEquals(productList.get(0).getPrice(), response.get(0).getPrice());
        verify(productRepository, times(1)).getAllProductList();
    }
}
