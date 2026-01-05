package com.example.demoStripe.controllers;

import com.example.demoStripe.services.ISellerService;
import com.stripe.exception.StripeException;
import com.stripe.model.v2.core.Account;
import com.stripe.model.v2.core.AccountLink;
import jakarta.websocket.server.PathParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/seller")
@Slf4j
public class SellerController {
    private final ISellerService sellerService;

    public SellerController(
            ISellerService sellerService
    ) {
        this.sellerService = sellerService;
    }

    @PostMapping()
    public ResponseEntity<?> createAccount(@RequestParam String email, @RequestParam String country) throws StripeException {
        Pair<Account, AccountLink> pair = this.sellerService.createAccount(email, country);
        return ResponseEntity.ok(Map.of(
                "stripeAccountId", pair.getFirst().getId(),
                "onboardingUrl", pair.getSecond().getUrl()
            )
        );
    }

    @GetMapping("/account-status/{sellerAccountId}")
    public ResponseEntity<?> accountStatus(@PathParam("sellerAccountId") String sellerAccountId) throws StripeException {
        Map<String, Object> body = this.sellerService.accountStatus(sellerAccountId);
        return ResponseEntity.ok(body);
    }
}
