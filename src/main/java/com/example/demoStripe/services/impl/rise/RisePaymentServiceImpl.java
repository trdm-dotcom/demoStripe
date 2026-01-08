package com.example.demoStripe.services.impl.rise;

import com.example.demoStripe.services.IPaymentService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class RisePaymentServiceImpl implements IPaymentService {
    @Override
    public Map<String, String> createPayment(Long amount, String currency, String sellerAccountId, String customerId, List<String> paymentMethod) {
        return null;
    }

    @Override
    public Map<String, String> payout(String sellerAccountId, Long amount, String currency) {
        return null;
    }

    @Override
    public Map<String, String> refund(String paymentIntentId, Long amount, String currency, String customerId) {
        return null;
    }
}
