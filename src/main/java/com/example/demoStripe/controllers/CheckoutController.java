package com.example.demoStripe.controllers;

import com.example.demoStripe.services.ICheckoutService;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/checkout")
@Slf4j
public class CheckoutController {
    private final ICheckoutService checkoutService;

    public CheckoutController(
            ICheckoutService checkoutService
    ) {
        this.checkoutService = checkoutService;
    }

    @PostMapping("/session")
    public ResponseEntity<?> createCheckoutSession(
            @RequestParam(required = false) String customerId,
            @RequestParam String sellerAccountId,
            @RequestBody List<Map<String, Object>> lineItem
    ) throws StripeException {
        Session session = this.checkoutService.createCheckoutSession(customerId, sellerAccountId, lineItem);
        return ResponseEntity.ok(Map.of(
                "url", session.getUrl()
        ));
    }


}
