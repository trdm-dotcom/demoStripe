package com.example.demoStripe.controllers;

import com.example.demoStripe.services.ITransferService;
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
    private final ITransferService transferService;

    public TransferController(ITransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping()
    public ResponseEntity<?> createTransfer(@RequestParam String sellerAccountId, @RequestParam Long amount, @RequestParam String currency) {
        Map<String, String> result = this.transferService.createTransfer(sellerAccountId, amount, currency);
        return ResponseEntity.ok(result);
    }
}
