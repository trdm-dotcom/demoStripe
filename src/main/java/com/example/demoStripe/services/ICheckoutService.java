package com.example.demoStripe.services;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;

import java.util.List;
import java.util.Map;

public interface ICheckoutService {
    Session createCheckoutSession(String customerId, String sellerAccountId, List<Map<String, Object>> lineItem) throws StripeException;
}
