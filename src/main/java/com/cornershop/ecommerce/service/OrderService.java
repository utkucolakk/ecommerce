package com.cornershop.ecommerce.service;

import com.cornershop.ecommerce.dto.OrderProductInfo;
import com.cornershop.ecommerce.dto.OrderRequest;
import com.cornershop.ecommerce.exception.ProductNotFoundException;
import com.cornershop.ecommerce.model.Order;
import com.cornershop.ecommerce.model.Product;
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
import java.util.List;

@Service
@Slf4j
public class OrderService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private JavaMailSender mailSender;

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
        orderRequest.getOrderList().forEach(orderRequestInfo -> {
            Order order = new Order();
            Product product = productRepository.getProductById(orderRequestInfo.getProductId()).orElseThrow(() -> new ProductNotFoundException("product not found id : " + orderRequestInfo.getProductId()));
            Double totalPrice = orderRequestInfo.getQuantity() * product.getPrice();
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

        sendMail();
        return true;
    }

    public void sendMail() {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        try {
            helper.setFrom(emailFrom, "CornerShop");
            helper.setTo("x");
            helper.setSubject("Your Order is in progress");
            String content = "<p>Hello,</p><p>This is a test email sent from Spring Boot.</p>";

            helper.setText(content, true);
            mailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
