package com.example.demoStripe.services;

import com.stripe.model.Transfer;

import java.util.Map;

public interface ITransferService {
    Map<String, String> createTransfer(String sellerAccountId, Long amount, String currency);
}
