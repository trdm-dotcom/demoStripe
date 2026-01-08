package com.example.demoStripe.services;

import java.util.List;
import java.util.Map;

public interface IPaymentService {
    Map<String, String> createPayment(Long amount, String currency, String sellerAccountId, String customerId, List<String> paymentMethod);
    Map<String, String> payout(String sellerAccountId, Long amount, String currency);
    Map<String, String> refund(String paymentIntentId, Long amount, String currency, String customerId);
}
