package com.cornershop.ecommerce.service;

import com.cornershop.ecommerce.dto.OrderProductInfo;
import com.cornershop.ecommerce.dto.OrderRequest;
import com.cornershop.ecommerce.exception.CustomerNotFoundException;
import com.cornershop.ecommerce.exception.ProductNotFoundException;
import com.cornershop.ecommerce.helper.OrderRequestDOFactory;
import com.cornershop.ecommerce.helper.ProductDOFactory;
import com.cornershop.ecommerce.model.Customer;
import com.cornershop.ecommerce.repository.CustomerRepository;
import com.cornershop.ecommerce.repository.OrderRepository;
import com.cornershop.ecommerce.repository.ProductRepository;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(properties = { "spring.mail.username=annotation" })
public class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private JavaMailSender javaMailSender;

    @Mock
    private MimeMessage mimeMessage;

    @Mock
    private MimeMessageHelper mimeMessageHelper;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private Environment environment;

    private ProductDOFactory productDoFactory;

    private OrderRequestDOFactory orderRequestDOFactory;


    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        this.productDoFactory = new ProductDOFactory();
        this.orderRequestDOFactory = new OrderRequestDOFactory();

    }

    @Test
    public void doOrder_success() {
        Long productId = 3L;
        Long customerId = 5L;
        int quantity = 2;
        OrderRequest orderRequest = orderRequestDOFactory.getOrderRequest(quantity, productId, customerId);

        Customer customer = new Customer();
        customer.setId(customerId);
        customer.setEmail("test@email.com");
        customer.setFirstName("test");

        when(productRepository.findById(productId)).thenReturn(Optional.of(productDoFactory.getProductWithId(productId)));
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        // MimeMessage ve MimeMessageHelper'ı mocklamak
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
        mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        ReflectionTestUtils.setField(orderService, "emailFrom", "test@mail.com");

        boolean response = orderService.doOrder(orderRequest);

        assertTrue(response); // Same  -> assertEquals(true, response);
        verify(orderRepository, times(1)).save(any());
        verify(productRepository, times(1)).save(any());
        verify(javaMailSender).send(mimeMessage);
        verify(productRepository, times(2)).findById(productId);
    }
    //TODO: productUnitStockCheck methodunda 2 adet daha test yazılacak biri ProductNotFoundException diğeri ise RuntimeException

    @Test
    void doOrder_fail_ProductNotFoundException() {
        Long productId = 3L;
        Long customerId = 5L;
        int quantity = 2;
        OrderRequest orderRequest = orderRequestDOFactory.getOrderRequest(quantity, productId, customerId);

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        ProductNotFoundException thrown = Assertions.assertThrows(ProductNotFoundException.class,
                () -> orderService.doOrder(orderRequest));

        assertEquals("product not found id : 3", thrown.getMessage());
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    void doOrder_fail_RuntimeException() {
        Long productId = 3L;
        Long customerId = 5L;
        int quantity = 6;
        OrderRequest orderRequest = orderRequestDOFactory.getOrderRequest(quantity, productId, customerId);

        when(productRepository.findById(productId)).thenReturn(Optional.of(productDoFactory.getProductWithId(productId)));

        RuntimeException thrown = Assertions.assertThrows(RuntimeException.class,
                () -> orderService.doOrder(orderRequest));

        assertEquals("the product stock insufficient productName : macbook", thrown.getMessage());
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    void doOrder_fail_CustomerNotFoundException() {
        Long productId = 3L;
        Long customerId = 5L;
        int quantity = 2;
        OrderRequest orderRequest = orderRequestDOFactory.getOrderRequest(quantity, productId, customerId);

        when(productRepository.findById(productId)).thenReturn(Optional.of(productDoFactory.getProductWithId(productId)));
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        CustomerNotFoundException thrown = Assertions.assertThrows(CustomerNotFoundException.class,
                () -> orderService.doOrder(orderRequest));

        assertEquals(customerId + " customer not found!", thrown.getMessage());
        verify(productRepository, times(2)).findById(productId);
        verify(orderRepository, times(1)).save(any());
        verify(productRepository, times(1)).save(any());
        verify(customerRepository, times(1)).findById(customerId);
    }
}
