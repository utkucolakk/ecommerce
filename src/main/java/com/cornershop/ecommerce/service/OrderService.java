package com.cornershop.ecommerce.service;

import com.cornershop.ecommerce.dto.OrderProductInfo;
import com.cornershop.ecommerce.dto.OrderRequest;
import com.cornershop.ecommerce.exception.CustomerNotFoundException;
import com.cornershop.ecommerce.exception.ProductNotFoundException;
import com.cornershop.ecommerce.model.Customer;
import com.cornershop.ecommerce.model.Order;
import com.cornershop.ecommerce.model.Product;
import com.cornershop.ecommerce.repository.CustomerRepository;
import com.cornershop.ecommerce.repository.OrderRepository;
import com.cornershop.ecommerce.repository.ProductRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
@Slf4j
public class OrderService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private CustomerRepository customerRepository;

    @Value("${spring.mail.username}")
    private String emailFrom;

    private void productUnitStockCheck(List<OrderProductInfo> orderProductInfoList) {
        orderProductInfoList.forEach(productInfo -> {
             Product product = productRepository.findById(productInfo.getProductId())
                    .orElseThrow(() -> new ProductNotFoundException("product not found id : " + productInfo.getProductId()));

            if (product.getUnitsInStock() - productInfo.getQuantity() < 0) {
                log.error("the product stock insufficient id : " + productInfo.getProductId());
                throw new RuntimeException("the product stock insufficient productName " + product.getName());
            }
        });

    }
    public boolean doOrder(OrderRequest orderRequest) {
        log.info("Order request time {} customer :{}", LocalDateTime.now(), orderRequest.getCustomerId());
        productUnitStockCheck(orderRequest.getOrderList());
        List<Double> orderTotalCostList = new ArrayList<>();
        orderRequest.getOrderList().forEach(orderRequestInfo -> {
            Order order = new Order();
            Product product = productRepository.getProductById(orderRequestInfo.getProductId()).orElseThrow(() -> new ProductNotFoundException("product not found id : " + orderRequestInfo.getProductId()));
            Double totalPrice = orderRequestInfo.getQuantity() * product.getPrice();
            orderTotalCostList.add(totalPrice);
            order.setTotalPrice(totalPrice);

            order.setQuantity(orderRequestInfo.getQuantity());
            order.setProductId(orderRequestInfo.getProductId());
            order.setCustomerId(orderRequest.getCustomerId());
            order.setPurchaseDate(LocalDate.now());
            order.setPrice(product.getPrice() );
            if (product.getUnitsInStock() - orderRequestInfo.getQuantity() == 0) {
                product.setActive(false);
            }

            orderRepository.save(order);

            product.setUnitsInStock(product.getUnitsInStock() - orderRequestInfo.getQuantity());
            productRepository.save(product);
        });

        Customer customer = customerRepository.findById(orderRequest.getCustomerId())
                .orElseThrow(() -> new CustomerNotFoundException(orderRequest.getCustomerId() + " customer not found!"));


        Double orderTotalCost = orderTotalCostList.stream().mapToDouble(Double::doubleValue).sum();
        sendMail(customer.getEmail(), customer.getFirstName(), orderTotalCost);
        return true;
    }

    //TODO: kullanıcı register olduğunda da hoşgeldin maili atılsın.
    //NOTE: you can use Mustache library.
    public void sendMail(String emailTo, String firstName, double totalCost) {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        try {
            helper.setFrom(emailFrom, "CornerShop");
            helper.setTo(emailTo);
            helper.setSubject("Hello" + firstName + " Your Order is in progress");
            String content = "<p>" + "Hello " + firstName + "</p><p>The total cost is " +totalCost + "</p>";

            helper.setText(content, true);
            mailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
