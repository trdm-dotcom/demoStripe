package com.example.demoStripe.controllers;

import com.example.demoStripe.services.IPaymentService;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Payout;
import com.stripe.model.Refund;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/payment")
@Slf4j
public class PaymentController {
    private final IPaymentService paymentService;

    public PaymentController(IPaymentService paymentService) {
        this.paymentService = paymentService;
    }


    @PostMapping()
    public ResponseEntity<?> createPayment(
            @RequestParam Long amount,
            @RequestParam String currency,
            @RequestParam String sellerAccountId,
            @RequestParam(required = false) String customerId,
            @RequestParam List<String> paymentMethod
    ) throws StripeException {
        PaymentIntent intent = this.paymentService.createPayment(amount, currency, sellerAccountId, customerId, paymentMethod);
        return ResponseEntity.ok((Map.of("paymentIntentId", intent.getId(), "clientSecret", intent.getClientSecret())));
    }

    @PostMapping("payout")
    public ResponseEntity<?> payout(
            @RequestParam String sellerAccountId,
            @RequestParam Long amount,
            @RequestParam String currency
    ) throws StripeException {
        Payout payout = this.paymentService.payout(sellerAccountId, amount, currency);
        return ResponseEntity.ok(Map.of("payoutId", payout.getId()));
    }

    @PostMapping("refund")
    public ResponseEntity<?> refund(
            @RequestParam String paymentIntentId,
            @RequestParam Long amount,
            @RequestParam String currency,
            @RequestParam(required = false) String customerId
    ) throws StripeException {
        Refund refund = this.paymentService.refund(paymentIntentId, amount, currency, customerId);
        return ResponseEntity.ok(Map.of("refundId", refund.getId()));
    }
}
