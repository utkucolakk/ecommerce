package com.cornershop.ecommerce.service;

import com.cornershop.ecommerce.dto.OrderProductInfo;
import com.cornershop.ecommerce.dto.OrderRequest;
import com.cornershop.ecommerce.helper.ProductDoFactory;
import com.cornershop.ecommerce.model.Customer;
import com.cornershop.ecommerce.repository.CustomerRepository;
import com.cornershop.ecommerce.repository.OrderRepository;
import com.cornershop.ecommerce.repository.ProductRepository;
import jakarta.mail.internet.MimeMessage;
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

    private ProductDoFactory productDoFactory;


    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        this.productDoFactory = new ProductDoFactory();

    }

    @Test
    public void doOrder_success() {
        Long productId = 3L;
        Long customerId = 5L;
        int quantity = 2;

        OrderRequest orderRequest = new OrderRequest();
        OrderProductInfo orderProductInfo = new OrderProductInfo();
        orderProductInfo.setQuantity(quantity);
        orderProductInfo.setProductId(productId);
        orderRequest.setOrderList(List.of(orderProductInfo));
        orderRequest.setCustomerId(customerId);

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




        assertTrue(response); // Same  ->  assertEquals(true, response);
        verify(orderRepository, times(1)).save(any());
        verify(productRepository, times(1)).save(any());
        verify(javaMailSender).send(mimeMessage);
        verify(productRepository, times(2)).findById(productId);
    }

    //TODO: productUnitStockCheck methodunda 2 adet daha test yazılacak biri ProductNotFoundException diğeri ise RuntimeException
}
