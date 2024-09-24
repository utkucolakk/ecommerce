package com.cornershop.ecommerce.controller;
import com.cornershop.ecommerce.dto.OrderRequest;
import com.cornershop.ecommerce.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;


    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_USER')")

    public ResponseEntity<Boolean> doOrder(@RequestBody OrderRequest orderRequest) {
        return new ResponseEntity<>(orderService.doOrder(orderRequest), HttpStatus.OK);
    }

    @GetMapping("/test")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> test () {
        orderService.sendMail("", "", 0d);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
