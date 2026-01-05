package com.example.demoStripe.controllers;

import com.example.demoStripe.services.ICustomerService;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/customer")
@Slf4j
public class CustomerController {
    private final ICustomerService customerService;

    public CustomerController(ICustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping()
    public ResponseEntity<?> createCustomer(@RequestParam String email, @RequestParam String name) throws StripeException {
        Customer customer = this.customerService.createCustomer(email, name);
        return ResponseEntity.ok().body(Map.of(
                "customerId", customer.getId()
        ));
    }
}
