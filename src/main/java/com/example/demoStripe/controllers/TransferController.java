package com.example.demoStripe.controllers;

import com.stripe.exception.StripeException;
import com.stripe.model.Transfer;
import com.stripe.param.TransferCreateParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/transfer")
@Slf4j
public class TransferController {

    @PostMapping()
    public ResponseEntity<?> bonus(@RequestParam String sellerAccountId, @RequestParam Long amount, @RequestParam String currency) throws StripeException {
        TransferCreateParams params =
                TransferCreateParams.builder()
                        .setAmount(amount)
                        .setCurrency(currency)
                        .setDestination(sellerAccountId)
                        .setDescription("Platform bonus")
                        .build();

        Transfer transfer = Transfer.create(params);
        return ResponseEntity.ok(
                Map.of("transferId", transfer.getId())
        );
    }
}
